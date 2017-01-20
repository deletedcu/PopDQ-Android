package com.popdq.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.popdq.app.R;
import com.popdq.app.view.textview.TextViewNormal;

/**
 * Created by Dang Luu on 03/08/2016.
 */
public class DialogBase extends Dialog {
    private TextViewNormal tvTitle, tvMessage;
    private Button btnCancel, btnOk;
    private View.OnClickListener ok, cancel;

    public DialogBase(Context context) {
        super(context);
        init();

    }

    public DialogBase(Context context, int themeResId) {
        super(context, themeResId);
        init();

    }

    protected DialogBase(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();

    }

    public TextViewNormal getTvTitle() {
        return tvTitle;
    }

    public TextViewNormal getTvMessage() {
        return tvMessage;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    public View.OnClickListener getOk() {
        return ok;
    }

    public View.OnClickListener getCancel() {
        return cancel;
    }

    public void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.layout_dialog_notice_view_answer);
        tvMessage = (TextViewNormal) findViewById(R.id.tvMessage);
        tvTitle = (TextViewNormal) findViewById(R.id.tvTitle);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);

        if (ok != null)
            btnOk.setOnClickListener(ok);
        if (cancel != null)
            btnCancel.setOnClickListener(cancel);

    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setOnClickOkListener(final View.OnClickListener onClickOkListener) {
        ok = onClickOkListener;
        if (onClickOkListener != null) {
            btnOk.setOnClickListener(onClickOkListener);

        }
    }

    public void setOnClickCancelListener(final  View.OnClickListener cancel) {
        this.cancel = cancel;
        if (cancel != null) {
            btnCancel.setOnClickListener(cancel);
        }

    }

    public void setTextOk(String textOk) {
        if (btnOk != null)
            btnOk.setText(textOk);
    }
}
