package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.values.Values;

/**
 * Created by Dang Luu on 21/07/2016.
 */
public class UserUtil {

//    public static

    public static void search(Context context, String token, String name,String expertCategory, int limit, int offset, String verified, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_SEARCH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.name, name);
        volleyUtils.addParam(Values.expertCategory, expertCategory);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.verified, verified + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void searchFriendFacebook(Context context, String token,String tokenFb, String name,String expertCategory, int limit, int offset, String verified, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_GET_FRIEND_FACEBOOK);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.fbToken, tokenFb);
        volleyUtils.addParam(Values.name, name);
        volleyUtils.addParam(Values.expertCategory, expertCategory);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.verified, verified + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void updateCategories(Context context, String token, String arrCategories, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_UPDATE_CATEGORIES);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.newExpertCategory, arrCategories);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getMyProfile(Context context, String token, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_GET_MY_PROFILE);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void updatePassword(Context context, String token, String oldPass, String newPass, VolleyUtils.OnRequestListenner onRequestListenner){
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_CHANGE_PASSWORD);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.password, oldPass);
        volleyUtils.addParam(Values.newPassword, newPass);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }






}
