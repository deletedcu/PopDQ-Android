package com.popdq.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.util.Utils;
import com.popdq.app.view.textview.TextViewNormal;

/**
 * Created by Dang Luu on 9/5/2016.
 */
public class ReportDialog extends Dialog {
    private static final String TAG = "ReportDialog";
    private Activity context;
    //    private View.OnClickListener btnOkClickListener;
    private Button btnOk;
    private TextViewNormal tvTitle, tvMessage;
    private RadioButton radioButton;
    private String messageReport;
    private EditText edtMessageReport;
    private RadioGroup radioGroup;
    private RadioButton radioButtonOthers;

    public ReportDialog(Activity context) {
        super(context);
        this.context = context;
        init();
    }

    public ReportDialog setTitleDialog(String title) {
        ((TextViewNormal) findViewById(R.id.tvTitle)).setText(title);
        return this;
    }

    public void setBtnOkClickListener(View.OnClickListener btnOkClickListener) {
//        this.btnOkClickListener = btnOkClickListener;
        btnOk.setOnClickListener(btnOkClickListener);
    }

    public String getMessageReport() {
        if (radioButtonOthers.isChecked())
            messageReport = edtMessageReport.getText().toString();
        return messageReport;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }


    public Button getBtnOk() {
        return btnOk;
    }

    public void init() {
//        final Dialog dialog = new Dialog(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.layout_dialog_notice_report_question);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonOthers = (RadioButton) findViewById(R.id.radioButtonOthers);
        edtMessageReport = (EditText) findViewById(R.id.edtMessageReport);
        tvMessage = (TextViewNormal) findViewById(R.id.tvMessage);
        tvTitle = (TextViewNormal) findViewById(R.id.tvTitle);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e(TAG, i + "");
                radioButtonOthers.setChecked(false);
                radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                if (radioButton != null)
                    messageReport = radioButton.getText().toString();
            }
        });
        radioButtonOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    edtMessageReport.requestFocus();
                    Utils.showKeyBoard(context);
                    radioGroup.clearCheck();
                    radioButtonOthers.setChecked(b);
                    messageReport = edtMessageReport.getText().toString();
                    Log.e(TAG, "onCheckedChanged " + messageReport);
                }
            }
        });
//        final EditText edtContenReport = (EditText) dialog.findViewById(R.id.edtContenReport);
//        edtContenReport.setTypeface(MyApplication.getInstanceTypeNormal(this));

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);

        btnOk.setTypeface(MyApplication.getInstanceTypeNormal(context));
        btnCancel.setTypeface(MyApplication.getInstanceTypeNormal(context));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (dialog != null && dialog.isShowing()) {
                dismiss();
//                }
            }
        });
//        btnOk.setOnClickListener(btnOkClickListener);
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (radioButtonOthers.isChecked())
//                    messageReport = edtMessageReport.getText().toString();
//
//                QuestionUtil.report(ContentQuestionActivity.this, token, question_id, messageReport, new VolleyUtils.OnRequestListenner() {
//                    @Override
//                    public void onSussces(String reponse, Result result) {
//                        Log.e(TAG, " message:" + messageReport);
//                        Toast.makeText(ContentQuestionActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
//            }
//        });
        show();
    }
}
