package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.model.User;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

public class WithdrawActivity extends BaseSearchActivity {
    private int MIN_AMOUT_WITHDRAW = 20;
    private TextViewBold tvTotalEarning;
    private EditText inputAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        tvTotalEarning = (TextViewBold) findViewById(R.id.tvTotalEarning);
        inputAmount = (EditText) findViewById(R.id.inputAmount);
        Utils.setActionBar(this, getString(R.string.withdraw_title), R.drawable.btn_back);

//        Utils.setBottomButton(this, getString(R.string.request), new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                float credit = 0;
//                try {
//                    credit = Float.parseFloat(inputAmount.getText().toString());
//
//                } catch (Exception e) {
//
//                }
//                CreditUtils.withDraw(WithdrawActivity.this, PreferenceUtil.getToken(WithdrawActivity.this), credit, inputEmailPaypal.getText().toString(), new VolleyUtils.OnRequestListenner() {
//                    @Override
//                    public void onSussces(String response, Result result) {
//                        Log.e("response withdraw:", response);
//                        if (VolleyUtils.requestSusscess(response)) {
//                            Toast.makeText(WithdrawActivity.this, getString(R.string.with_draw_success), Toast.LENGTH_SHORT).show();
//                            VolleyUtils.getUserAndPushPreference(WithdrawActivity.this, response);
//                            sendBroadcast(new Intent("change_user"));
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
//            }
//        });

        final User user = User.getInstance(this);
        tvTotalEarning.setText(getString(R.string.total_earning) + " $" + user.getCredit_earnings() + "");
        ((TextViewNormal) findViewById(R.id.tvMinValuesWithdraw)).setText("$" + MIN_AMOUT_WITHDRAW);
        ((RelativeLayout) findViewById(R.id.btnWithDrawPaypal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final DialogBase dialogBase = new DialogBase(WithdrawActivity.this);
//                dialogBase.setMessage(getString(R.string.notice_withdraw_not_allow));
//                dialogBase.setTitle(getString(R.string.notice));
//                dialogBase.getBtnOk().setText("OK");
//                dialogBase.getBtnOk().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogBase.dismiss();
//                    }
//                });
//                dialogBase.getBtnCancel().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogBase.dismiss();
//                    }
//                });
//                dialogBase.show();
                //not allowed
                Intent intent = new Intent(WithdrawActivity.this, WithdrawalPaypalActivity.class);
                float amount = 0;
                try {
                    amount = Float.parseFloat(inputAmount.getText().toString());

                    if (amount > user.getCredit_earnings()) {
                        Toast.makeText(WithdrawActivity.this, getString(R.string.amount_to_large), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (amount < MIN_AMOUT_WITHDRAW) {
                        Toast.makeText(WithdrawActivity.this, String.format(getString(R.string.amount_to_small), (MIN_AMOUT_WITHDRAW) + ""), Toast.LENGTH_SHORT).show();

                        return;
                    }
                    intent.putExtra(Values.WITHDRAW_AMOUNT, amount);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WithdrawActivity.this, "Input correct amount", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        ((Button) findViewById(R.id.btnWithDrawBank)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WithdrawActivity.this, WithdrawalBankActivity.class);
                float amount = 0;
                try {
                    amount = Float.parseFloat(inputAmount.getText().toString());
                    if (amount > user.getCredit_earnings()) {
                        Toast.makeText(WithdrawActivity.this, getString(R.string.amount_to_large), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (amount < MIN_AMOUT_WITHDRAW) {
                        Toast.makeText(WithdrawActivity.this, String.format(getString(R.string.amount_to_small), (MIN_AMOUT_WITHDRAW) + ""), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (user.getCredit_earnings() < MIN_AMOUT_WITHDRAW + 1) {
                        Toast.makeText(WithdrawActivity.this, String.format(getString(R.string.your_earning_min_is), (MIN_AMOUT_WITHDRAW + 1) + ""), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra(Values.WITHDRAW_AMOUNT, amount);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WithdrawActivity.this, "Input correct amount", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }


}
