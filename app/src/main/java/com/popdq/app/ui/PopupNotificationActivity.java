package com.popdq.app.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewThin;

public class PopupNotificationActivity extends BaseActivity {
    private int type = -1;
    private long question_id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final Intent intent = getIntent();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_notification_small);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        if (intent.hasExtra(Values.message)) {
            ((TextViewThin) dialog.findViewById(R.id.tvMessage)).setText(intent.getStringExtra(Values.message));
        }

//        final DialogBase dialogBase = new DialogBase(this);
//
//        if (intent.hasExtra(Values.title)) {
//            dialogBase.setTitle(intent.getStringExtra(Values.title));
//        }
//        if (intent.hasExtra(Values.message)) {
//            dialogBase.setMessage(intent.getStringExtra(Values.message));
//        }
        if (intent.hasExtra(Values.type)) {
            type = intent.getIntExtra(Values.type, -1);
        }
        if (intent.hasExtra(Values.question_id)) {
            question_id = intent.getLongExtra(Values.question_id, -1);
        }
        ((LinearLayout) dialog.findViewById(R.id.root)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PopupNotificationActivity.this, ContentQuestionActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra(Values.question_id, question_id);
                startActivity(intent1);
                finish();
            }
        });
        dialog.show();
//        dialogBase.setOnClickOkListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(PopupNotificationActivity.this, ContentQuestionActivity.class);
//                intent1.putExtra(Values.question_id, question_id);
//                startActivity(intent1);
//                dialogBase.dismiss();
//
//            }
//        });
//        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogBase.dismiss();
//
//            }
//        });
//        dialogBase.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                finish();
//            }
//        });
//
//        dialogBase.show();
    }
}
