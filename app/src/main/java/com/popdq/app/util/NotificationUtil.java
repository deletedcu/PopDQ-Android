package com.popdq.app.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.fragment.FeedFragment;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ContentQuestionActivity;
import com.popdq.app.ui.MyAnswerActivity;
import com.popdq.app.ui.PopupNotificationActivity;
import com.popdq.app.ui.TransactionViewActivity;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.ui.ViewAnswerTextActivity;
import com.popdq.app.ui.ViewAnswerVideoActivity;
import com.popdq.app.ui.ViewAnswerVoiceRecordActivity;
import com.popdq.app.values.Values;

import java.util.Random;

/**
 * Created by Dang Luu on 9/5/2016.
 */
public class NotificationUtil {
    private static final String TAG = "NotificationUtil";

    public static void sendNotification(Context context, String title, String message, Class openClass) {
        try {
//            User.changeCreditAndPutPrefernce(context, dataReceiveModel.data.credit_earnings, dataReceiveModel.data.credit);
            Intent intent = new Intent(context, openClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = new Random().nextInt(1000);
            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e);
        }
    }


    public static void getNotifications(Context context, String token, int limit, int offset, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_GET_NOTIFICATION);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.limit, limit + "");
        volleyUtils.addParam(Values.offset, offset + "");
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void readNotification(final Context context, String token, long notificationId, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_NOTIFICATION_READ);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.notificationId, notificationId + "");
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    Intent intent1 = new Intent("update_unread");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();
    }

    public static void readAllNotification(Context context, String token, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_NOTIFICATION_READ_ALL);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static void getCountUnread(Context context, String token, VolleyUtils.OnRequestListenner onRequestListenner) {
        VolleyUtils volleyUtils = new VolleyUtils(context, Values.URL_NOTIFICATION_GET_COUNT_UNREAD);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(onRequestListenner);
        volleyUtils.query();
    }

    public static Intent getIntentPopupNotification(Context context, int type, Question question, String userAskName) {
        Intent intent = null;
        String message = "";
        if (type == 1) {
            message = context.getString(R.string.noti_got_question);
        } else if (type == 2) {
            message = String.format(context.getString(R.string.noti_has_been_answed), userAskName);
        } else if (type == 3) {
            message = userAskName + " " + context.getString(R.string.noti_item_view_your_question);
        }
        intent = new Intent(context, PopupNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Values.question_id, question.getId());
        intent.putExtra(Values.title, context.getString(R.string.app_name));
        intent.putExtra(Values.type, type);
        intent.putExtra(Values.message, message);
        return intent;
    }


    public static Intent getIntentNotificationBar(Context context, int type, Question question, User user) {
        Intent intent = null;
        switch (type) {
            case 1:
                intent = new Intent(context, ContentQuestionActivity.class);
                intent.putExtra(Values.question_id, question.getId());
                break;
            case 2:
                int method = question.getMethod();
                if (method == 1) {
                    intent = new Intent(context, ViewAnswerTextActivity.class);
                } else if (method == 2) {
                    intent = new Intent(context, ViewAnswerVoiceRecordActivity.class);
                } else if (method == 3) {
                    intent = new Intent(context, ViewAnswerVideoActivity.class);
                }
                intent.putExtra(Values.question_id, question.getId());
                intent.putExtra(Values.method, question.getMethod());
                intent.putExtra(Values.title, question.getTitle());
                break;
            case 3:

                method = question.getMethod();
                if (method == 1) {
                    intent = new Intent(context, ViewAnswerTextActivity.class);
                } else if (method == 2) {
                    intent = new Intent(context, ViewAnswerVoiceRecordActivity.class);
                } else if (method == 3) {
                    intent = new Intent(context, ViewAnswerVideoActivity.class);
                }
                intent.putExtra(Values.question_id, question.getId());
                intent.putExtra(Values.method, question.getMethod());
                intent.putExtra(Values.title, question.getTitle());
                intent.putExtra(Values.name, user.getUsername());
                break;
            case 4:
                intent = new Intent(context, ContentQuestionActivity.class);
                intent.putExtra(Values.question_id, question.getId());
                break;
            case 5:
                intent = new Intent(context, FeedFragment.class);
//                intent.putExtra(Values.verified, true);
                break;
            case 8:
                intent = new Intent(context, TransactionViewActivity.class);

                break;
            case 10:
                intent = new Intent(context, ContentQuestionActivity.class);
                intent.putExtra(Values.question_id, question.getId());

                break;
            case 12:
                intent = new Intent(context, ContentQuestionActivity.class);
                intent.putExtra(Values.question_id, question.getId());

                break;

            case 13:
                intent = new Intent(context, MyAnswerActivity.class);
                break;
            case 14:
                intent = new Intent(context, MyAnswerActivity.class);
                break;

            case 16://to a specific user's profile page
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, user.getId());
                break;
            case 17://to feed
                intent = new Intent(context, MainActivity.class);

                break;
            case 18: //to a specific question's page
                intent = new Intent(context, ContentQuestionActivity.class);
                intent.putExtra(Values.question_id, question.getId());

                break;
            case 19: // to My Transactions
                intent = new Intent(context, TransactionViewActivity.class);

                break;
        }


        return intent;
    }
}
