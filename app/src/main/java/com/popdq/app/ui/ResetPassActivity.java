package com.popdq.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.textview.TextViewNormal;

public class ResetPassActivity extends BaseActivity {
    private InputForm edtEmail;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.reset));
        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.reset_password));
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ((Button) findViewById(R.id.btnReset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = edtEmail.getText().toString();
                VolleyUtils volleyUtils = new VolleyUtils(ResetPassActivity.this, Values.URL_USER_FORGOT_PASWORD);
                volleyUtils.addParam(Values.EMAIL, email);
                volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {

                        PreferenceUtil.getInstanceEditor(ResetPassActivity.this).putString(Values.EMAIL, email);
                        PreferenceUtil.getInstanceEditor(ResetPassActivity.this).commit();

                        switch (result.getCode()) {
                            case 0:
                                startActivity(new Intent(ResetPassActivity.this, NoticeForgetPassActivity.class));
                                finish();
                                break;
                            default:
                                Toast.makeText(ResetPassActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                volleyUtils.query();

            }
        });

        edtEmail = (InputForm) findViewById(R.id.inputEmail);
        edtEmail.getEdtText().setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    v.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            scrollView.scrollBy(0, 150);

                        }
                    }, 500);
                }


            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}
