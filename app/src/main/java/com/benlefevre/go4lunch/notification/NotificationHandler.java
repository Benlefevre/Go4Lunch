package com.benlefevre.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.controllers.activities.RestaurantActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ADDRESS;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.USER_NAME;

public class NotificationHandler extends Worker {

    private Context mContext;
    private String mUserName;
    private String mRestaurantName;
    private String mRestaurantId;
    private String mRestaurantAddress;
    private StringBuilder mWorkmates = new StringBuilder();

    public NotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        mContext = getApplicationContext();
        fetchRestaurantInfoInSharedPreferences();
        fetchRestaurantNbUserInFirestore();
        return Result.success();
    }


    /**
     * Fetches in the SharedPreferences the restaurant's name and address.
     */
    private void fetchRestaurantInfoInSharedPreferences() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        mUserName = sharedPreferences.getString(USER_NAME, " ");
        mRestaurantName = sharedPreferences.getString(CHOSEN_RESTAURANT_NAME, "");
        mRestaurantId = sharedPreferences.getString(CHOSEN_RESTAURANT_ID,"");
        mRestaurantAddress = sharedPreferences.getString(CHOSEN_RESTAURANT_ADDRESS, "");

    }

    /**
     * Fetches in Firestore the users who has chose the fetched restaurant
     */
    private void fetchRestaurantNbUserInFirestore() {
        UserHelper.getUsersCollection().whereEqualTo("restaurantName", mRestaurantName)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    if (Objects.equals(document.get("displayName"), mUserName))
                        mWorkmates.append(mContext.getString(R.string.you_notif));
                    else
                        mWorkmates.append((document.get("displayName"))).append(", ");
                }
            }
            sendNotification();
        });
    }

    /**
     * Configures a notification's channel according to the build version SDK.
     * Sets the notification's style and content
     */
    private void sendNotification() {
        Intent intent = new Intent(mContext, RestaurantActivity.class);
        intent.putExtra(RESTAURANT_NAME, mRestaurantName);
        intent.putExtra(RESTAURANT_ID,mRestaurantId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Go4Lunch notification";
        String channelName = "Go4Lunch notification";

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(mContext.getString(R.string.time_lunch))
                .setContentText(mContext.getString(R.string.lunch_at, mRestaurantName))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(mContext.getString(R.string.is_located, mRestaurantName))
                        .addLine(mRestaurantAddress)
                        .addLine(mContext.getString(R.string.you_lunch_with))
                        .addLine(mWorkmates.toString())
                        .setBigContentTitle(mContext.getString(R.string.time_lunch))
                        .setSummaryText(mContext.getString(R.string.recall)));
        if (notificationManager != null) {
            notificationManager.notify(1,notification.build());
        }
    }

    /**
     * Configures an OneTimeWorkRequest with an initialDelay and a tag.
     * @param context the application context
     * @param tag a tag that allows to add a tag to the request
     */
    static void launchNotificationSend(Context context, String tag){
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationHandler.class)
                .setInitialDelay(setDelayDuration(), TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueue(notificationWork);
    }


    /**
     * Deletes an OneTimeRequest with it's tag
     * @param context the application context
     * @param tag the request's tag to delete
     */
    static void deleteNotificationWork(Context context, String tag){
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(tag);
    }

    /**
     * Calculate the initial delay before sending the notification with the
     * @return a long corresponding at the difference between now and 12:00
     */
    private static long setDelayDuration() {
        long duration;
        Calendar now = Calendar.getInstance();
        Calendar twelve = Calendar.getInstance();
        twelve.set(Calendar.HOUR_OF_DAY,12);
        twelve.set(Calendar.MINUTE,0);

        if (now.after(twelve)){
            twelve.add(Calendar.DAY_OF_YEAR,1);
            duration = twelve.getTimeInMillis() - System.currentTimeMillis();
        } else
            duration = twelve.getTimeInMillis() - System.currentTimeMillis();
        return duration;
    }
}
