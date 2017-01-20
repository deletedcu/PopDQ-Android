package com.popdq.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.R;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int OUR_REQUEST_CODE = 5;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String requestIdToken = "698177898809-pj0vpko7tp65pvcsi904npeogshnqav0.apps.googleusercontent.com";
//    private String requestIdToken = "698177898809-a8nu67iip4ce5etflgml66orqf6frtj5.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(LoginActivity.this, Values.PROJECT_TOKEN_MIXPANEL);
//            MixpanelAPI.getInstance(LoginActivity.this, Values.PROJECT_TOKEN_MIXPANEL).alias(System.currentTimeMillis()+"", mixpanelAPI.getDistinctId());
//            int i=0;
//            i++;
//        } catch (Exception e) {
//
//        }
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.print("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(this,""+ Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceUtil.getInstancePreference(this);
        editor = PreferenceUtil.getInstanceEditor(this);

        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.sign_up));
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.sign_up_option));

        ((LinearLayout) findViewById(R.id.btnLoginWithFb)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnLoginWithGoogle)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnLoginWithEmail)).setOnClickListener(this);
        ((TextViewBold) findViewById(R.id.btnLogin)).setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(requestIdToken)
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this /* Context */)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {
            IntentFilter intentFilter = new IntentFilter("login");
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastLogin, intentFilter);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastLogin);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLogin:
                startActivity(new Intent(this, LoginWithEmailActivity.class));
                break;
            case R.id.btnLoginWithFb:
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                       Set<String> denied =  loginResult.getRecentlyDeniedPermissions();
//                       Set<String> grant =  loginResult.getRecentlyGrantedPermissions();
                        if(denied.contains("email")){
                            Toast.makeText(LoginActivity.this, getString(R.string.email_required), Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        Log.e(TAG, "onSuccess:----- " + loginResult.getAccessToken());
//                        Log.e(TAG, "onSuccesstoken =-------:" + AccessToken.getCurrentAccessToken().getToken());
//                        Log.e(TAG, "onSuccess profile =-------:" + Profile.getCurrentProfile().getDisplayName());
                        logInWithFb(AccessToken.getCurrentAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                        Log.e(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "onError: " + error.toString());
                    }
                });
                List<String> permissionNeeds = Arrays.asList(new String[]{"public_profile", "user_friends", "email"});
                LoginManager.getInstance().logInWithReadPermissions(this,
                        permissionNeeds);
//                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(new String("user_friends")));
//                try {
//                    PackageInfo info = getPackageManager().getPackageInfo(
//                            getPackageName(),
//                            PackageManager.GET_SIGNATURES);
//                    for (Signature signature : info.signatures) {
//                        MessageDigest md = MessageDigest.getInstance("SHA");
//                        md.update(signature.toByteArray());
//                        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//
//                } catch (NoSuchAlgorithmException e) {
//
//                }

//                Toast.makeText(LoginActivity.this, "Facebook", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLoginWithGoogle:
                getDataGoogle();
//                startActivity(new Intent(this, HomeActivity.class));

//                Toast.makeText(LoginActivity.this, "Google", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLoginWithEmail:
                startActivity(new Intent(this, SignupActivity.class));
//                Toast.makeText(LoginActivity.this, "Email", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void getDataGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, OUR_REQUEST_CODE);
    }

    public void logInWithGoogle(String id_token) {

        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_LOGIN_WITH_GOOGLE);
        volleyUtils.addParam(Values.id_token, id_token);
        volleyUtils.addParam(Values.client_type, 3 + "");
//        volleyUtils.addParam("debug", 1 + "");
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                Log.e("respone login google:", reponse);
                LoginUtil.loginSuccess(LoginActivity.this, reponse);
//                if (result.getCode() != 0) {
//                    Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                try {
//                    JSONObject jsonObject = new JSONObject(reponse);
//                    int code = jsonObject.getInt("r");
//                    if (jsonObject.has("isNew")) {
//                        if (jsonObject.getInt("isNew") == 1) {
//                            volleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                            startActivity(new Intent(LoginActivity.this, InsertProfileActivity.class));
//                            finish();
//                            return;
//                        }
//                    }
//                    if (code == 0) {
//                        volleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                        finish();
//                    } else if (code == 9) {
//                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onError(String error) {
                if (error != null)
                    Log.e("error login google:", error);
            }
        });
        volleyUtils.query();
    }

    public void init() {

    }
//
//    User user;
//    String userName, firstName, lastName;
//    int anonymous;
//    String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    static User user;
    static String userName, firstName, lastName;
    static int anonymous;

    public void logInWithFb(String access_token) {
        Log.e(TAG, "token fb: " + access_token);

//token danglh push:
//EAADE2bhJKbUBAFILPwQv7HbZBZAgtPZAttaVFYGThiSGADQ0ZB5OrYaSbMg0tUQwGmu6gXtiG0TVTSYp74nPjdbPtfxjUsTCXHaC9J2RTNX1aJhoWGx674TXp5hehx6OuEvXMyrzJJyPLoqgOtSJhPthPK8ZB58H3KZCZC4WR3YiXJ8q1xjhKhBuESNze8sjlnMR05EV5kRIJHlQBW49OFJ
        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_LOGIN_WITH_FB);
        volleyUtils.addParam(Values.access_token, access_token);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                Log.e("respone login facebook:", reponse);



                LoginUtil.loginSuccess(LoginActivity.this, reponse);
//                VolleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//
//                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
//                StringBuilder sb1 = new StringBuilder();
//                Random random1 = new Random();
//                for (int i = 0; i < 6; i++)
//                {
//                    char c1 = chars1[random1.nextInt(chars1.length)];
//                    sb1.append(c1);
//                }
//                String random_string = sb1.toString();
//
//
////                VolleyUtils.getUserAndPushPreference(activity, reponse);
//                String userS = PreferenceUtil.getInstancePreference(LoginActivity.this).getString(Values.user, "");
//
//                    user = new Gson().fromJson(userS, User.class);
//                    userName = user.getFirstname() + " - "+ random_string;
//                    anonymous = user.getStatus_anonymous();
//                    userName = user.getFirstname();
//                    firstName = user.getFirstname();
//                    lastName = user.getLastname();
//
//                    Log.e("LOG DATA LOGIN", "TOKEN"+PreferenceUtil.getInstancePreference(LoginActivity.this).getString(Values.TOKEN, "")+ "\nUSERNAME : "+user+"\n FNAME : "+firstName+" \n LNAME : "+lastName);
//


//                VolleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                String userS = PreferenceUtil.getInstancePreference(LoginActivity.this).getString(Values.user, "");
//
//
//                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
//                StringBuilder sb1 = new StringBuilder();
//                Random random1 = new Random();
//                for (int i = 0; i < 6; i++)
//                {
//                    char c1 = chars1[random1.nextInt(chars1.length)];
//                    sb1.append(c1);
//                }
//                String random_string = sb1.toString();
//
//
//
//                try {
//                    user = new Gson().fromJson(userS, User.class);
//                    userName = user.getUsername() + " - "+ random_string;
//                    anonymous = user.getStatus_anonymous();
//                    userName = user.getFirstname();
//                    firstName = user.getFirstname();
//                    lastName = user.getLastname();
//                } catch (Exception e) {
//
//                }
//
//                final VolleyUtils volleyUtils = new VolleyUtils(LoginActivity.this, Values.URL_USER_UPDATE_INFO_USER);
//                volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(LoginActivity.this).getString(Values.TOKEN, ""));
//                volleyUtils.addParam(Values.username, userName);
//                volleyUtils.addParam(Values.firstname, firstName);
//                volleyUtils.addParam(Values.lastname, lastName);
//                volleyUtils.addParam(Values.status_anonymous, anonymous + "");
//                String uriAvatar = PreferenceUtil.getInstancePreference(LoginActivity.this).getString(Values.avatar, "");
//                Uri uri = Uri.parse(uriAvatar);
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(LoginActivity.this.getContentResolver(), uri);
//                    int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
//                    ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
//                    bitmap = (Utils.getCroppedBitmap(bitmap));
//                    String avatar = Utils.getBase64Image(Bitmap.createScaledBitmap(bitmap, Values.SIZE_AVATAR, Values.SIZE_AVATAR, true));
//                    volleyUtils.addParam(Values.avatar, avatar);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
//                    @Override
//                    public void onSussces(String reponse, Result result) {
//                        Log.e("UPLOAD S", reponse);
//                        if (VolleyUtils.requestSusscess(reponse)) {
//                            try {
//                                MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(LoginActivity.this, Values.PROJECT_TOKEN_MIXPANEL);
//                                MixpanelAPI.getInstance(LoginActivity.this, Values.PROJECT_TOKEN_MIXPANEL).alias(userName, mixpanelAPI.getDistinctId());
//                            } catch (Exception e) {
//
//                            }
//                            volleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                            Toast.makeText(LoginActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
//
//
//                            Intent intent = new Intent(LoginActivity.this, MyInterestActivity.class);
//                            intent.putExtra("isNew", true);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            if (result.getCode() == 18) {
//                                Toast.makeText(LoginActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        Log.e("UPLOAD R", error);
//                    }
//                });
//                volleyUtils.query();

//                if (result.getCode() != 0) {
//                    Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                try {
//                    JSONObject jsonObject = new JSONObject(reponse);
//                    int code = jsonObject.getInt("r");
//                    if (jsonObject.has("isNew")) {
//                        if (jsonObject.getInt("isNew") == 1) {
//                            volleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                            startActivity(new Intent(LoginActivity.this, InsertProfileActivity.class));
//                            finish();
//                            return;
//                        }
//                    }
//                    if (code == 0) {
//                        volleyUtils.getUserAndPushPreference(LoginActivity.this, reponse);
//                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                        finish();
//                    } else if (code == 9) {
//                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void onError(String error) {
                if (error != null)
                    Log.e("error login facebook:", error);
            }
        });

        volleyUtils.query();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OUR_REQUEST_CODE) {

            // Resolve the intent into a GoogleSignInResult we can process.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "GoogleSignInResult: " + result);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String ss = acct.getServerAuthCode();
            // If you don't already have a server session, you can now send this code to your
            // server to authenticate on the backend.
            String idToken = acct.getIdToken();
            Log.e(TAG, "token gg: " + idToken);
            logInWithGoogle(idToken);

//            Toast.makeText(LoginActivity.this, idToken, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        LoginManager.getInstance().logOut();
        super.onBackPressed();
    }

    public BroadcastReceiver broadcastLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    //M8xlSf1eAsNhkF8y2J6vdPMVtkU=

}
