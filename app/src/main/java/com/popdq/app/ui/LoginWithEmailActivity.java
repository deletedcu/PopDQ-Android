package com.popdq.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.popdq.app.R;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.MyLayout;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.Arrays;
import java.util.List;

public class LoginWithEmailActivity extends BaseSearchActivity implements View.OnClickListener {

    private static final String TAG = "LoginWithEmailActivity";
    private TextViewBold btnForgotPass;
    private Button btnLogin;
    private InputForm inputEmail, inputPassWord;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    private String requestIdToken = "698177898809-pj0vpko7tp65pvcsi904npeogshnqav0.apps.googleusercontent.com";
    private static final int OUR_REQUEST_CODE = 5;
    private ScrollView sv;

//    // Threshold for minimal keyboard height.
//    final int MIN_KEYBOARD_HEIGHT_PX = 150;
//    // Top-level window decor view.
//    final View decorView = this.getWindow().getDecorView();

//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);


        ((MyLayout)findViewById(R.id.layout)).setOnSoftKeyboardListener(new MyLayout.OnSoftKeyboardListener() {
            @Override
            public void onShown() {
                findViewById(R.id.logo).setVisibility(View.GONE);
                findViewById(R.id.option).setVisibility(View.GONE);
            }

            @Override
            public void onHidden() {
                findViewById(R.id.logo).setVisibility(View.VISIBLE);
                findViewById(R.id.option).setVisibility(View.VISIBLE);
            }
        });

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            private final Rect windowVisibleDisplayFrame = new Rect();
//            private int lastVisibleDecorViewHeight;
//
//            @Override
//            public void onGlobalLayout() {
//                // Retrieve visible rectangle inside window.
//                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
//                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();
//
//                // Decide whether keyboard is visible from changing decor view height.
//                if (lastVisibleDecorViewHeight != 0) {
//                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
//                        // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
//                        int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
//                        // Notify listener about keyboard being shown.
//                        listener.onKeyboardShown(currentKeyboardHeight);
//                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
//                        // Notify listener about keyboard being hidden.
//                        listener.onKeyboardHidden();
//                    }
//                }
//                // Save current decor view height for the next call.
//                lastVisibleDecorViewHeight = visibleDecorViewHeight;
//            }
//        });


        btnForgotPass = (TextViewBold) findViewById(R.id.btnForgotPass);
        btnForgotPass.setOnClickListener(this);
        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.login).toUpperCase());
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.login_with_email));
        ((LinearLayout) findViewById(R.id.btnLoginWithFb)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnLoginWithGoogle)).setOnClickListener(this);
        sv = (ScrollView)findViewById(R.id.scrolllogin);

        callbackManager = CallbackManager.Factory.create();
        inputEmail = (InputForm) findViewById(R.id.inputEmail);
        inputPassWord = (InputForm) findViewById(R.id.inputPassWord);
        inputPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

//        inputEmail.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
//                    sv.smoothScrollTo(0, sv.getMaxScrollAmount());
//                }
//            }
//        });
//
//        inputPassWord.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
//                    sv.smoothScrollTo(0, sv.getMaxScrollAmount());
//                }
//            }
//        });

//        inputEmail.getEdtText().setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);
        inputEmail.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    inputEmail.getEdtText().clearFocus();
                    inputPassWord.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

        inputPassWord.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
//        inputPassWord.getEdtText().setImeActionLabel("Log In", KeyEvent.KEYCODE_ENTER);

//        inputPassWord.getEdtText().setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);

        inputPassWord.getEdtText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        inputPassWord.getEdtText().setImeActionLabel(getString(R.string.done), EditorInfo.IME_ACTION_DONE);
        inputPassWord.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    hideSoftKeyboard(LoginWithEmailActivity.this);

                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        ((TextViewBold) findViewById(R.id.btnSignUp)).setOnClickListener(this);
        inputEmail.setText(PreferenceUtil.getInstancePreference(this).getString("email", ""));
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

    }

    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    public void getDataGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, OUR_REQUEST_CODE);
    }

    public void login() {
        final String email = inputEmail.getText();
        String pass = inputPassWord.getText();

//                String email = "danglh@azstack.com";
//                String pass = "123456";

        PreferenceUtil.getInstanceEditor(this).putString(Values.password, pass);
        PreferenceUtil.getInstanceEditor(this).commit();

        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_LOGIN_WITH_EMAIL);
        Log.e("Login email", String.valueOf(Values.URL_USER_LOGIN_WITH_EMAIL));
        volleyUtils.addParam("email", email);
        volleyUtils.addParam("password", pass);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                Log.e("respone login emnail:", reponse);
                if (result.getCode() == 13) {// password incorrect
                    Toast.makeText(LoginWithEmailActivity.this, getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
                }
                if (result.getCode() == 46) {// user not exist
                    Toast.makeText(LoginWithEmailActivity.this, getString(R.string.user_not_exist), Toast.LENGTH_SHORT).show();
                } else if (result.getCode() != 0) {
                    Toast.makeText(LoginWithEmailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }

                LoginUtil.loginSuccess(LoginWithEmailActivity.this, reponse);
                if (result.getCode() == 0) {
                    PreferenceUtil.getInstanceEditor(LoginWithEmailActivity.this).putString(Values.email, email);
                    PreferenceUtil.getInstanceEditor(LoginWithEmailActivity.this).commit();
                }
//                        if (result.getCode() == 0) {
//                            PreferenceUtil.getInstanceEditor(LoginWithEmailActivity.this).putString("email", email);
//                            PreferenceUtil.getInstanceEditor(LoginWithEmailActivity.this).commit();
//                            Intent intent = new Intent("login");
//                            sendBroadcast(intent);
//                            volleyUtils.getUserAndPushPreference(LoginWithEmailActivity.this, reponse);
//                            startActivity(new Intent(LoginWithEmailActivity.this, HomeActivity.class));
//                            finish();
//                        }else {
//                            Toast.makeText(LoginWithEmailActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
//                        }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginWithEmailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        volleyUtils.query();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnForgotPass:
                startActivity(new Intent(this, ResetPassActivity.class));
                break;
            case R.id.btnSignUp:
                startActivity(new Intent(this, SignupActivity.class));

                break;

            case R.id.btnLoginWithFb:
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

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
                List<String> permissionNeeds = Arrays.asList(new String[]{"public_profile", "user_friends"});
                LoginManager.getInstance().logInWithReadPermissions(this,
                        permissionNeeds);

                break;
            case R.id.btnLoginWithGoogle:
                getDataGoogle();

        }

    }

    public void logInWithFb(String access_token) {
        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_LOGIN_WITH_FB);
        volleyUtils.addParam(Values.access_token, access_token);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                Log.e("respone login facebook:", reponse);
                LoginUtil.loginSuccess(LoginWithEmailActivity.this, reponse);
            }

            @Override
            public void onError(String error) {
                Log.e("error login facebook:", error);
            }
        });
        volleyUtils.query();
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
                LoginUtil.loginSuccess(LoginWithEmailActivity.this, reponse);
            }

            @Override
            public void onError(String error) {
                if (error != null)
                    Log.e("error login google:", error);
            }
        });
        volleyUtils.query();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OUR_REQUEST_CODE) {

            // Resolve the intent into a GoogleSignInResult we can process.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }

}

