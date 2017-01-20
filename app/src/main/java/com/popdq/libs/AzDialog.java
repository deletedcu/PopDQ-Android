package com.popdq.libs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.popdq.app.R;

public class AzDialog extends Dialog {

    private TextView tvContent;
    private TextView btnNegative;
    private TextView btnPositive;
    private boolean isCancelable = true;

    public AzDialog(Context context) {
        super(context, R.style.AzStack_Theme_Dialog);
        setContentView(R.layout.azstack_dialog);

        View vForm = findViewById(R.id.v_form);
        vForm.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isCancelable) {
                    dismiss();
                }
            }
        });

        View vDialog = findViewById(R.id.v_dialog);
        vDialog.setOnClickListener(null);
        tvContent = (TextView) findViewById(R.id.tv_content);
        btnNegative = (TextView) findViewById(R.id.btn_negative);
        btnPositive = (TextView) findViewById(R.id.btn_positive);
    }

    public void setContent(String content) {
        tvContent.setText(content);
    }

    public void setNegative(String label, final BtnDialogListener listener) {
        btnNegative.setText(label);
        btnNegative.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick();
                }
                dismiss();
            }
        });
    }

    public void setPositive(String label, final BtnDialogListener listener) {
        btnPositive.setText(label);
        btnPositive.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick();
                }
                dismiss();
            }
        });
    }

    public interface BtnDialogListener {
        public void onClick();
    }

    public void setCancelable(boolean isCancelable) {
        super.setCancelable(isCancelable);
        this.isCancelable = isCancelable;
    }

}
