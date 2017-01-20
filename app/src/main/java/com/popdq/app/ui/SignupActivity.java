package com.popdq.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.MyLayout;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

public class SignupActivity extends BaseSearchActivity {

    private InputForm edtEmail, edtPassWord, edtPassWordConfirm,edtUserName;
    private ProgressDialog progressDialog;
    ScrollView sv;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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


        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.sign_up));
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.sign_up));

//        ((TextViewBold) findViewById(R.id.btnSignUp)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SignupActivity.this, LoginWithEmailActivity.class));
//            }
//        });f

        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        edtEmail = (InputForm) findViewById(R.id.edtEmail);
        edtPassWord = (InputForm) findViewById(R.id.edtPassWord);
        edtUserName = (InputForm) findViewById(R.id.edtUsername);
        edtPassWordConfirm = (InputForm) findViewById(R.id.edtPassWordConfirm);
//        sv = (ScrollView) findViewById(R.id.scroll);

        edtPassWord.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtPassWordConfirm.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//        edtEmail.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
////                    sv.smoothScrollTo(0,550);
////                    edtEmail.getEdtText().requestFocus();
//                }
//            }
//        });
//
//        edtPassWord.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
////                    sv.smoothScrollTo(0, sv.getMaxScrollAmount());
//                    edtPassWord.getEdtText().requestFocus();
//                }
//            }
//        });
//
//        edtUserName.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
////                    sv.smoothScrollTo(0, sv.getMaxScrollAmount());
//                    edtUserName.getEdtText().requestFocus();
//                }
//            }
//        });
//
//        edtPassWordConfirm.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
////                    sv.smoothScrollTo(0, sv.getMaxScrollAmount());
//                    edtPassWordConfirm.getEdtText().requestFocus();
//                }
//            }
//        });

//        edtUserName.getEdtText().setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);
        edtUserName.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtUserName.getEdtText().clearFocus();
                    edtEmail.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

//        edtEmail.getEdtText().setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);
        edtEmail.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtEmail.getEdtText().clearFocus();
                    edtPassWord.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });



//        edtPassWord.getEdtText().setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);
        edtPassWord.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtPassWord.getEdtText().clearFocus();
                    edtPassWordConfirm.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

        edtPassWordConfirm.getEdtText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtPassWordConfirm.getEdtText().setImeActionLabel(getString(R.string.done), EditorInfo.IME_ACTION_DONE);
        edtPassWordConfirm.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard(SignupActivity.this);
                    submit();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


    }

    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void submit() {
        progressDialog = new ProgressDialog(SignupActivity.this);

        String passWord = edtPassWord.getText().toString();
//                String rePassWord = edtConfirmPassWord.getText().toString();
        String email = edtEmail.getText().toString().trim();

        String userName = edtUserName.getText().toString();

        String passWordConfirm = edtPassWordConfirm.getText().toString();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\..+[a-z]+";

        if (userName.length() <= 0) {
            Toast.makeText(SignupActivity.this, getString(R.string.please_input_user_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName.length() < 5) {
            Toast.makeText(SignupActivity.this, getString(R.string.min_values_username), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(SignupActivity.this, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(SignupActivity.this, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
            return;
        }


        if (!passWordConfirm.equals(passWord)) {
            Toast.makeText(SignupActivity.this, getString(R.string.not_match_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (passWord.length() > 20 || passWord.length() < 6) {
            Toast.makeText(SignupActivity.this, getString(R.string.password_more_than_6), Toast.LENGTH_SHORT).show();
            return;
        }
//        progressDialog.setMessage(getString(R.string.loading));}
        progressDialog.setMessage(getString(R.string.popping));
        progressDialog.show();

//                if (!passWord.equals(rePassWord)) {
//                    Toast.makeText(SignupActivity.this, getString(R.string.not_match_password), Toast.LENGTH_SHORT).show();
//                    return;
//                }

        VolleyUtils volleyUtils = new VolleyUtils(SignupActivity.this, Values.URL_USER_SIGNUP_WITH_EMAIL);
        volleyUtils.addParam(Values.EMAIL, edtEmail.getText().toString());
        volleyUtils.addParam(Values.PASSWORD, edtPassWord.getText().toString());
        volleyUtils.addParam(Values.username, edtUserName.getText().toString());
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {

//                    JSONObject jsonObject = new JSONObject(reponse);
//                    Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();


                Log.e("Result code = ", String.valueOf(result.getCode()));

//                if (result.getCode() == 21) {
//                    //email exist
//                        Toast.makeText(SignupActivity.this, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
//                        edtEmail.setText("");
//                        edtPassWord.setText("");
//                        edtPassWordConfirm.setText("");
////                        startActivity(new Intent(SignupActivity.this, LoginWithEmailActivity.class));
////                        finish();
//
////                    Toast.makeText(SignupActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
////                        return;
//                }


                progressDialog.dismiss();
                if (VolleyUtils.requestSusscess(reponse)) {

//                    Log.e("Result code = ", String.valueOf(result.getCode()));

                    if (result.getCode() == 0) {
//                        startActivity(new Intent(SignupActivity.this, InsertProfileActivity.class));
                        try {
                            MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(SignupActivity.this, Values.PROJECT_TOKEN_MIXPANEL);
                            MixpanelAPI.getInstance(SignupActivity.this, Values.PROJECT_TOKEN_MIXPANEL).alias(edtUserName.getText().toString(), mixpanelAPI.getDistinctId());
                        } catch (Exception e) {

                        }

//                        Intent intent = new Intent(SignupActivity.this, MyInterestActivity.class);
//                        intent.putExtra("isNew", true);
//                        startActivity(intent);
                        VolleyUtils.getUserAndPushPreference(SignupActivity.this, reponse);

                        Log.e("IS NEW USER", "TRUE");
                        NotificationUtil.sendNotification(SignupActivity.this, getString(R.string.noti_wellcome_title),
                                getString(R.string.noti_wellcome_des), MainActivity.class);
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        regsiterNotif();
                        startActivity(intent);
                        finish();
//                        finish();

                    }
//                    if (result.getCode() == Integer.valueOf(": 21") || result.getCode() == Integer.valueOf(" 21") ||
//                            result.getCode() == Integer.valueOf("21")) {
//                        //email exist
//                        Toast.makeText(SignupActivity.this, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
//                        edtEmail.setText("");
//                        edtPassWord.setText("");
//                        edtPassWordConfirm.setText("");
//                        startActivity(new Intent(SignupActivity.this, LoginWithEmailActivity.class));
//                        finish();
//
////                        Toast.makeText(SignupActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
////                        return;
//                    }

                    if (result.getCode() == 14) {
                        Toast.makeText(SignupActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SignupActivity.this, getString(R.string.not_successful), Toast.LENGTH_SHORT).show();
            }
        });
        volleyUtils.query();

    }

    private void regsiterNotif() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString(Values.TOKEN, "");

        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_PUSH_NOTIFICATION);
        volleyUtils.addParam(Values.TOKEN, token);
//        volleyUtils.addParam(Values.newInterest, newList);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {

//                Toast.makeText(MyInterestActivity.this, response, Toast.LENGTH_SHORT).show();
                if (VolleyUtils.requestSusscess(response)) {
//                    if (getIntent().hasExtra("isNew")) {
//                        NotificationUtil.sendNotification(MyInterestActivity.this, getString(R.string.noti_wellcome_title),
//                                getString(R.string.noti_wellcome_des), MainActivity.class);
//                        Intent intent = new Intent(MyInterestActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent("update_interest");
//                        LocalBroadcastManager.getInstance(MyInterestActivity.this).sendBroadcast(intent);
//                        finish();
//
//                    }


                }

            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();

    }
}
