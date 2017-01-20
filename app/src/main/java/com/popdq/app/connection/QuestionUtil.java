package com.popdq.app.connection;

import android.content.Context;
import android.util.Log;

import com.popdq.app.values.Values;


/**
 * Created by Dang Luu on 13/07/2016.
 */
public class QuestionUtil {

    public static void create(Context context, String token, String category_id, long user_id_answer, String title,
                              String description, String attachments, String tag,
                              int status_anonymous, int method, String language_written, String language_spoken,  int free_preview,
                              int type, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_CREATE);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.category_id, category_id);
        volleyUtils.addParam(Values.user_id_answer, user_id_answer + "");
        volleyUtils.addParam(Values.title, title);
        volleyUtils.addParam(Values.description, description);
        volleyUtils.addParam(Values.attachments_arr, attachments);
        volleyUtils.addParam(Values.method, method + "");
        volleyUtils.addParam(Values.language_written, language_written + "");
        volleyUtils.addParam(Values.language_spoken, language_spoken + "");
        volleyUtils.addParam(Values.free_preview, free_preview + "");
        volleyUtils.addParam(Values.type, type + "");
        volleyUtils.addParam(Values.tag, tag);
        volleyUtils.addParam(Values.status_anonymous, status_anonymous + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        Log.e("QuestionUtil",
                "Token :"+token+"\n category_id : "+category_id+"\n user_id_answer : "+user_id_answer+"\n title : "+title+
                        "\n description : "+description+"\n attachment : "+attachments+"\n method : "+method+"\n language_written : "+language_written+
                        "\n language_spoken : "+language_spoken+"\n free_preview : "+free_preview+"\n type : "+type+"\n tag : "+tag+"\n status_anon : "+status_anonymous);

                volleyUtils.query();
    }

    public static void detail(Context context, String token, String question_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_GET_DETAIL);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void search(String link, Context context, String token, String keyword, String tag, String status, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, link);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.keyword, keyword);
        volleyUtils.addParam(Values.tag, tag);
        volleyUtils.addParam(Values.status, status);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void search(String link, Context context, int category_id, String token, String keyword, String tag, int status, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, link);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.category_id, category_id + "");
        volleyUtils.addParam(Values.keyword, keyword);
        volleyUtils.addParam(Values.tag, tag);
        volleyUtils.addParam(Values.status, status + "");
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getListViewer(Context context, String token, long question_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_GET_VIEWER);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void myListQuestion(Context context, String token, String keyword, String tag, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_MY_LIST);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.keyword, keyword);
        volleyUtils.addParam(Values.tag, tag);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void report(Context context, String token, long question_id, String content, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_REPORT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id + "");
        volleyUtils.addParam(Values.content, content);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void reject(Context context, String token, long question_id, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_REJECT);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.question_id, question_id + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getListAnswerOfUser(Context context, String token, long user_id, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_ANSWERED);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, user_id + "");
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getListAnsweredOfUser(Context context, String token, long user_id, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_QUESTION_ANSWERED);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.user_id, user_id + "");
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.status, 2 + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }
}
