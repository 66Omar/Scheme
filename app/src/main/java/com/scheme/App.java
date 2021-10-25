package com.scheme;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {
    public static final String CHANNEL_ONE = "LectureChannel";
    public static final String CHANNEL_TWO = "EventChannel";
    public static final String VERSION = "dataVersion";
    public static final String SHARED_PREFS = "mainData";
    public static final String NAME = "name";
    public static final String UNIVERSITY = "university";
    public static final String FACULTY = "faculty";
    public static final String SCHOOLYEAR = "year";
    public static final String SECTION = "section";
    public static final String SEAT = "seat";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ONE, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            CharSequence name2 = getString(R.string.channel_name2);
            String description2 = getString(R.string.channel_description2);
            int importance2 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_TWO, name2, importance2);
            channel.setDescription(description2);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
