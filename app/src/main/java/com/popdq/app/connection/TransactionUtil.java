package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.model.Transaction;
import com.popdq.app.values.Values;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 9/6/2016.
 */
public class TransactionUtil {

    public static void getListTransaction(Context context, String token, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_TRANSACTION_MY_LIST);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static List<Transaction> getListTransactionFromRespone(String response){
        List<Transaction> transactions = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                transactions.add(gson.fromJson(jsonArray.get(i).toString(), Transaction.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transactions;
    }



}
