package com.popdq.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.textview.TextViewNormal;

public class SignupActivityBackup extends BaseSearchActivity {

    private InputForm edtEmail, edtPassWord, edtPassWordConfirm,edtUserName;
    private ProgressDialog progressDialog;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.sign_up));
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.sign_up));
        sv = (ScrollView) findViewById(R.id.scroll);

        ((Button) findViewById(R.id.btnSignUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });

        edtEmail = (InputForm) findViewById(R.id.edtEmail);
        edtPassWord = (InputForm) findViewById(R.id.edtPassWord);
        edtUserName = (InputForm) findViewById(R.id.edtUsername);
        edtPassWordConfirm = (InputForm) findViewById(R.id.edtPassWordConfirm);
        edtPassWord.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtPassWordConfirm.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//        edtUserName.getEdtText().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(MotionEvent.ACTION_UP == event.getAction())
////                    Toast.makeText(getApplicationContext(), "onTouch: Down", Toast.LENGTH_SHORT).show();
//                    sv.scrollTo(0, sv.getBottom());
//                return false;
//            }
//        });
//
//
//        edtEmail.getEdtText().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(MotionEvent.ACTION_UP == event.getAction())
////                    Toast.makeText(getApplicationContext(), "onTouch: Down", Toast.LENGTH_SHORT).show();
//                    sv.scrollTo(0, sv.getBottom());
//                return false;
//            }
//        });
//
//        edtPassWord.getEdtText().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(MotionEvent.ACTION_UP == event.getAction())
////                    Toast.makeText(getApplicationContext(), "onTouch: Down", Toast.LENGTH_SHORT).show();
//                    sv.scrollTo(0, sv.getBottom());
//                return false;
//            }
//        });
//
//        edtPassWordConfirm.getEdtText().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(MotionEvent.ACTION_UP == event.getAction())
////                    Toast.makeText(getApplicationContext(), "onTouch: Down", Toast.LENGTH_SHORT).show();
//                    sv.scrollTo(0, sv.getBottom());
//                return false;
//            }
//        });

        edtEmail.getEdtText().setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        edtEmail.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getBottom());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtEmail.getEdtText().clearFocus();
                    edtPassWord.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

        edtUserName.getEdtText().setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        edtUserName.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getBottom());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtUserName.getEdtText().clearFocus();
                    edtEmail.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


        edtPassWord.getEdtText().setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        edtPassWord.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getBottom());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtPassWord.getEdtText().clearFocus();
                    edtPassWordConfirm.getEdtText().requestFocus();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


        edtPassWordConfirm.getEdtText().setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);

        edtPassWordConfirm.getEdtText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sv.scrollTo(0, sv.getBottom());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideSoftKeyboard(SignupActivityBackup.this);
                    submit();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });



//        edtPassWordConfirm.getEdtText().setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_DPAD_CENTER:
//                        case KeyEvent.KEYCODE_ENTER:
//                            submit();
//                            return true;
//                        default:
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
    }

    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void submit() {
        progressDialog = new ProgressDialog(SignupActivityBackup.this);

        String passWord = edtPassWord.getText().toString();
//                String rePassWord = edtConfirmPassWord.getText().toString();
        String email = edtEmail.getText().toString();

        String userName = edtUserName.getText().toString();

        String passWordConfirm = edtPassWordConfirm.getText().toString();

        if (userName.length() <= 0) {
            Toast.makeText(SignupActivityBackup.this, getString(R.string.please_input_user_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName.length() < 5) {
            Toast.makeText(SignupActivityBackup.this, getString(R.string.min_values_username), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(SignupActivityBackup.this, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passWordConfirm.equals(passWord)) {
            Toast.makeText(SignupActivityBackup.this, getString(R.string.not_match_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (passWord.length() > 20 || passWord.length() < 6) {
            Toast.makeText(SignupActivityBackup.this, getString(R.string.password_more_than_6), Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

//                if (!passWord.equals(rePassWord)) {
//                    Toast.makeText(SignupActivity.this, getString(R.string.not_match_password), Toast.LENGTH_SHORT).show();
//                    return;
//                }

        VolleyUtils volleyUtils = new VolleyUtils(SignupActivityBackup.this, Values.URL_USER_SIGNUP_WITH_EMAIL);
        volleyUtils.addParam(Values.EMAIL, edtEmail.getText().toString());
        volleyUtils.addParam(Values.PASSWORD, edtPassWord.getText().toString());
        volleyUtils.addParam(Values.username, edtUserName.getText().toString());
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {

//                    JSONObject jsonObject = new JSONObject(reponse);
//                    Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                if (VolleyUtils.requestSusscess(reponse)) {
                    if (result.getCode() == 0) {
//                        startActivity(new Intent(SignupActivity.this, InsertProfileActivity.class));
                        try {
                            MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(SignupActivityBackup.this, Values.PROJECT_TOKEN_MIXPANEL);
                            MixpanelAPI.getInstance(SignupActivityBackup.this, Values.PROJECT_TOKEN_MIXPANEL).alias(edtUserName.getText().toString(), mixpanelAPI.getDistinctId());
                        } catch (Exception e) {

                        }

                        Intent intent = new Intent(SignupActivityBackup.this, MyInterestActivity.class);
                        intent.putExtra("isNew", true);
                        startActivity(intent);
                        VolleyUtils.getUserAndPushPreference(SignupActivityBackup.this, reponse);
//                        finish();

                    }
                    if (result.getCode() == 21) {
                        //email exist
                        Toast.makeText(SignupActivityBackup.this, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
                        edtEmail.setText("");
                        edtPassWord.setText("");
                        edtPassWordConfirm.setText("");
                        startActivity(new Intent(SignupActivityBackup.this, LoginWithEmailActivity.class));
                        finish();
                        return;
                    }

                    if (result.getCode() == 9) {
                        Toast.makeText(SignupActivityBackup.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SignupActivityBackup.this, getString(R.string.not_successful), Toast.LENGTH_SHORT).show();
            }
        });
        volleyUtils.query();

    }
}
