package com.popdq.app.connection;

import android.content.Context;

import com.popdq.app.model.Credit;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 31/07/2016.
 */
public class CreditUtils {

    public static void convert(Context context, String token, float credit, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_CREDIT_CONVERT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.credit, credit + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void withDraw(Context context, String token, float credit, String email, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_CREDIT_CONVERT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.credit, credit + "");
        volleyUtils.addParam(Values.email, email + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void withDrawPaypal(Context context, String token, float credit,String email, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_TRANSACTION_WITHDRAW);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.credit, credit + "");
        volleyUtils.addParam(Values.type, 1 + "");
        volleyUtils.addParam(Values.email,email);
        volleyUtils.addParam(Values.bank_name,  "  ");
        volleyUtils.addParam(Values.bank_account,  "  ");
        volleyUtils.addParam(Values.your_name,  "  ");

        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void withDrawBank(Context context, String token, String bankName, String bankAcc, String yourName, float credit, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_TRANSACTION_WITHDRAW);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.credit, credit + "");
        volleyUtils.addParam(Values.bank_name, bankName + "");
        volleyUtils.addParam(Values.email, User.getInstance(context).getEmail()+"");
        volleyUtils.addParam(Values.bank_account, bankAcc + "");
        volleyUtils.addParam(Values.type, 2 + "");
        volleyUtils.addParam(Values.your_name, yourName + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void verify(Context context, String token, String receipt_data, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_CREDIT_VERIFY);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.type, 2 + "");
        volleyUtils.addParam(Values.receipt_data, receipt_data + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);

        volleyUtils.query();
    }

    public static void getListCanBuy(Context context, String token, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_CREDIT_LIST_CREDIT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static List<Credit> getListCreditFromRespone(String response) {
        List<Credit> credits = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                credits.add(gson.fromJson(jsonArray.get(i).toString(), Credit.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credits;
    }

    public static List<Credit> getLocalListCredit(Context context) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Credit>>() {
        }.getType();
        List<Credit> credits = gson.fromJson(PreferenceUtil.getInstancePreference(context).getString(Values.LIST_CREDIT_CAN_BUY, ""), listType);
        return credits;
    }

    public static List<Credit> getListCreditsAdded0AndDisable(Context context) {
        List<Credit> credits = getLocalListCredit(context);
        for (Credit credit : credits) {
            credit.setCredit("$" + credit.getCredit());
        }
        Credit credit1 = new Credit();
        credit1.setCredit("FREE");
        credits.add(0, credit1);
        Credit credit = new Credit();
        credit.setCredit("DISABLED");
        credits.add(credits.size(), credit);
        return credits;
    }

    public static void getListCreditCanBuyToLocal(final Context context) {
        // get list credit can buy
        CreditUtils.getListCanBuy(context, PreferenceUtil.getToken(context), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    List credits = VolleyUtils.getListModelFromRespone(response, Credit.class);
                    PreferenceUtil.getInstanceEditor(context).putString(Values.LIST_CREDIT_CAN_BUY, new Gson().toJson(credits));
                    PreferenceUtil.getInstanceEditor(context).commit();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public static void getListCategories(final Context context) {
        CategoryUtils.getListCategories(context, PreferenceUtil.getToken(context), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                PreferenceUtil.getInstanceEditor(context).putString(Values.category_respone, response);
                PreferenceUtil.getInstanceEditor(context).commit();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

}
