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
    private static final String eventAlarms = "eventAlarms";


    public static void cancelAlarm(Context context, Intent intent, int notificationId, int type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        removeAlarmId(context, notificationId, type);
    }

    public static void cancelAllAlarms(Context context, Intent intent, int type) {
        for (int idAlarm : getAlarmIds(context, type)) {
            cancelAlarm(context, intent, idAlarm, type);
        }
    }

//    public static boolean hasAlarm(Context context, Intent intent, int notificationId) {
//        return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null;
//    }

    public static void saveAlarmId(Context context, int id, int type) {
        List<Integer> idsAlarms = getAlarmIds(context, type);

        if (idsAlarms.contains(id)) {
            return;
        }
        idsAlarms.add(id);

        saveIdsInPreferences(context, idsAlarms, type);
    }

    public static void removeAlarmId(Context context, int id, int type) {
        List<Integer> idsAlarms = getAlarmIds(context, type);
        idsAlarms.remove((Integer) id);
        saveIdsInPreferences(context, idsAlarms, type);
    }

    public static List<Integer> getAlarmIds(Context context, int type) {
        List<Integer> ids = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            JSONArray jsonArray2;
            if (type == 0) {
                jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + sTagAlarms, "[]"));
            }
            else {
                jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + eventAlarms, "[]"));
            }

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public static void saveIdsInPreferences(Context context, List<Integer> lstIds, int type) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : lstIds) {
            jsonArray.put(idAlarm);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (type==0) {
            editor.putString(context.getPackageName() + sTagAlarms, jsonArray.toString());
        }
        else {
            editor.putString(context.getPackageName() + eventAlarms, jsonArray.toString());
        }

        editor.apply();
    }
}