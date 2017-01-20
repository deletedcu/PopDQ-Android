package com.popdq.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.popdq.app.R;
import com.popdq.app.model.User;
import com.popdq.app.view.textview.TextViewNormal;

/**
 * Created by Dang Luu on 8/30/2016.
 */
public class DialogNoticeCredit {

    public interface OnClickDialogListener {
        public void onClickOk();
    }

    public static Dialog showDialogAskOrTranfer(Activity activity, String title, String message, final OnClickDialogListener okClick) {
        User user = User.getInstance(activity);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog_notice_ask);
        ((TextViewNormal) dialog.findViewById(R.id.tvTitle)).setText(title);
        ((TextViewNormal) dialog.findViewById(R.id.tvTotalCredit)).setText("$" + String.format("%.2f", user.getCredit()));
        ((TextViewNormal) dialog.findViewById(R.id.tvMessage)).setText(message);
        ((Button) dialog.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (okClick != null) {
                    okClick.onClickOk();
                }
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
}
