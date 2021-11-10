package com.scheme.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.scheme.MainActivity;
import com.scheme.R;

public class NotificationSender extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION_CONTENT = "notification_content";
    public static String NOTIFICATION_TITLE = "notification_title";
    public static String NOTIFICATION_CHANNEL = "notification_channel";
    private static final int ONE_WEEK = 604800000;

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        String content = intent.getStringExtra(NOTIFICATION_CONTENT);
        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String channel = intent.getStringExtra(NOTIFICATION_CHANNEL);

        Notification notification = createNotification(context, title, content, channel);

        notificationManager.notify(id, notification);

        // scheduling next alarm
        Intent notificationIntent = new Intent(context, NotificationSender.class);
        notificationIntent.putExtra(NOTIFICATION_ID, id);
        notificationIntent.putExtra(NOTIFICATION_CONTENT, content);
        notificationIntent.putExtra(NOTIFICATION_TITLE, title);
        notificationIntent.putExtra(NOTIFICATION_CHANNEL, channel);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ONE_WEEK, p);
    }

    private Notification createNotification(Context context, String title, String message, String channel) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel);
        builder.setContentTitle(title)
        .setContentText(message)
        .setWhen(System.currentTimeMillis())
        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
        .setSmallIcon(R.mipmap.ic_launcher_foreground)
        .setAutoCancel(true)
        .setPriority(Notification.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentIntent(pendingIntent);

        return builder.build();
    }
}