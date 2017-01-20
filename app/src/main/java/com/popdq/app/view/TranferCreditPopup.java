package com.popdq.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.ui.TransactionViewActivity;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;

/**
 * Created by Dang Luu on 8/30/2016.
 */
public class TranferCreditPopup {
    public interface TranferListener {
        public void onSuccess(float credit);
    }

    public static Dialog showDialogConvert(final Activity activity, final TranferListener tranferListener) {
        final Dialog dialogConvert;
        InputForm inputAmount;
        Button btnCancel, btnConvert;
        dialogConvert = new Dialog(activity);
        dialogConvert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConvert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogConvert.setContentView(R.layout.layout_dialog_convert_credit);
        btnCancel = (Button) dialogConvert.findViewById(R.id.btnCancel);
        btnConvert = (Button) dialogConvert.findViewById(R.id.btnConvert);
        inputAmount = (InputForm) dialogConvert.findViewById(R.id.inputAmount);
        inputAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnCancel.setTypeface(MyApplication.getInstanceTypeNormal(activity));
        btnConvert.setTypeface(MyApplication.getInstanceTypeNormal(activity));
        final InputForm finalInputAmount = inputAmount;
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                float credit = 0;
                try {
                    credit = Float.parseFloat(finalInputAmount.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(activity, "Credit format error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final float finalCredit = credit;
                CreditUtils.convert(activity, PreferenceUtil.getToken(activity), credit, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (VolleyUtils.requestSusscess(response)) {
                            Toast.makeText(activity, "Convert success!", Toast.LENGTH_SHORT).show();
                            VolleyUtils.getUserAndPushPreference(activity, response);
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("change_user_more_tab"));
                            if (dialogConvert != null && dialogConvert.isShowing())
                                dialogConvert.dismiss();
                            if (tranferListener != null) {
                                tranferListener.onSuccess(finalCredit);
                            }
                        } else {
                            Toast.makeText(activity, "Convert error!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                NotificationUtil.sendNotification(activity, activity.getString(R.string.app_name),
                        String.format(activity.getString(R.string.noti_tranfered), finalCredit + ""), TransactionViewActivity.class);

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConvert.dismiss();
            }
        });


        dialogConvert.show();
        return dialogConvert;
    }
}
