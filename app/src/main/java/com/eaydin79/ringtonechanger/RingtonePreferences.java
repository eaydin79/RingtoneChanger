package com.eaydin79.ringtonechanger;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

public class RingtonePreferences {

    private final SharedPreferences sharedPreferences;

    public RingtonePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("ChangerPreferences", 0);
    }

    public Uri getDirUri(int fileType) throws NullPointerException {
        switch (fileType) {
            case RingtoneManager.TYPE_RINGTONE: return getRingtonesDirUri();
            case RingtoneManager.TYPE_NOTIFICATION: return getNotificationsDirUri();
            case RingtoneManager.TYPE_ALARM: return getAlarmsDirUri();
        }
        throw new NullPointerException();
    }

    public void setDirUri(int fileType, Uri uri) throws NullPointerException {
        switch (fileType) {
            case RingtoneManager.TYPE_RINGTONE: setRingtonesDirUri(uri); break;
            case RingtoneManager.TYPE_NOTIFICATION: setNotificationsDirUri(uri); break;
            case RingtoneManager.TYPE_ALARM: setAlarmsDirUri(uri); break;
        }
    }

    public Uri getRingtonesDirUri() throws NullPointerException{
        return Uri.parse(sharedPreferences.getString("ringtones_dir_uri", null));
    }

    public void setRingtonesDirUri(Uri uri) {
        sharedPreferences.edit().putString("ringtones_dir_uri", uri.toString()).apply();
    }

    public Uri getNotificationsDirUri() throws NullPointerException{
        return Uri.parse(sharedPreferences.getString("notifications_dir_uri", null));
    }

    public void setNotificationsDirUri(Uri uri) {
        sharedPreferences.edit().putString("notifications_dir_uri", uri.toString()).apply();
    }

    public Uri getAlarmsDirUri() throws NullPointerException{
        return Uri.parse(sharedPreferences.getString("alarms_dir_uri", null));
    }

    public void setAlarmsDirUri(Uri uri) {
        sharedPreferences.edit().putString("alarms_dir_uri", uri.toString()).apply();
    }

}
