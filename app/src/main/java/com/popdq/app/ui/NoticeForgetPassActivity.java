package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.textview.TextViewNormal;

/**
 * Created by Dang Luu on 11/08/2016.
 */
public class NoticeForgetPassActivity extends BaseActivity {
    private InputForm inputPassWord, inputConfirmPassWord, inputCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_reset_pass);
        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.forgot_pass_title).toUpperCase());
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.forgot_pass_title).toUpperCase());

        inputCode = (InputForm) findViewById(R.id.inputCode);
        inputPassWord = (InputForm) findViewById(R.id.inputPassWord);
//        inputConfirmPassWord = (InputForm) findViewById(R.id.inputConfirmPassWord);
        inputPassWord.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//        inputConfirmPassWord.getEdtText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        Utils.hideKeyBoard(this);
        ((Button) findViewById(R.id.btnDone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(NoticeForgetPassActivity.this, LoginWithEmailActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
                String pass = inputPassWord.getText().toString();
//                String rePass = inputConfirmPassWord.getText().toString();
                if (pass.length() < 6) {
                    Toast.makeText(NoticeForgetPassActivity.this, getString(R.string.password_more_than_6), Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (!pass.equals(rePass)) {
//                    Toast.makeText(NoticeForgetPassActivity.this, getString(R.string.not_match_password), Toast.LENGTH_SHORT).show();
//                    return;
//                }

                VolleyUtils volleyUtils = new VolleyUtils(NoticeForgetPassActivity.this, Values.URL_USER_RESET_PASS);
                volleyUtils.addParam(Values.EMAIL, PreferenceUtil.getInstancePreference(NoticeForgetPassActivity.this).getString(Values.EMAIL, ""));
                volleyUtils.addParam(Values.code, inputCode.getText().toString());
                volleyUtils.addParam(Values.password, pass);
                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
                        if (result.getCode() == 0) {
                            Intent intent = new Intent(NoticeForgetPassActivity.this, LoginWithEmailActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(NoticeForgetPassActivity.this, getString(R.string.reset_success) + " " , Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(NoticeForgetPassActivity.this, getString(R.string.error_verification) + " " + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                volleyUtils.query();
            }
        });

        ((Button) findViewById(R.id.btnResend)).setTypeface(MyApplication.getInstanceTypeNormal(this));
        ((Button) findViewById(R.id.btnDone)).setTypeface(MyApplication.getInstanceTypeNormal(this));
//
        ((Button) findViewById(R.id.btnResend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VolleyUtils volleyUtils = new VolleyUtils(NoticeForgetPassActivity.this, Values.URL_USER_FORGOT_PASWORD);
                volleyUtils.addParam(Values.EMAIL, PreferenceUtil.getInstancePreference(NoticeForgetPassActivity.this).getString(Values.EMAIL, ""));
                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
//                        try {
//                            PreferenceUtil.getInstanceEditor(NoticeForgetPassActivity.this).commit();
//                            JSONObject jsonObject = new JSONObject(reponse);
//                            int r = jsonObject.getInt("r");
//                            switch (r) {
//                                case 0:
//                                    startActivity(new Intent(NoticeForgetPassActivity.this, EmailVerifiActivity.class));
//                                    finish();
//                                    break;
//                                default:
                        Toast.makeText(NoticeForgetPassActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                volleyUtils.query();
            }
        });

    }
}
