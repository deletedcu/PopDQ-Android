package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.model.FavoriteModel;
import com.popdq.app.values.Values;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 13/07/2016.
 */
public class FavoriteUtils {
    public static VolleyUtils volleyUtils;

    public static void add(Context context, String token, long user_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils = new VolleyUtils(context, Values.URL_FAROVITE_ADD);
        addOrRemoveBase(token, user_id, onRequestListenner);
    }

    public static void remove(Context context, String token, long user_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils = new VolleyUtils(context, Values.URL_FAROVITE_REMOVE);
        addOrRemoveBase(token, user_id, onRequestListenner);
    }

    public static void search(Context context, String token, String user_id, String favorite, String name, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils = new VolleyUtils(context, Values.URL_FAROVITE_SEARCH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, user_id + "");
        volleyUtils.addParam(Values.favorite, favorite);
        volleyUtils.addParam(Values.name, name);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();

    }

    public static void addOrRemoveBase(String token, long user_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, user_id + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static List<FavoriteModel> getListFavoriteFromJson(String respone) {
        List<FavoriteModel> favoriteModels = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(respone);

            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Gson gson = new Gson();
                FavoriteModel favoriteModel = gson.fromJson(jsonArray.get(i).toString(), FavoriteModel.class);
                favoriteModels.add(favoriteModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favoriteModels;
    }
}

