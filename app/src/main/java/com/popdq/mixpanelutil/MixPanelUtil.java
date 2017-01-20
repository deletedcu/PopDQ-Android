package com.popdq.mixpanelutil;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.R;
import com.popdq.app.model.Interest;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Dang Luu on 10/12/2016.
 */

public class MixPanelUtil {
    public static void trackSearchKeyword(Context context, String keyword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Values.TRACK_SEARCH_KEYWORD, keyword);
            MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).track(Values.TRACK_SEARCH_KEYWORD, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void trackCategory(Context context, Interest category) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Values.category_id, category.getId());
            jsonObject.put(Values.category_name, category.getName());
            MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).track(Values.TRACK_CATEGORY, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void trackViewAll(Context context) {
        MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).track(Values.TRACK_VIEW_ALL);
    }

    public static void trackPopThisQuestion(Context context) {
        MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).track(Values.TRACK_POP_THIS_QUESTION);
    }

    public static void trackActivity(Context context, Class clasName) {
        try {
            MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).track(clasName.getName());
        } catch (Exception e) {

        }
    }

    public static void registerPushMixpanel(Context context) {

       MixpanelAPI mMixpanel = MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL);
        MixpanelAPI.People people = mMixpanel.getPeople();
        people.identify(mMixpanel.getDistinctId());
        people.initPushHandling(context.getString(R.string.sender_id));
//        String token = "";
//        InstanceID instanceID = InstanceID.getInstance(context);
//        try {
//            try {
//                token = instanceID.getToken(context.getString(R.string.sender_id),
//                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            MixpanelAPI.People people = MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).getPeople();
//            people.identify(MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).getDistinctId());
//            people.setPushRegistrationId(token);
//        } catch (Exception e) {
//
//        }
    }

    public static void clearPushMixpanel(Context context) {
        String tokenPush = PreferenceUtil.getInstancePreference(context).getString(Values.token_push, "");
        MixpanelAPI.People people = MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).getPeople();
        people.identify(MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).getDistinctId());
        people.clearPushRegistrationId(tokenPush);
    }
}