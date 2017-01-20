package com.popdq.app.connection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.LoginActivity;
import com.popdq.app.ui.SignupActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class VolleyUtils {

    private static final String TAG = "VolleyUtils";
    private static Toast toast;

    public interface OnRequestListenner {
        public void onSussces(String response, Result result);

        public void onError(String error);
    }

    private OnRequestListenner onRequestListenner;

    public void setOnRequestComplete(OnRequestListenner onRequestListenner) {
        this.onRequestListenner = onRequestListenner;
    }

    private HashMap<String, String> params;
    private static String url;
    private static boolean issignup;
    private Context context;
    private RequestQueue queue;

    public VolleyUtils(Context context, String url) {
        this.context = context;
        this.url = url;
        queue = Volley.newRequestQueue(context);
        params = new HashMap<>();
//        VolleyM

    }

//    public VolleyUtils(Context context, String url, boolean issignup) {
//        this.context = context;
//        this.url = url;
//        queue = Volley.newRequestQueue(context);
//        params = new HashMap<>();
//        issignup = issignup;
////        VolleyM
//
//    }

    public HashMap<String, String> getParams() {
        Iterator i = params.keySet().iterator();

        while (i.hasNext()) {
            String key = i.next().toString();
            String value = params.get(key);
            Log.e("params: " + key, value);
        }
        return params;
    }

    //    public void jsonRequest() {
//            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    if (onRequestListenner != null) {
//                        onRequestListenner.onSussces(response.toString());
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    return checkParams(params);
//                }
//
//            };
//            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    60000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            queue.add(jsonRequest);
//
//
//
//    }
    boolean isUp1s = false, isResult = false;
    ProgressDialog progressDialog = null;

    public void query() {
        isUp1s = false;
        isResult = false;

        boolean isShow = true;
        if (url == null) {
//            Toast.makeText(context, "URL error!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (url.equals(Values.URL_QUESTION_SEARCH) || url.equals(Values.URL_QUESTION_MY_PREVIEW)
                || url.equals(Values.URL_EXPERT_SEARCH) || url.equals(Values.URL_QUESTION_MY_LIST)
                || url.equals(Values.URL_ANSWER_MY_LIST_ANSWER) || url.equals(Values.URL_QUESTION_ANSWERED)
                || url.equals(Values.URL_FAROVITE_SEARCH) || url.equals(Values.URL_USER_SEARCH)
                || url.equals(Values.URL_FAROVITE_ADD) || url.equals(Values.URL_FAROVITE_REMOVE)
                || url.equals(Values.URL_TRANSACTION_MY_LIST) || url.equals(Values.URL_GET_NOTIFICATION)
                || url.equals(Values.URL_USER_GET_MY_PROFILE)
                || url.equals(Values.URL_NOTIFICATION_GET_COUNT_UNREAD)) {


//            try {
//                if (params.containsKey(Values.limit)) {
//                    if (Integer.parseInt(params.get(Values.limit)) > 0) {
            isShow = false;
//                    }
//                }
//            } catch (Exception e) {
//
//            }
        }
        if (isShow) {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.dismiss();
                progressDialog.setMessage(context.getString(R.string.loading));
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isUp1s = true;
                        if (isResult && isUp1s) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {

                            }
                        }

                    }
                }, 1000);
            } catch (Exception e) {

            }
        }
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            return;
        }

//        final ProgressDialog finalProgressDialog = progressDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (url.equals(Values.URL_FAROVITE_REMOVE) || url.equals(Values.URL_FAROVITE_ADD)) {
                            Intent intent = new Intent("change_user");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }

                        Log.e(TAG, "onResponse - "+context.getClass().getSimpleName()+ ": " + url);
                        Log.e(TAG, "onResponse - " +context.getClass().getSimpleName()+ ": "+ response);
                        isResult = true;
                        try {
                            if (isResult && isUp1s) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {

                        }
                        if (onRequestListenner != null) {
                            Result result = VolleyUtils.getResult(response);
                            checkTokenValidate(context, result.getCode());
                            onRequestListenner.onSussces(response, result);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                Log.e(TAG, "onErrorResponse: " + url);
                Log.e(TAG, "onErrorResponse: " + error);
                isResult = true;
                try {
                    if (isResult && isUp1s) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {

                }
                if (onRequestListenner != null) {
                    onRequestListenner.onError(error.getMessage());
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                return checkParams(params);
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                120000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    public void track(String url) {
//        if(url.equals(Values.URL))
    }

    private Map<String, String> checkParams(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    public void addParam(String key, String values) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, values);
    }

    public void loginWithEmail(String email, String passWord, OnRequestListenner onRequestListenner) {

    }

    public static List<Question> getListQuestionFromJson(String reponse) {
        List<Question> questions = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(reponse);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                questions.add(gson.fromJson(jsonArray.get(i).toString(), Question.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void showAToast ( final Context context,String st){ //"Toast toast" is declared in the class
        try{ toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(context, st, Toast.LENGTH_LONG);
        }
        toast.show();  //finally display it
    }


    public static void checkTokenValidate(final Context context, int resultCode) {

        if (resultCode == 35 || resultCode == 9) {
            MyApplication.user = null;
            PreferenceUtil.getInstanceEditor(context).putString(Values.user, "");
            PreferenceUtil.getInstanceEditor(context).commit();
            if (url.equals(Values.URL_USER_FORGOT_PASWORD)) {
//                Toast.makeText(context, "YOU HAVE BEEN BLOCKED ACCOUNT!", Toast.LENGTH_SHORT).show();

            } else {
//                Toast.makeText(context, "Your session has expired, please re-login again, thanks.", Toast.LENGTH_SHORT).show();
                showAToast(context,"Oops, an error has occurred. Try logging out and logging in again.");
                Intent intent = new Intent(context, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } else if(resultCode == 2 ) {

            MyApplication.user = null;
            PreferenceUtil.getInstanceEditor(context).putString(Values.user, "");
            PreferenceUtil.getInstanceEditor(context).commit();
            if (url.equals(Values.URL_USER_FORGOT_PASWORD)) {
//                Toast.makeText(context, "YOU HAVE BEEN BLOCKED ACCOUNT!", Toast.LENGTH_SHORT).show();

            } else {
                showAToast(context,"Oops, an error has occurred. Try logging out and logging in again.");
                Intent intent = new Intent(context, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }



        } else if (resultCode == 4) {
            if (context instanceof Activity) {
                DialogBase dialogBase = new DialogBase(context);
                dialogBase.setTitle(context.getString(R.string.notice));
                dialogBase.setMessage(context.getString(R.string.notice_inactivate));
                dialogBase.getBtnCancel().setVisibility(View.GONE);
                dialogBase.getBtnOk().setText("OK");
                dialogBase.setOnClickOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                try {
                    dialogBase.show();
                } catch (Exception e) {

                }
            }

        } else if (resultCode == 14) {
            Toast.makeText(context, context.getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
        } else if (resultCode == 21) {
            Toast.makeText(context, context.getString(R.string.email_exist), Toast.LENGTH_SHORT).show();

        }

    }

    public static List<User> getListUserFromJsonNotMe(Context context, String reponse) {
        List<User> users = new ArrayList<>();
        long myId = PreferenceUtil.getUserId(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(reponse);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                User user = gson.fromJson(jsonArray.get(i).toString(), User.class);
                if (user.getId() != myId)
                    users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<User> getListUserFromJson(String reponse) {
        List<User> users = getListModelFromRespone(reponse, User.class);
        return users;
    }

    public static User getUserInfo(String respone) {
        try {
            JSONObject jsonObject = new JSONObject(respone);
            int r = jsonObject.getInt("r");
            switch (r) {
                case 0:
                    Gson gson = new Gson();
                    String content = jsonObject.getString("user");
                    User user = gson.fromJson(content, User.class);
                    return user;
            }

        } catch (JSONException e) {

        }
        return null;
    }

    public static User getExpertInfo(String respone) {
        try {
            JSONObject jsonObject = new JSONObject(respone);
            int r = jsonObject.getInt("r");
            switch (r) {
                case 0:
                    Gson gson = new Gson();
                    String content = jsonObject.getString("experts");
                    User user = gson.fromJson(content, User.class);
                    return user;
            }

        } catch (JSONException e) {

        }
        return null;
    }

    public static void getUserAndPushPreference(Context context, String reponse) {
        try {
            JSONObject jsonObject = new JSONObject(reponse);
            int r = jsonObject.getInt("r");
            switch (r) {
                case 0:
                    Gson gson = new Gson();
                    String content = jsonObject.getString("user");
                    Log.e("Content User",content);
                    User user = gson.fromJson(content, User.class);

                    MyApplication.user = user;
//                    Toast.makeText(content, user.getCurrent_token(), Toast.LENGTH_SHORT).show();
                    PreferenceUtil.getInstanceEditor(context).putString(Values.TOKEN, user.getCurrent_token());
                    PreferenceUtil.getInstanceEditor(context).putString(Values.user, content);
                    PreferenceUtil.getInstanceEditor(context).putLong(Values.my_user_id, user.getId());
                    PreferenceUtil.getInstanceEditor(context).commit();
                    try {
                        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL);
                        MixpanelAPI.getInstance(context, Values.PROJECT_TOKEN_MIXPANEL).identify(mixpanelAPI.getDistinctId());
                        JSONObject json = new JSONObject(content);
                        json.put("categories", user.getCategoriesString());
                        json.put("config_charge", user.getConfigchareString());
                        mixpanelAPI.getPeople().set(json);
                        mixpanelAPI.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        } catch (JSONException e) {
            Toast.makeText(context, reponse, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public static final boolean requestSusscess(String responseJson) {
        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            if (jsonObject.getInt("r") == 0) {
                int i = 0;
                i++;
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static Result getResult(String responseJson) {
        Result result = null;
        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            int code = jsonObject.getInt("r");
            String message = jsonObject.getString("message");
            result = new Result(code, message);
        } catch (JSONException e) {
            e.printStackTrace();
            result = new Result(-1, e.getMessage());
        }
        return result;
    }

    public static List getListModelFromRespone(String response, Class aClass) {
        List list = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                list.add(gson.fromJson(jsonArray.get(i).toString(), aClass));
            }
//            Collections.sort(list);
//            Collections.reverse(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


}
