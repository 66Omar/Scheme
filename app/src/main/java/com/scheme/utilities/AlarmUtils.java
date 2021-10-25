package com.scheme.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AlarmUtils {

    private static final String sTagAlarms = ":alarms";


    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        removeAlarmId(context, notificationId);
    }

    public static void cancelAllAlarms(Context context, Intent intent) {
        for (int idAlarm : getAlarmIds(context)) {
            cancelAlarm(context, intent, idAlarm);
        }
    }

    public static boolean hasAlarm(Context context, Intent intent, int notificationId) {
        return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void saveAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        if (idsAlarms.contains(id)) {
            return;
        }

        idsAlarms.add(id);

        saveIdsInPreferences(context, idsAlarms);
    }

    public static void removeAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        for (int i = 0; i < idsAlarms.size(); i++) {
            if (idsAlarms.get(i) == id)
                idsAlarms.remove(i);
        }

        saveIdsInPreferences(context, idsAlarms);
    }

    public static List<Integer> getAlarmIds(Context context) {
        List<Integer> ids = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            JSONArray jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + sTagAlarms, "[]"));

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public static void saveIdsInPreferences(Context context, List<Integer> lstIds) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : lstIds) {
            jsonArray.put(idAlarm);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getPackageName() + sTagAlarms, jsonArray.toString());

        editor.apply();
    }
}