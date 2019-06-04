package com.benlefevre.go4lunch.notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.benlefevre.go4lunch.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN;
import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN_CHANGED;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES,MODE_PRIVATE);
        sharedPreferences.edit().putString(MESS_TOKEN,token).apply();
        sharedPreferences.edit().putBoolean(MESS_TOKEN_CHANGED,true).apply();
        Log.i("info", "onNewToken: called");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("info", "onMessageReceived: called");
    }
}
