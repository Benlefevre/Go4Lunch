package com.benlefevre.go4lunch.notification;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN;
import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN_CHANGED;

public class NotificationService extends FirebaseMessagingService {

    /**
     * Called when a new Token is needed.
     * @param token the new token
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().putString(MESS_TOKEN, token).apply();
        sharedPreferences.edit().putBoolean(MESS_TOKEN_CHANGED, true).apply();
    }

    /**
     * Called when Firebase Cloud Messaging send a message.
     * According to the meesage's content, launches a notification in background with WorkManager or
     * deletes a previous notification in waiting
     * @param remoteMessage the received message by Firebase Cloud Messaging
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Context context = getApplicationContext();
        boolean notification = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(getApplicationContext().getString(R.string.notif_pref), true);
        if (notification && remoteMessage.getData() != null) {
            String type = remoteMessage.getData().get("type");
            String tag = remoteMessage.getData().get("tag");

            if (type != null && type.equals("notification")) {
                NotificationHandler.launchNotificationSend(context,tag);
            } else
                NotificationHandler.deleteNotificationWork(context,tag);
        }
    }
}

