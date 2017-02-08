package com.popdq.app.connection;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.InsertProfileActivity;
import com.popdq.app.ui.LoginActivity;
import com.popdq.app.ui.MyInterestActivity;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.mixpanelutil.MixPanelUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Dang Luu on 09/08/2016.
 */
public class LoginUtil {

    private static final String TAG = "LoginUtil";
    static User user;
    static String userName, firstName, lastName;
    static int anonymous;
    static String random_string;
    static String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static GoogleApiClient mGoogleApiClient;

    public static void loginSuccess(final Activity activity, String reponse) {
        try {
            JSONObject jsonObject = new JSONObject(reponse);
            int code = jsonObject.getInt("r");
            if (jsonObject.has("isNew")) {

                int isnew = jsonObject.getInt("isNew");

                if (isnew == 1) {
                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                    String userS = PreferenceUtil.getInstancePreference(activity).getString(Values.user, "");
                    user = new Gson().fromJson(userS, User.class);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(activity)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(activity.getString(R.string.noti_wellcome_title))
                                    .setContentText(activity.getString(R.string.noti_wellcome_des))
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri);


                    NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(1, mBuilder.build());

                    try {
                        MixpanelAPI.getInstance(activity, Values.PROJECT_TOKEN_MIXPANEL).getPeople().identify(user.getUsername());
                    } catch (Exception e) {

                    }
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("login"));
                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                    if (user.getUsername() == null || user.getUsername().equals("")) {
                        Intent intent = new Intent(activity, InsertProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        return;
                    }

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            String token = "";
                            InstanceID instanceID = InstanceID.getInstance(activity);
                            try {

                                token = instanceID.getToken(activity.getString(R.string.sender_id),
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                Log.e(TAG, "token: " + token);
                                final String finalToken = token;
                                MixPanelUtil.registerPushMixpanel(activity);
                                PushNotificationUtil.registerPush(activity, PreferenceUtil.getToken(activity), token, activity.getPackageName(), new VolleyUtils.OnRequestListenner() {
                                    @Override
                                    public void onSussces(String response, Result result) {
                                        Log.e(TAG, "registerpush: " + response);
                                        PreferenceUtil.getInstanceEditor(activity).putString(Values.token_push, finalToken);
                                        PreferenceUtil.getInstanceEditor(activity).commit();
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                        }
                    }.execute();
//                    activity.startActivity(new Intent(activity, MainActivity.class));
//                    activity.finish();
                    pushnotifregis(activity);



                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();

                } else {
                    Log.e("isNew", "Not isNew = 0");

                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                    String userS = PreferenceUtil.getInstancePreference(activity).getString(Values.user, "");
                    user = new Gson().fromJson(userS, User.class);

//                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(activity)
//                                    .setSmallIcon(R.mipmap.ic_launcher)
//                                    .setContentTitle(activity.getString(R.string.noti_wellcome_title))
//                                    .setContentText(activity.getString(R.string.noti_wellcome_des))
//                                    .setAutoCancel(true)
//                                    .setSound(defaultSoundUri);
//
//
//                    NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                    mNotificationManager.notify(1, mBuilder.build());

                    try {
                        MixpanelAPI.getInstance(activity, Values.PROJECT_TOKEN_MIXPANEL).getPeople().identify(user.getUsername());
                    } catch (Exception e) {

                    }
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("login"));
                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                    if (user.getUsername() == null || user.getUsername().equals("")) {
                        Intent intent = new Intent(activity, InsertProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        return;
                    }

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            String token = "";
                            InstanceID instanceID = InstanceID.getInstance(activity);
                            try {

                                token = instanceID.getToken(activity.getString(R.string.sender_id),
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                Log.e(TAG, "token: " + token);
                                final String finalToken = token;
                                MixPanelUtil.registerPushMixpanel(activity);
                                PushNotificationUtil.registerPush(activity, PreferenceUtil.getToken(activity), token, activity.getPackageName(), new VolleyUtils.OnRequestListenner() {
                                    @Override
                                    public void onSussces(String response, Result result) {
                                        Log.e(TAG, "registerpush: " + response);
                                        PreferenceUtil.getInstanceEditor(activity).putString(Values.token_push, finalToken);
                                        PreferenceUtil.getInstanceEditor(activity).commit();
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                        }
                    }.execute();
//                    activity.startActivity(new Intent(activity, MainActivity.class));
//                    activity.finish();
//                    pushnotifregis(activity);



                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();

                }

            } else if (code == 0) {

                User user = new Gson().fromJson(jsonObject.getString("user"), User.class);

                try {
                    MixpanelAPI.getInstance(activity, Values.PROJECT_TOKEN_MIXPANEL).getPeople().identify(user.getUsername());
                } catch (Exception e) {

                }
                LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("login"));
                VolleyUtils.getUserAndPushPreference(activity, reponse);
                if (user.getUsername() == null || user.getUsername().equals("")) {
                    Intent intent = new Intent(activity, InsertProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                    return;
                }

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        String token = "";
                        InstanceID instanceID = InstanceID.getInstance(activity);
                        try {

                            token = instanceID.getToken(activity.getString(R.string.sender_id),
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            Log.e(TAG, "token: " + token);
                            final String finalToken = token;
                            MixPanelUtil.registerPushMixpanel(activity);
                            PushNotificationUtil.registerPush(activity, PreferenceUtil.getToken(activity), token, activity.getPackageName(), new VolleyUtils.OnRequestListenner() {
                                @Override
                                public void onSussces(String response, Result result) {
                                    Log.e(TAG, "registerpush: " + response);
                                    PreferenceUtil.getInstanceEditor(activity).putString(Values.token_push, finalToken);
                                    PreferenceUtil.getInstanceEditor(activity).commit();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();

            } else if (code == 9) {
                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } else if (code == 101) {
                Toast.makeText(activity, activity.getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void pushnotifregis(Activity activity) {

        VolleyUtils volleyUtils = new VolleyUtils(activity, Values.URL_PUSH_NOTIFICATION);
        volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(activity).getString(Values.TOKEN, ""));
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {


            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();

    }


    public static void logOut(Context context, GoogleApiClient mGoogleApiClient) {

        MixPanelUtil.clearPushMixpanel(context);
        String tokenPush = PreferenceUtil.getInstancePreference(context).getString(Values.token_push, "");

        PushNotificationUtil.unRegisterPush(context, PreferenceUtil.getToken(context), tokenPush, context.getPackageName(), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                Log.e(TAG, "unregisterpush: " + response);
            }

            @Override
            public void onError(String error) {

            }
        });
        try {
            if (User.getInstance(context).getFacebook_id() != null) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
         }

//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(getActivt)
//                .addOnConnectionFailedListener(context).addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
//

//        mGoogleApiClient.disconnect();
//        mGoogleApiClient.clearDefaultAccount();
        if(mGoogleApiClient != null ) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                        }
                    });
        }

        PreferenceUtil.getInstanceEditor(context).putString(Values.TOKEN, "");
        PreferenceUtil.getInstanceEditor(context).commit();

        PreferenceUtil.getInstanceEditor(context).putString(Values.user, "");
        PreferenceUtil.getInstanceEditor(context).commit();

        PreferenceUtil.getInstanceEditor(context).putString(Values.avatar, "");
        PreferenceUtil.getInstanceEditor(context).commit();

        PreferenceUtil.getInstanceEditor(context).putString(Values.category_id, "");
        PreferenceUtil.getInstanceEditor(context).commit();
//        context.startActivity(new Intent(context, LoginActivity.class));

    }


}
