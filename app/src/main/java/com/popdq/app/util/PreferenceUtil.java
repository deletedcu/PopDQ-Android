package com.popdq.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.popdq.app.values.Values;

/**
 * Created by Dang Luu on 09/07/2016.
 */
public class PreferenceUtil {
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;


    public static SharedPreferences getInstancePreference(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences;
    }

    public static SharedPreferences.Editor getInstanceEditor(Context context) {
        if (editor == null) {
            editor = getInstancePreference(context).edit();
        }
        return editor;
    }

    public static String getToken(Context context) {
        return PreferenceUtil.getInstancePreference(context).getString(Values.TOKEN, "");
    }


    public static String getCategoryArr(Context context) {
        return PreferenceUtil.getInstancePreference(context).getString(Values.category_id, "");
    }

    public static long getUserId(Context context) {
        return PreferenceUtil.getInstancePreference(context).getLong(Values.my_user_id, 0);
    }

    public static String getTokenFb(Context context) {
        return PreferenceUtil.getInstancePreference(context).getString(Values.fbToken, "");
    }

    public static void setStatusApp(Context context, boolean inForeground) {
        PreferenceUtil.getInstanceEditor(context).putBoolean(Values.KEY_STATUS_APP, inForeground);
        PreferenceUtil.getInstanceEditor(context).commit();
    }

    public static boolean appInForeGround(Context context) {
        return PreferenceUtil.getInstancePreference(context).getBoolean(Values.KEY_STATUS_APP, false);
    }
}
