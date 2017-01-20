package com.popdq.app.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.model.Question;
import com.popdq.app.model.User;
import com.popdq.app.ui.BuyCreditActivityBackup;
import com.popdq.app.ui.ViewAnswerTextActivity;
import com.popdq.app.ui.ViewAnswerVideoActivity;
import com.popdq.app.ui.ViewAnswerVoiceRecordActivity;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogNoticeCredit;
import com.popdq.app.view.TranferCreditPopup;

/**
 * Created by Dang Luu on 8/30/2016.
 */
public class ViewAnswerCheckCredits {
    public static void checkViewAnswer(final Activity context, final Question question, final View.OnClickListener onClickListener) {
        float FEE_VIEW_ANSWER = 0;
        int method = question.getMethod();
        int free = question.getFree_preview();
        if (free == 1) {
            FEE_VIEW_ANSWER = 0f;
        } else {
            if (method == 1) {
                FEE_VIEW_ANSWER = 0.1f;
            } else if (method == 2) {
                FEE_VIEW_ANSWER = 0.2f;
            } else if (method == 3) {
                FEE_VIEW_ANSWER = 0.3f;
            }
        }

        if (question.getTotal_answer() <= 0) {
            Toast.makeText(context, "No Answer!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = User.getInstance(context);
        if (question.getUser() == null) {

        }
        if (question.getUser_id() == user.getId() || question.getUser_id_answer() == user.getId() || question.isViewed()) {
            viewAnswer(context, question, FEE_VIEW_ANSWER, true);
            if (onClickListener != null) {
                onClickListener.onClick(null);
            }
        } else if (MyApplication.user.getCredit() >= FEE_VIEW_ANSWER) {
            if (FEE_VIEW_ANSWER > 0 && !question.isViewed()) {
                String title = context.getString(R.string.title_dialog_notice_view_answer);
                String message = String.format(context.getString(R.string.message_dialog_view_answer), "$" + String.format("%.2f", FEE_VIEW_ANSWER) + "");
                final float finalFEE_VIEW_ANSWER = FEE_VIEW_ANSWER;
                DialogNoticeCredit.showDialogAskOrTranfer(context, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                    @Override
                    public void onClickOk() {
                        viewAnswer(context, question, finalFEE_VIEW_ANSWER, question.isViewed());
                        if (onClickListener != null) {
                            onClickListener.onClick(null);
                        }
                    }
                });

//                        final Dialog dialog = new Dialog(this);
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                        dialog.setContentView(R.layout.layout_dialog_notice_view_answer);
//                        DecimalFormat df = new DecimalFormat("#.##");
//                        String fee = df.format(FEE_VIEW_ANSWER);
//                        ((TextViewNormal) dialog.findViewById(R.id.tvMessage)).setText(String.format(getString(R.string.you_will_fee_is), fee));
//                        ((Button) dialog.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                viewAnswer(method);
//                                dialog.dismiss();
//                            }
//                        });
//                        ((Button) dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                        ((Button) dialog.findViewById(R.id.btnCancel)).setTypeface(MyApplication.getInstanceTypeNormal(this));
//                        ((Button) dialog.findViewById(R.id.btnOk)).setTypeface(MyApplication.getInstanceTypeNormal(this));
//
//
//                        dialog.show();
            } else {
                viewAnswer(context, question, FEE_VIEW_ANSWER, question.isViewed());
                if (onClickListener != null) {
                    onClickListener.onClick(null);
                }
            }

//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setMessage(String.format(getString(R.string.you_will_fee_is), FEE_VIEW_ANSWER));
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            viewAnswer(method);
//
//                        }
//                    });
//                    builder.setNegativeButton("Cancel", null);
//                    builder.show();
        } else if (FEE_VIEW_ANSWER > MyApplication.user.getCredit() && FEE_VIEW_ANSWER < MyApplication.user.getCredit_earnings()) {
            String title = context.getString(R.string.title_dialog_notice_view_answer);
            String message = (context.getString(R.string.message_dialog_tranfer));
            DialogNoticeCredit.showDialogAskOrTranfer(context, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                @Override
                public void onClickOk() {
                    TranferCreditPopup.showDialogConvert(context, new TranferCreditPopup.TranferListener() {
                        @Override
                        public void onSuccess(float credit) {
                            checkViewAnswer(context, question, onClickListener);
                        }
                    });

                }
            });
        } else {
//            Toast.makeText(context, "Not enough credits...", Toast.LENGTH_SHORT).show();
            String title = context.getString(R.string.no_credit);
            String message = String.format(context.getString(R.string.message_dialog_buy_credit), "$" + String.format("%.2f", FEE_VIEW_ANSWER) + "");
            DialogNoticeCredit.showDialogAskOrTranfer(context, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                @Override
                public void onClickOk() {
                    context.startActivity(new Intent(context, BuyCreditActivityBackup.class));

                }
            });

            return;
        }

    }

    public static void viewAnswer(Activity context, Question question, float feeViewAnswer, boolean isViewed) {
        Intent intentViewAnswer = null;
        Intent intent = new Intent("update_views");
        intent.putExtra("total_views", question.getTotal_view());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        switch (question.getMethod()) {

            case 1:
                intentViewAnswer = new Intent(context, ViewAnswerTextActivity.class);
                break;
            case 2:
//                if (question.getMethod() == 2) {
//                    if (ContextCompat.checkSelfPermission(context,
//                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(context,
//                                new String[]{Manifest.permission.RECORD_AUDIO},
//                                ContentQuestionActivity.RECORD_REQUEST_CODE_VISUAL);
//                        return;
//                    }
//
//                }
                intentViewAnswer = new Intent(context, ViewAnswerVoiceRecordActivity.class);
                break;
            case 3:
                intentViewAnswer = new Intent(context, ViewAnswerVideoActivity.class);
                break;
        }
        intentViewAnswer.putExtra(Values.question_id, question.getId());
        intentViewAnswer.putExtra(Values.title, question.getTitle());
        intentViewAnswer.putExtra(Values.avatar, question.getUser().getAvatar());
        intentViewAnswer.putExtra(Values.name, question.getUser().getDisplayName());
        intentViewAnswer.putExtra(Values.language_written, question.getLanguage_written());
        intentViewAnswer.putExtra(Values.isViewed, isViewed);
        question.setViewed(true);

        intentViewAnswer.putExtra(Values.method, question.getMethod());
        intentViewAnswer.putExtra(Values.FEE_VIEW_ANSWER, feeViewAnswer);
        context.startActivity(intentViewAnswer);
    }
}
