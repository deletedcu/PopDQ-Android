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

public class WithdrawalBankActivity extends BaseSearchActivity {
    private EditText inputBankName, inputBankAcc, inputYourName;
    private TextViewBold tvTotalEarning;
    private float amountF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_bank);
        Utils.setActionBar(this, getString(R.string.withdraw_title), R.drawable.btn_back);
        inputBankName = (EditText) findViewById(R.id.inputBankName);
        inputBankAcc = (EditText) findViewById(R.id.inputBankAcc);
        inputYourName = (EditText) findViewById(R.id.inputYourName);
        amountF = getIntent().getFloatExtra(Values.WITHDRAW_AMOUNT, -1);
        final String amount = getString(R.string.withdraw_amount) + ": $" + amountF;
        ((TextViewBold) findViewById(R.id.tvWithdrawAmount)).setText(amount);
        ((TextViewNormal) findViewById(R.id.tvMinValuesWithdraw)).setText("$" + 20);
        ((TextViewBold) findViewById(R.id.tvTotalAmount)).setText(getString(R.string.total_withdraw_amount) + ": $" + User.getInstance(this).getCredit_earnings() + "");

        Utils.setBottomButton(this, getString(R.string.request), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bankAcc = inputBankAcc.getText().toString();
                String bankName = inputBankName.getText().toString();

                String yourName = inputYourName.getText().toString();
                final String token = PreferenceUtil.getToken(WithdrawalBankActivity.this);

                if (bankAcc.length() <= 0 || bankName.length() <= 0 || yourName.length() <= 0) {
                    Toast.makeText(WithdrawalBankActivity.this, getString(R.string.all_field_requried), Toast.LENGTH_SHORT).show();
                    return;
                }

                confirmWithDraw(token, bankName, bankAcc, yourName);
            }
        });
    }

    public void confirmWithDraw(final String token, final String bankName, final String bankAcc, final String yourName) {
        final DialogBase dialogBase = new DialogBase(WithdrawalBankActivity.this);
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

                CreditUtils.withDrawBank(WithdrawalBankActivity.this, token, bankName, bankAcc, yourName, amountF, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (result.getCode() == 0) {

                            UserUtil.getMyProfile(WithdrawalBankActivity.this, token, new VolleyUtils.OnRequestListenner() {
                                @Override
                                public void onSussces(String response, Result result) {
                                    VolleyUtils.getUserAndPushPreference(WithdrawalBankActivity.this, response);
                                    LocalBroadcastManager.getInstance(WithdrawalBankActivity.this).sendBroadcast(new Intent("change_user_more_tab"));
                                    showDialogSucces();

                                }

                                @Override
                                public void onError(String error) {

                                }
                            });

                        } else if(result.getCode()==32){
                            Toast.makeText(WithdrawalBankActivity.this, getString(R.string.credit_too_small), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(WithdrawalBankActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
        final DialogBase dialogBase = new DialogBase(WithdrawalBankActivity.this);
        dialogBase.setTitle(getString(R.string.title_submit_succes));
        dialogBase.setMessage(getString(R.string.des_submit_succes));
        dialogBase.getBtnCancel().setVisibility(View.GONE);
        dialogBase.getBtnOk().setText("OK");
        dialogBase.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
                Intent intent = new Intent(WithdrawalBankActivity.this, TransactionViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBase.show();
    }
}
