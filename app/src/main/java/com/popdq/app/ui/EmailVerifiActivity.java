package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.google.gson.Gson;

public class EmailVerifiActivity extends BaseSearchActivity {
    private EditText edtCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verifi);
        edtCode = (EditText) findViewById(R.id.edtCode);
        edtCode.setTypeface(MyApplication.getInstanceTypeNormal(this));

        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.verifi_email));
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.verifi_email));

        ((Button) findViewById(R.id.btnDone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtCode.getText().toString();
                if (code.length() <= 0) {
                    Toast.makeText(EmailVerifiActivity.this, getString(R.string.verification_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                VolleyUtils volleyUtils = new VolleyUtils(EmailVerifiActivity.this, Values.URL_USER_EMAIL_VERIFY);
                volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(EmailVerifiActivity.this).getString(Values.TOKEN, ""));
                volleyUtils.addParam(Values.code, edtCode.getText().toString());
                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
                        if (result.getCode() == 0) {
//                                Intent intent = new Intent(EmailVerifiActivity.this, InsertProfileActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
                            finish();
                            User user = User.getInstance(EmailVerifiActivity.this);
                            user.setEmail_verified(1);
                            PreferenceUtil.getInstanceEditor(EmailVerifiActivity.this).putString(Values.user, new Gson().toJson(user));
                            PreferenceUtil.getInstanceEditor(EmailVerifiActivity.this).commit();
                            startActivity(new Intent(EmailVerifiActivity.this, WithdrawActivity.class));

                        } else {
                            Toast.makeText(EmailVerifiActivity.this, getString(R.string.error_verification) + " " + result.getMessage(), Toast.LENGTH_SHORT).show();
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

        ((Button) findViewById(R.id.btnResend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyUtils volleyUtils = new VolleyUtils(EmailVerifiActivity.this, Values.URL_USER_SEND_CODE_VERIFY);
                volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(EmailVerifiActivity.this).getString(Values.TOKEN, ""));
                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
//                        if (result.getCode() == 0) {
//                            startActivity(new Intent(EmailVerifiActivity.this, InsertProfileActivity.class));
//                        } else {
                        Toast.makeText(EmailVerifiActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        if (getIntent().hasExtra("not_activate")) {
            LoginUtil.logOut(EmailVerifiActivity.this, null);
        }
        super.onDestroy();

    }
}
