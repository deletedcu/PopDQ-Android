package com.popdq.app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.model.Question;
import com.popdq.app.model.User;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;

import java.util.Random;

/**
 * Created by Dang Luu on 26/08/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        if (data.containsKey("mp_message")) {
            String mp_message = data.getString("mp_message");
            Log.e(TAG, "mixpanel message: " + mp_message);
            //mp_message now contains the notification's text
            NotificationUtil.sendNotification(this, getString(R.string.app_name), mp_message, MainActivity.class);

        } else {

            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Bundle: " + data.toString());
            DataReceiveModel dataReceiveModel = new DataReceiveModel();

            if (data.containsKey("data")) {
                Log.d(TAG, "data: " + data.getString("data"));
                Gson gson = new Gson();
                dataReceiveModel.data = gson.fromJson(data.getString("data"), DataReceiveModel.Data.class);
            }
            if (data.containsKey("aps")) {
                Log.d(TAG, "aps: " + data.getString("aps"));
                Gson gson = new Gson();
                dataReceiveModel.aps = gson.fromJson(data.getString("aps"), DataReceiveModel.Aps.class);
            }

            Intent intent1 = new Intent("update_unread");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

            sendNotification(dataReceiveModel);
        }
/// /        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        // [END_EXCLUDE]
    }
    // [END receive_message]


    private void sendNotification(DataReceiveModel dataReceiveModel) {
        try {
//            Intent intent = new Intent(this, HomeActivity.class);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);
            int type = dataReceiveModel.data.type;
            if (dataReceiveModel.data.type == 1) {
                notificationBuilder.setContentText(getString(R.string.noti_got_question));
                try {
                    Intent intent = new Intent("change_user");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } catch (Exception e) {

                }
//                intent = new Intent(this, ContentQuestionActivity.class);
//                intent.putExtra(Values.question_id, dataReceiveModel.data.question.getId());
            } else if (dataReceiveModel.data.type == 2) {
                notificationBuilder.setContentText(String.format(getString(R.string.noti_has_been_answed), dataReceiveModel.data.from.getDisplayName()));
                Question question = dataReceiveModel.data.question;

//                int method = question.getMethod();
//                if (method == 1) {
//                    intent = new Intent(this, ViewAnswerTextActivity.class);
//                } else if (method == 2) {
//                    intent = new Intent(this, ViewAnswerVoiceRecordActivity.class);
//                } else if (method == 3) {
//                    intent = new Intent(this, ViewAnswerVideoActivity.class);
//                }
//                intent.putExtra(Values.question_id, question.getId());
//                intent.putExtra(Values.method, question.getMethod());
//                intent.putExtra(Values.title, question.getTitle());
            } else if (dataReceiveModel.data.type == 3) {
                User.changeCreditAndPutPrefernce(this, dataReceiveModel.data.credit_earnings, dataReceiveModel.data.credit);
                notificationBuilder.setContentText(dataReceiveModel.data.from.getDisplayName() + " " + getString(R.string.noti_item_view_your_question));
                Question question = dataReceiveModel.data.question;
//                int method = question.getMethod();
//                if (method == 1) {
//                    intent = new Intent(this, ViewAnswerTextActivity.class);
//                } else if (method == 2) {
//                    intent = new Intent(this, ViewAnswerVoiceRecordActivity.class);
//                } else if (method == 3) {
//                    intent = new Intent(this, ViewAnswerVideoActivity.class);
//                }
//                intent.putExtra(Values.question_id, question.getId());
//                intent.putExtra(Values.title, question.getTitle());
            } else if (dataReceiveModel.data.type == 4) {
                User.changeCreditAndPutPrefernce(this, dataReceiveModel.data.credit_earnings, dataReceiveModel.data.credit);
                notificationBuilder.setContentText(String.format(getString(R.string.noti_item_has_been_rejected), dataReceiveModel.data.from.getDisplayName()));

            } else if (dataReceiveModel.data.type == 5) {
                notificationBuilder.setContentText(getString(R.string.noti_wellcome_des));
            } else if (dataReceiveModel.data.type == 8) {
                User.changeCreditAndPutPrefernce(this, dataReceiveModel.data.credit_earnings, dataReceiveModel.data.credit);

//                intent = new Intent(this, TransactionViewActivity.class);
                notificationBuilder.setContentText(String.format(getString(R.string.noti_credited), dataReceiveModel.data.credit + ""));
            } else if (dataReceiveModel.data.type == 10) {
//                intent = new Intent(this, ContentQuestionActivity.class);
//                intent.putExtra(Values.question_id, dataReceiveModel.data.question.getId());
                notificationBuilder.setContentText(String.format(getString(R.string.noti_follow_ask), dataReceiveModel.data.from.getDisplayName()));
            } else if (dataReceiveModel.data.type == 12) {
//                intent = new Intent(this, ContentQuestionActivity.class);
//                intent.putExtra(Values.question_id, dataReceiveModel.data.question.getId());
                notificationBuilder.setContentText(String.format(getString(R.string.noti_follow_answer), dataReceiveModel.data.from.getDisplayName()));
            } else if (dataReceiveModel.data.type == 13) {
//                intent = new Intent(this, MyAnswerActivity.class);
                notificationBuilder.setContentText(String.format(getString(R.string.noti_has_been_expried), dataReceiveModel.data.from.getDisplayName()));
            } else if (dataReceiveModel.data.type == 14) {
//                intent = new Intent(this, MyAnswerActivity.class);
                notificationBuilder.setContentText(String.format(getString(R.string.noti_has_been_expried_in_hour), dataReceiveModel.data.from.getDisplayName()));
            } else if (type == 16 || type == 17 || type == 18 || type == 19) {
                notificationBuilder.setContentText(dataReceiveModel.aps.alert);
            }


//show popup backfround
            if (type == 1 || type == 2 || type == 3) {
                if (!PreferenceUtil.appInForeGround(this)) {
                    startActivity(NotificationUtil.getIntentPopupNotification(this, dataReceiveModel.data.type, dataReceiveModel.data.question, dataReceiveModel.data.from.getDisplayName()));
                }
            }

            Intent intent = NotificationUtil.getIntentNotificationBar(this, dataReceiveModel.data.type, dataReceiveModel.data.question, dataReceiveModel.data.from);
            intent.putExtra(Values.FROM_NOTIFICATION_BAR, true);
            intent.putExtra(Values.NOTIFICATION_ID, dataReceiveModel.data.notification_id);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int id = new Random().nextInt(1000);
            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());

            Log.e("Notification log", "TYPE = "+dataReceiveModel.data.type+
            "Question = "+ dataReceiveModel.data.question+
                    "User = "+dataReceiveModel.data.from+
            "Notif ID = "+dataReceiveModel.data.notification_id);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exception: " + e);
        }
    }

}