package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

public class WithdrawalPaypalActivity extends BaseSearchActivity {
    private EditText inputEmailPaypal, inputEmailPaypalConfirm;
    private float amountF;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_paypal);
        Utils.setActionBar(this, getString(R.string.withdraw_title), R.drawable.btn_back);

        inputEmailPaypal = (EditText) findViewById(R.id.inputEmailPaypal);
        inputEmailPaypalConfirm = (EditText) findViewById(R.id.inputEmailPaypalConfirm);
        amountF = getIntent().getFloatExtra(Values.WITHDRAW_AMOUNT, -1);

        final String amount = getString(R.string.withdraw_amount) + ": " + amountF;
        ((TextViewBold) findViewById(R.id.tvWithdrawAmount)).setText(amount);
        ((TextViewBold) findViewById(R.id.tvTotalAmount)).setText(getString(R.string.total_withdraw_amount) + ": " + User.getInstance(this).getCredit_earnings() + "");
        ((TextViewNormal) findViewById(R.id.tvMinValuesWithdraw)).setText("$" + 20);

        Utils.setBottomButton(this, getString(R.string.request), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmailPaypal.getText().toString();
                if (email.length() <= 0) {
                    Toast.makeText(WithdrawalPaypalActivity.this, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.equals(inputEmailPaypalConfirm.getText().toString())) {
                    confirmWithDraw();

                } else {
                    Toast.makeText(WithdrawalPaypalActivity.this, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });

    }

    public void confirmWithDraw() {
        final DialogBase dialogBase = new DialogBase(WithdrawalPaypalActivity.this);
        dialogBase.setTitle(getString(R.string.notice));
        dialogBase.setMessage(String.format(getString(R.string.des_confirm_withdrawal), (amountF + 1) + ""));
        dialogBase.getBtnCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
            }
        });
        dialogBase.getBtnOk().setText("OK");
        dialogBase.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
                final String token = PreferenceUtil.getToken(WithdrawalPaypalActivity.this);
                CreditUtils.withDrawPaypal(WithdrawalPaypalActivity.this, token, amountF, email, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (result.getCode() == 0) {
                            UserUtil.getMyProfile(WithdrawalPaypalActivity.this, token, new VolleyUtils.OnRequestListenner() {
                                @Override
                                public void onSussces(String response, Result result) {
                                    VolleyUtils.getUserAndPushPreference(WithdrawalPaypalActivity.this, response);
                                    LocalBroadcastManager.getInstance(WithdrawalPaypalActivity.this).sendBroadcast(new Intent("change_user_more_tab"));
                                    showDialogSucces();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });

                        } else if (result.getCode() == 32) {
                            Toast.makeText(WithdrawalPaypalActivity.this, getString(R.string.credit_too_small), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WithdrawalPaypalActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
        dialogBase.show();


    }

    public void showDialogSucces() {
        final DialogBase dialogBase = new DialogBase(WithdrawalPaypalActivity.this);
        dialogBase.setTitle(getString(R.string.title_submit_succes));
        dialogBase.setMessage(getString(R.string.des_submit_succes));
        dialogBase.getBtnCancel().setVisibility(View.GONE);
        dialogBase.getBtnOk().setText("OK");
        dialogBase.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
                Intent intent = new Intent(WithdrawalPaypalActivity.this, TransactionViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBase.show();
    }
}
