package com.popdq.app.connection;

import android.content.Context;
import android.util.Log;

import com.popdq.app.model.Answer;
import com.popdq.app.values.Values;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 15/07/2016.
 */
public class AnswerUtil {
    //09:06 test push


    public static void create(Context context, String token, long question_id, String content, String attachments,
                              int method, String language_written, String language_spoken, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_CREATE);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id + "");
        volleyUtils.addParam(Values.content, content);
        volleyUtils.addParam(Values.attachments_arr, attachments);
        volleyUtils.addParam(Values.method, method + "");
        volleyUtils.addParam(Values.language_written, language_written);
        volleyUtils.addParam(Values.language_spoken, language_spoken);
        volleyUtils.setOnRequestComplete(onRequestListenner);

        Log.e("Answer CREATE log", "token : "+token+"\n answer id : "+question_id
                +"\n content : "+content+"\n attachment : "+attachments
                +"\n method : "+method+"\n language_written : "+language_written
                +"\n language_spoken : "+language_spoken);

        volleyUtils.query();
    }

    public static void update(Context context, String token, long question_id, String content, long answer_id,
                              String attachments, int method, String language_written,String language_spoken, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_UPDATE);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.answer_id, answer_id + "");
        volleyUtils.addParam(Values.content, content);
        volleyUtils.addParam(Values.attachments_arr, attachments);
        volleyUtils.addParam(Values.method, method + "");
        volleyUtils.addParam(Values.language_written, language_written);
        volleyUtils.addParam(Values.language_spoken, language_spoken);
//        volleyUtils.addParam(Values.answer_id, answer_id);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        Log.e("Answer log", "token : "+token+"\n answer id : "+question_id
                +"\n content : "+content+"\n attachment : "+attachments
                +"\n method : "+method+"\n language_written : "+language_written
                +"\n language_spoken : "+language_spoken);
        volleyUtils.query();
    }

    public static void getListAnswer(Context context, String token, long user_id, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_LIST_ANSWER);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, user_id + "");
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getMyListAnswer(Context context, String token, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_MY_LIST_ANSWER);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void rating(Context context, String token, long answer_id, String content, float stars, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_RATEING);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.answer_id, answer_id + "");
        volleyUtils.addParam(Values.content, content + "");
        volleyUtils.addParam(Values.rating, stars + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void search(Context context, String token, long question_id, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_SEARCH);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id + "");
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void report(Context context, String token, long answer_id, String content, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_ANSWER_REPORT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.answer_id, answer_id + "");
        volleyUtils.addParam(Values.content, content + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static List<Answer> getAnswerFromJsonRespone(String respone) {
        List<Answer> answers = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(respone);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();
            Gson gson = new Gson();
            for (int i = 0; i < length; i++) {
                answers.add(gson.fromJson(jsonArray.get(i).toString(), Answer.class));
            }
            return answers;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answers;
    }
}
