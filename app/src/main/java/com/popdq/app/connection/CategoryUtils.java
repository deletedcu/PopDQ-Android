package com.popdq.app.connection;

import android.content.Context;
import android.widget.Toast;

import com.popdq.app.interfaces.OnRequestListener;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;

import java.util.List;

/**
 * Created by Dang Luu on 9/12/2016.
 */
public class CategoryUtils {
    public static void getListCategories(Context context, String token, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_USER_GET_LIST_INTEREST);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static List<Interest> getListFromRespone(Context context, String respone) {
        List<Interest> interests = VolleyUtils.getListModelFromRespone(respone, Interest.class);
        PreferenceUtil.getInstanceEditor(context).putString(Values.category_respone, respone);
        PreferenceUtil.getInstanceEditor(context).commit();
        return interests;
    }


    public static List<Interest> getListFromLocal(Context context) {
        String respone = PreferenceUtil.getInstancePreference(context).getString(Values.category_respone, "");
        List<Interest> interests = null;
        if (respone != null)
            interests = VolleyUtils.getListModelFromRespone(respone, Interest.class);
        return interests;
    }

    public static void updateAllMyInterests(final Context context, final OnRequestListener onRequestListener) {
        getListCategories(context, PreferenceUtil.getToken(context), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    List<Interest> myInterests = VolleyUtils.getListModelFromRespone(response, Interest.class);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("[");
                    for (int i = 0; i < myInterests.size(); i++) {
                        stringBuilder.append((myInterests.get(i).getId()) + ",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.append("]");


                    VolleyUtils update = new VolleyUtils(context, Values.URL_USER_UPDATE_LIST_INTEREST);
                    update.addParam(Values.TOKEN, PreferenceUtil.getToken(context));
                    update.addParam(Values.newInterest, stringBuilder.toString());
                    update.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (result.getCode() == 0) {
                                if (onRequestListener != null) {
                                    onRequestListener.sucess(response);
                                }
                            } else {
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                    update.query();
                } else
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
