package com.finefrock.james.security;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by james on 12/18/17.
 */

public class DoorSensorFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "DoorSensorFirebaseServi";
    private SharedPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int alarmStatus = preferences.getInt(getString(R.string.notification_status), 0);

        if(alarmStatus == 0) {
            sendNotification(remoteMessage.getData().toString());
        } else if(alarmStatus == 2) {
            if(isInTimeRange()) {
                sendNotification(remoteMessage.getData().toString());
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getResources().getString(R.string.notification_channel))
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;

        notificationManager.notify(0 /* ID of notification */, notification);
    }

    private boolean isInTimeRange() {
        try {
            String string1 = preferences.getString(getString(R.string.notification_start_time), "0:00");
            Date time1 = new SimpleDateFormat("HH:mm").parse(string1);

            String string2 = preferences.getString(getString(R.string.notification_end_time), "0:00");
            Date time2 = new SimpleDateFormat("HH:mm").parse(string2);

            boolean switched = false;

            if(time2.before(time1)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(time2);
                cal.add(Calendar.DATE, 1); //minus number would decrement the days
                time2 = cal.getTime();
                switched = true;
            }

            Calendar calendar3 = Calendar.getInstance();
            Date x = calendar3.getTime();
            Date time3 = new SimpleDateFormat("HH:mm").parse(x.getHours() + ":" + x.getMinutes());

            if(time3.getHours() < 12 && switched) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(time3);
                cal.add(Calendar.DATE, 1); //minus number would decrement the days
                time3 = cal.getTime();
            }

            if (time3.after(time1) && time3.before(time2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }
}
