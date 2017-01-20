package com.popdq.app.connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.InsertProfileActivity;
import com.popdq.app.ui.MyInterestActivity;
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
public class LoginUtilBackup {

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
                if (jsonObject.getInt("isNew") == 1) {
//                    VolleyUtils.getUserAndPushPreference(activity, reponse);
//                    Intent intent = new Intent(activity, InsertProfileActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    activity.startActivity(intent);
//                    activity.finish();

                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                    String userS = PreferenceUtil.getInstancePreference(activity).getString(Values.user, "");

                    char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                    StringBuilder sb1 = new StringBuilder();
                    Random random1 = new Random();
                    for (int i = 0; i < 6; i++)
                    {
                        char c1 = chars1[random1.nextInt(chars1.length)];
                        sb1.append(c1);
                    }
                    random_string = sb1.toString();
//                    System.out.println(sb1.toString()  );
                   // String random_string = sb1.toString();


                    try {
                        user = new Gson().fromJson(userS, User.class);
//                        userName = user.getFirstname() + sb1.toString();

                          if(random_string == null || user.getFirstname() == null){
                              userName = "Open_K7J3GR";
                              Log.e("LOG DATA LOGIN", "Username = OPEN_K7J3GR");
                          } else {
                              Log.e("LOG DATA FNAME", "Username = "+user.getFirstname() + random_string);
                              Log.e("LOG DATA UNAME", "Username = "+user.getUsername());
//                              Toast.makeText(activity,"LOG DATA LOGIN Username = "+user.getFirstname() + random_string,Toast.LENGTH_LONG).show();
//                              userName = user.getFirstname() + random_string;
                              userName = user.getUsername();
                          }
                        anonymous = user.getStatus_anonymous();
//                        userName = user.getFirstname()+"_"+random_string;

                        // userName = user.getFirstname() + random_string;
                        firstName = user.getFirstname();
                        lastName = user.getLastname();

                        Log.e("LOG DATA LOGIN", "TOKEN"+PreferenceUtil.getInstancePreference(activity).getString(Values.TOKEN, "")+ "\nUSERNAME : "+userName+"\n FNAME : "+firstName+" \n LNAME : "+lastName);

                    } catch (Exception e) {

                    }

                    final VolleyUtils volleyUtils = new VolleyUtils(activity, Values.URL_USER_UPDATE_INFO_USER);
                    volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(activity).getString(Values.TOKEN, ""));
                    volleyUtils.addParam(Values.username, userName);
                    volleyUtils.addParam(Values.firstname, firstName);
                    volleyUtils.addParam(Values.lastname, lastName);
                    volleyUtils.addParam(Values.status_anonymous, anonymous + "");
                    String uriAvatar = PreferenceUtil.getInstancePreference(activity).getString(Values.avatar, "");
                    Uri uri = Uri.parse(uriAvatar);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                        int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
                        ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
                        bitmap = (Utils.getCroppedBitmap(bitmap));
                        String avatar = Utils.getBase64Image(Bitmap.createScaledBitmap(bitmap, Values.SIZE_AVATAR, Values.SIZE_AVATAR, true));
                        volleyUtils.addParam(Values.avatar, avatar);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String reponse, Result result) {
                            Log.e("UPLOAD S", reponse);
                            if (VolleyUtils.requestSusscess(reponse)) {
                                try {
                                    MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(activity, Values.PROJECT_TOKEN_MIXPANEL);
                                    MixpanelAPI.getInstance(activity, Values.PROJECT_TOKEN_MIXPANEL).alias(userName, mixpanelAPI.getDistinctId());
                                } catch (Exception e) {

                                }
                                volleyUtils.getUserAndPushPreference(activity, reponse);
                                Toast.makeText(activity, activity.getString(R.string.upload_success), Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(activity, MyInterestActivity.class);
                                intent.putExtra("isNew", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                                activity.finish();

                            } else {
                                if (result.getCode() == 18) {
                                    Toast.makeText(activity, activity.getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
//                                    VolleyUtils.getUserAndPushPreference(activity, reponse);
                                    Intent intent = new Intent(activity, InsertProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                    activity.finish();

                                } else {
                                    Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("UPLOAD R", error);
                        }
                    });
                    volleyUtils.query();

                    return;
                }
            }


            if (code == 0) {
                User user = new Gson().fromJson(jsonObject.getString("user"), User.class);

//                if(user.getCategories() != null){
//                    for (int i=0;i<user.getCategories().size();i++){
//
//                        Log.e("USER LOGIN CATEGORY", user.getCategories().get(i).getName());
//                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getCategories()));
                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getIdCategoriesArr()));
                PreferenceUtil.getInstanceEditor(activity).putString(Values.category_id, user.getIdCategoriesArr());
                PreferenceUtil.getInstanceEditor(activity).commit();
//                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getRealCategory()));
//                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getCategoriesString()));
//                        Log.e("USER LOGIN CHECK", user.getName());
//                    }
//                } else {
//                    Log.e("USER LOGIN CATEGORY", "KOSONG COY");
//                    Log.e("USER LOGIN CHECK", user.getName());
//                }


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
            Log.e(TAG, e.getMessage());
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
