package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.values.Values;

/**
 * Created by Dang Luu on 09/08/2016.
 */
public class PushNotificationUtil {

    public static void registerPush(Context context, String token, String clientId, String bundleId,  VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_REGISTER_PUSH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.clientId, clientId + "");
        volleyUtils.addParam(Values.bundleId, bundleId);
        volleyUtils.addParam(Values.type, 2+"");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }
    public static void unRegisterPush(Context context, String token, String clientId, String bundleId,  VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_UNREGISTER_PUSH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.clientId, clientId + "");
        volleyUtils.addParam(Values.bundleId, bundleId);
        volleyUtils.addParam(Values.type, 2+"");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }
}
