package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.values.Values;

/**
 * Created by Dang Luu on 13/07/2016.
 */
public class ExpertUtils {
    private static VolleyUtils volleyUtils;

    public static void search(Context context,String token, String name, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils = new VolleyUtils(context, Values.URL_EXPERT_SEARCH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.name, name);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void detail(Context context,String token, long experts_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils = new VolleyUtils(context, Values.URL_USER_MY_PROFILE);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, experts_id+"");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

}
