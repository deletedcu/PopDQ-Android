package com.popdq.app.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.popdq.app.R;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.DateUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.util.ViewAnswerCheckCredits;
import com.popdq.app.values.Values;
import com.popdq.app.view.ReportDialog;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentQuestionActivity extends BaseSearchActivity implements View.OnClickListener {
    public static final int RECORD_REQUEST_CODE = 45;
    //    public static final int RECORD_REQUEST_CODE_VISUAL = 46;
    private static final int RECORD_REQUEST_CODE_READ_EXTERNAL = 41;
    public static final int CAMERA_PERMISSION = 23;
    private static final String TAG = "ContentQuestionActivity";
    private static float FEE_VIEW_ANSWER = 1.0f;
    private long question_id;
    private SharedPreferences sharedPreferences;
    private Button btnDecline, btnReply;
    //    private ImageView imgMethod;
    private ImageView icMethod;
    private CircleImageView imgAvatarAsk, imgAvatarAnswer;
    private TextViewNormal textMethod;
    private TextViewNormal tvCreditYouEarn;
    private TextViewNormal tvLanguageWritten;
    private RelativeLayout questionLayout;
    private LinearLayout detail_info;
    private LinearLayout layoutViewAnswer;
    private LinearLayout layoutDeclineAndReply;
    private LinearLayout layoutAttachmentImage, listImage;
    private Question question;
    private RelativeLayout btnReport;
    private LinearLayout notMyQuestion2;
    private String token;
    private int method;
    long myId;

    private ProgressDialog progressDialog;
    private TextViewNormal tvBottom;
    private TextViewBold tvTotalRate;
    private RatingBar rate;
    private ImageView icMethod3;
    private LinearLayout btnViewProfile;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        Intent in = getIntent();
        Uri data = in.getData();


        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        Utils.setActionBar(this, getString(R.string.question), R.drawable.btn_back);
//        Utils.setBottomButton(this, getString(R.string.view_answer), this);
        Utils.checkStartActivityFromNotificationAndSendRead(this, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                Log.e(TAG, response);
            }

            @Override
            public void onError(String error) {

            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myId = PreferenceUtil.getUserId(this);
        if (in.hasExtra(Values.question_id))
            question_id = getIntent().getExtras().getLong(Values.question_id);

        else if (in.getAction() == Intent.ACTION_VIEW) {
            try {
                Uri uri = in.getData();
                String link = uri.toString();
                question_id = Long.parseLong(link.substring(link.indexOf("question_id=") + 12));
                int i=0;
                i++;
            } catch (Exception e) {

            }
        }
        questionLayout = (RelativeLayout) findViewById(R.id.question);
        btnDecline = (Button) findViewById(R.id.btnDecline);
        btnReply = (Button) findViewById(R.id.btnReply);
        detail_info = (LinearLayout) findViewById(R.id.detail_info);
        layoutViewAnswer = (LinearLayout) findViewById(R.id.layoutViewAnswer);
        layoutViewAnswer.setOnClickListener(this);
        layoutDeclineAndReply = (LinearLayout) findViewById(R.id.layoutDeclineAndReply);
        layoutAttachmentImage = (LinearLayout) findViewById(R.id.layoutAttachmentImage);
        listImage = (LinearLayout) findViewById(R.id.listImage);
        btnViewProfile = (LinearLayout) findViewById(R.id.btnViewProfile);
        btnReport = (RelativeLayout) findViewById(R.id.btnReport);
//        imgMethod = (ImageView) findViewById(R.id.imgMethod);
        icMethod = (ImageView) findViewById(R.id.icMethod);
        textMethod = (TextViewNormal) findViewById(R.id.textMethod);
        tvCreditYouEarn = (TextViewNormal) findViewById(R.id.tvCreditYouEarn);
        tvTotalRate = (TextViewBold) findViewById(R.id.tvTotalRate);
        rate = (RatingBar) findViewById(R.id.rate);
        rate.setIsIndicator(true);
        tvLanguageWritten = (TextViewNormal) findViewById(R.id.tvLanguage);
        imgAvatarAsk = (CircleImageView) findViewById(R.id.imgAvatarAsk);
        imgAvatarAnswer = (CircleImageView) findViewById(R.id.imgAvatarAnswer);
        icMethod3 = (ImageView) findViewById(R.id.icMethod3);
        notMyQuestion2 = (LinearLayout) findViewById(R.id.notMyQuestion2);
        imgAvatarAsk.setOnClickListener(this);
        btnViewProfile.setOnClickListener(this);

        tvBottom = (TextViewNormal) findViewById(R.id.tvBottom);


        btnReport.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
        btnReply.setOnClickListener(this);

        token = PreferenceUtil.getToken(this);

        questionLayout.post(new Runnable() {
            @Override
            public void run() {
                questionLayout.setMinimumHeight(((LinearLayout) findViewById(R.id.content_height)).getHeight());

            }
        });
        setActivityOfUser(false);
        getData();
        try {
            broadCastReply = new BroadCastReply();
            IntentFilter intentFilter = new IntentFilter("change_count_reply");
            registerReceiver(broadCastReply, intentFilter);
        } catch (Exception e) {

        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
//                Toast.makeText(ContentQuestionActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "share: onSuccess");
            }

            @Override
            public void onCancel() {
//                Toast.makeText(ContentQuestionActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "share: onCancel");


            }

            @Override
            public void onError(FacebookException error) {
//                Toast.makeText(ContentQuestionActivity.this, "onError", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "share: onError");


            }
        });


    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(broadCastReply);
        } catch (Exception e) {

        }
        super.onDestroy();

    }

    public void setListImageAttachment(Question.Attachments[] listImageAttachment) {
        if (question.getAttachments() == null || question.getAttachments().length <= 0) {
            layoutAttachmentImage.setVisibility(View.GONE);
        } else {
            layoutAttachmentImage.setVisibility(View.VISIBLE);
            final ArrayList<String> strings = new ArrayList<>();
            for (Question.Attachments attachments : listImageAttachment) {
                strings.add(attachments.link);
            }
            for (int i = 0; i < listImageAttachment.length; i++) {
                CircleImageView circleImageView = new CircleImageView(this);
                int size = (int) Utils.pxFromDp(this, 80);
                circleImageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                circleImageView.setPadding(20, 0, 20, 0);
                try {
                    Glide.with(this).load(Values.BASE_URL_AVATAR + listImageAttachment[i].link).placeholder(R.drawable.loading).dontAnimate().into(circleImageView);
                } catch (Exception e) {

                }
                listImage.addView(circleImageView);
                final int finalI = i;
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ContentQuestionActivity.this, ViewImageActivity.class);
                        intent.putStringArrayListExtra("listImages", strings);
                        intent.putExtra(Values.index, finalI);
                        startActivity(intent);
                    }
                });
            }
        }

    }

    public void isMyQuestion(boolean isMyQuestion) {
        // This question is my question
        icMethod3.setImageResource(Utils.getMethodIconWhite(question.getMethod()));
        if (isMyQuestion) {
//            ((LinearLayout) findViewById(R.id.layout_rate)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.date)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.isMyQuestion1)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.notMyQuestion1)).setVisibility(View.GONE);
            notMyQuestion2.setVisibility(View.GONE);
            icMethod3.setVisibility(View.VISIBLE);

            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.question));
        } else {
            ((LinearLayout) findViewById(R.id.notMyQuestion1)).setOnClickListener(this);
//            ((LinearLayout) findViewById(R.id.layout_rate)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.date)).setVisibility(View.VISIBLE);
            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.question) + " (" + question.getTotal_view() + " " + getString(R.string.views_upper) + ")");
            ((LinearLayout) findViewById(R.id.isMyQuestion1)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.notMyQuestion1)).setVisibility(View.VISIBLE);

            if (question.isViewed()) {
                notMyQuestion2.setVisibility(View.GONE);
                icMethod3.setVisibility(View.VISIBLE);

            } else {
                if (question.getStatus() == 3 || question.getStatus() == 5) {
                    notMyQuestion2.setVisibility(View.GONE);

                } else {
                    notMyQuestion2.setVisibility(View.VISIBLE);

                }
                icMethod3.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.icMethod2)).setImageResource(Utils.getMethodIconWhite(method));
            }

        }

    }

    private void setActivityOfUser(boolean userAnswer) {
        int status = -1;
        if (question != null)
            status = question.getStatus();
        if (userAnswer && (status == 1 || status == 4)) {
            detail_info.setVisibility(View.VISIBLE);
            layoutDeclineAndReply.setVisibility(View.VISIBLE);
            layoutViewAnswer.setVisibility(View.GONE);

        } else {
            detail_info.setVisibility(View.GONE);
            layoutDeclineAndReply.setVisibility(View.GONE);
            layoutViewAnswer.setVisibility(View.VISIBLE);

        }

        questionLayout.post(new Runnable() {
            @Override
            public void run() {
                int height = ((LinearLayout) findViewById(R.id.content_height)).getHeight();
                questionLayout.setMinimumHeight(height);

            }
        });


    }


    public void getData() {
        String token = sharedPreferences.getString(Values.TOKEN, "");
        QuestionUtil.detail(this, token, question_id + "", new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                try {
                    JSONObject jsonObject = new JSONObject(reponse);
                    question = new Gson().fromJson(jsonObject.getString("question"), Question.class);
                    if (question == null) return;
                    ((TextViewBold) findViewById(R.id.tvTitle)).setText(question.getTitle());
                    ((TextViewNormal) findViewById(R.id.tvContent)).setText(question.getDescription());

                    String professional = question.getUser_answer().getCategoriesString();
                    if (professional == null || professional.length() <= 0) {
                        professional = "No information";
                    }
                    ((TextViewThin) findViewById(R.id.tvProfesstion)).setSelected(true);
                    ((TextViewThin) findViewById(R.id.tvProfesstion)).setText(professional);
                    String nameAsk = question.getUser().getDisplayName();
                    ((TextViewThin) findViewById(R.id.tvAskName)).setSelected(true);
                    if (question.getUser().getStatus_anonymous() == 0) {
                        ((TextViewThin) findViewById(R.id.tvAskName)).setText(Utils.getAnonymousName(nameAsk));
                    } else {
                        ((TextViewThin) findViewById(R.id.tvAskName)).setText(nameAsk);
                    }
                    ((TextViewThin) findViewById(R.id.tvAskName)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.startActivityExpertProfile(ContentQuestionActivity.this, question.getUser().getId());
                        }
                    });

                    ((TextViewBold) findViewById(R.id.tvAnswerName)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.startActivityExpertProfile(ContentQuestionActivity.this, question.getUser_answer().getId());
                        }
                    });
                    String nameAnswer = question.getUser_answer().getDisplayName();
                    if (question.getUser_answer().getStatus_anonymous() == 0) {
                        ((TextViewBold) findViewById(R.id.tvAnswerName)).setText(Utils.getAnonymousName(nameAnswer));
                    } else {
                        ((TextViewBold) findViewById(R.id.tvAnswerName)).setText(nameAnswer);
                    }
                    if (question.getCount_rating() > 0) {
                        tvTotalRate.setText("(" + question.getCount_rating() + ")");
                    }

                    ((TextViewThin) findViewById(R.id.tvView)).setText(question.getTotal_view() + " " + getString(R.string.views));
//                    ((TextViewNormal) findViewById(R.id.tvReport)).setText(question.getTotal_report() + "");
                    ((TextViewNormal) findViewById(R.id.tvCountImages)).setText("(" + question.getAttachments().length + ")");
//                    String date = DateUtil.getBriefRelativeTimeSpanString(ContentQuestionActivity.this, Locale.getDefault(), question.getCreated_timestamp());
                    String date = DateUtil.getTimeAgo(question.getCreated_timestamp(), ContentQuestionActivity.this );
//                    String date = DateUtil.getBriefRelativeTimeSpanString(ContentQuestionActivity.this, Locale.getDefault(), question.getCreated_timestamp());

                    ((TextViewThin) findViewById(R.id.tvDate)).setText(date);
                    try {
                        rate.setRating(question.getTotal_rating() / (question.getCount_rating() * 2));
                    } catch (Exception e) {

                    }
//                    if(question.getTotal_rating()/question.getCount_rating())

//                   String[] strings = new String[]{"dang", "android", "windowphone", "ios", "ict", "azstack", "azgram", "viettel", "azstack", "fpt", "vnpt"};
//
//                    setTagView(strings);
//                    setTagView(question.getTag());

//                    imgAvatarA.setOnClickListener(ContentQuestionActivity.this);
                    if (question.getCredit_hold() <= 0) {
                        tvCreditYouEarn.setText(String.format(getString(R.string.you_earn_from_answering), "$0"));

                    } else {
                        tvCreditYouEarn.setText(String.format(getString(R.string.you_earn_from_answering), "$" + question.getCredit_hold() / 2));

                    }
                    method = question.getMethod();
                    if (method == 1) {
//                        imgMethod.setImageResource(R.drawable.phone_number_icon);
                        textMethod.setText(getString(R.string.reply_method_text));
                        if (question.getFree_preview() == 0) {
                            FEE_VIEW_ANSWER = 0.1f;
                        }
                    } else if (method == 2) {
//                        imgMethod.setImageResource(R.drawable.phone_number_icon);
                        textMethod.setText(getString(R.string.reply_method_voice));
                        if (question.getFree_preview() == 0) {
                            FEE_VIEW_ANSWER = 0.2f;
                        }
                    } else if (method == 3) {
//                        imgMethod.setImageResource(R.drawable.phone_number_icon);
                        textMethod.setText(getString(R.string.reply_method_video));
                        if (question.getFree_preview() == 0) {
                            FEE_VIEW_ANSWER = 0.3f;
                        }
                    }


                    if (question.getFree_preview() == 1) {
                        FEE_VIEW_ANSWER = 0f;
                    }

                    ((TextViewBold) findViewById(R.id.tvFee)).setText("$" + String.format("%.2f", FEE_VIEW_ANSWER));
                    ((TextViewNormal) findViewById(R.id.tvFee2)).setText("$" + String.format("%.2f", FEE_VIEW_ANSWER) + ")");
                    int status = question.getStatus();
                    setStatus(status);
                    try {
                        Glide.with(ContentQuestionActivity.this).load(Values.BASE_URL_AVATAR + question.getUser().getAvatar()).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatarAsk);
                        Glide.with(ContentQuestionActivity.this).load(Values.BASE_URL_AVATAR + question.getUser_answer().getAvatar()).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatarAnswer);
                    } catch (Exception e) {

                    }

                    if (question.getUser_answer().getVerified() == 1) {
                        ((ImageView) findViewById(R.id.imgVerified)).setVisibility(View.VISIBLE);
                    } else {
                        ((ImageView) findViewById(R.id.imgVerified)).setVisibility(View.GONE);

                    }
                    tvLanguageWritten.setText(question.getLanguage_written());
                    setListImageAttachment(question.getAttachments());
                    if (question.getStatus() == 3 || question.getStatus() == 5) {
                        setActivityOfUser(false);
                    } else
                        setActivityOfUser(question.getUser_id_answer() == myId);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }


                    long myId = PreferenceUtil.getUserId(ContentQuestionActivity.this);
                    if (question.getUser_answer().getId() == myId) {
                        isMyQuestion(true);
                    } else {
                        isMyQuestion(false);
                    }


                    if (question.getTotal_rating() <= 0) {
                        ((TextViewNormal) findViewById(R.id.tvNorating)).setVisibility(View.VISIBLE);
                        rate.setVisibility(View.GONE);


                    } else {
                        ((TextViewNormal) findViewById(R.id.tvNorating)).setVisibility(View.GONE);
                        rate.setVisibility(View.VISIBLE);

                    }


                } catch (Exception e) {
                    Toast.makeText(ContentQuestionActivity.this, "Have error...", Toast.LENGTH_SHORT).show();
                    finish();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    public void setStatus(int status) {
        if (status == 1 || status == 4) {
            tvBottom.setText(getString(R.string.pending_reply).toUpperCase());
            layoutViewAnswer.setBackgroundResource(R.color.text_solved_static);

        } else if (status == 2) {
            tvBottom.setText(getString(R.string.view_answer).toUpperCase());
            layoutViewAnswer.setBackgroundResource(R.drawable.button_pop_a_question);

        } else if (status == 3) {
            tvBottom.setText(getString(R.string.expired).toUpperCase());
            layoutViewAnswer.setBackgroundColor(Utils.getColor(this, R.color.text_solved_static));
        } else if (status == 5) {
            tvBottom.setText(getString(R.string.reject).toUpperCase());
            layoutViewAnswer.setBackgroundColor(Utils.getColor(this, R.color.text_solved_static));
        }

//        String method = null;
//        if (question.getMethod() == 1) {
//            method = "TEXT";
//        } else if (question.getMethod() == 2) {
//            method = "VOICE";
//
//        } else if (question.getMethod() == 3) {
//            method = "VIDEO";
//
//        }

//        imgMethod = (ImageView) findViewById(R.id.imgMethod);
//        TextViewNormal button = ((TextViewNormal) findViewById(R.id.status));
//        if (FEE_VIEW_ANSWER > 0 && status == 2) {
//            ((ImageView) findViewById(R.id.imgPaid)).setVisibility(View.VISIBLE);
////            button.setVisibility(View.GONE);
////            imgMethod.setVisibility(View.VISIBLE);
//        } else {
//            ((ImageView) findViewById(R.id.imgPaid)).setVisibility(View.GONE);
////            button.setVisibility(View.VISIBLE);
////            imgMethod.setVisibility(View.GONE);
//
//        }

//        if (status == 2) {
//            button.setVisibility(View.GONE);

        if (method == 1) {
            icMethod.setImageResource(R.drawable.text);
        } else if (method == 2) {
            icMethod.setImageResource(R.drawable.voice);
        } else {
            icMethod.setImageResource(R.drawable.video);
        }

//            icMethod.setVisibility(View.VISIBLE);
//            button.setText(method);
//            button.setBackgroundResource(R.drawable.bg_solved);
//        } else
        if (status == 3)
            setActivityOfUser(false);
//            icMethod.setVisibility(View.GONE);
//            button.setVisibility(View.VISIBLE);
//            button.setText(getString(R.string.reject));
//            button.setBackgroundResource(R.drawable.bg_solved);
//            button.setTextColor(Utils.getColor(this, R.color.text_solved_static));

//        } else {
//            icMethod.setVisibility(View.GONE);
//            button.setVisibility(View.VISIBLE);
//            button.setText(getString(R.string.help));
//            button.setBackgroundResource(R.drawable.bg_help);
//            button.setTextColor(Utils.getColor(this, R.color.white));
//        }

        if (status == 2) {
            ((LinearLayout) findViewById(R.id.layoutCountView)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.layoutCountView)).setOnClickListener(this);

        } else ((LinearLayout) findViewById(R.id.layoutCountView)).setVisibility(View.GONE);


    }

    public void reject() {
        final ReportDialog reportDialog = new ReportDialog(this);
        reportDialog.setTitle(getString(R.string.reject_title_question));
        reportDialog.setMessage(getString(R.string.reject_description));
        reportDialog.getBtnOk().setText(getString(R.string.reject_non_ed));

        reportDialog.setBtnOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = reportDialog.getMessageReport();
                if (message == null || message.equals("")) {
                    Toast.makeText(ContentQuestionActivity.this, getString(R.string.report_message_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                QuestionUtil.reject(ContentQuestionActivity.this, token, question_id, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
                        if (result.getCode() == 0) {
                            Toast.makeText(ContentQuestionActivity.this, getString(R.string.reject_success), Toast.LENGTH_SHORT).show();
                            setStatus(5);
                            setActivityOfUser(false);
                            User newUserInfo = VolleyUtils.getUserInfo(reponse);

                            User.changeCreditAndPutPrefernce(ContentQuestionActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
                            reportDialog.dismiss();
                        }
                        Toast.makeText(ContentQuestionActivity.this, "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.do_you_want_reject));
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                QuestionUtil.reject(ContentQuestionActivity.this, token, question_id, new VolleyUtils.OnRequestListenner() {
//                    @Override
//                    public void onSussces(String reponse, Result result) {
//                        if (result.getCode() == 0) {
//                            Toast.makeText(ContentQuestionActivity.this, getString(R.string.reject_success), Toast.LENGTH_SHORT).show();
//                            setStatus(5);
//                            User newUserInfo = VolleyUtils.getUserInfo(reponse);
//
//                            User.changeCreditAndPutPrefernce(ContentQuestionActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
//
//                        }
//                        Toast.makeText(ContentQuestionActivity.this, "" + result.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
//            }
//        });
//        builder.setNegativeButton("CANCEL", null);
//        builder.show();

    }

//    RadioButton radioButton;
//    String messageReport;

    public void report() {
        final ReportDialog reportDialog = new ReportDialog(this);
        reportDialog.setBtnOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = reportDialog.getMessageReport();
                if (message == null || message.equals("")) {
                    Toast.makeText(ContentQuestionActivity.this, getString(R.string.report_message_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                QuestionUtil.report(ContentQuestionActivity.this, token, question_id, reportDialog.getMessageReport(), new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String reponse, Result result) {
                        Log.e(TAG, " message:" + reportDialog.getMessageReport());
                        Toast.makeText(ContentQuestionActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        reportDialog.dismiss();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
    }


    public void reply(int method) {
        Intent intent = null;
        switch (method) {
            case 1:
                intent = new Intent(this, ProvideAnswerByTextActivity.class);
                break;
            case 2:
                intent = new Intent(this, ProvideAnswerByVoiceActivity.class);
                break;
            case 3:
                intent = new Intent(this, ProvideAnswerByVideoActivity.class);

                break;
        }
        intent.putExtra(Values.question_id, question.getId());
        intent.putExtra(Values.method, question.getMethod());
        intent.putExtra(Values.title, question.getTitle());
        intent.putExtra(Values.language_written, question.getLanguage_written());
        startActivity(intent);
    }

    public void viewAnswer(int method) {
        Intent intentViewAnswer = null;
        switch (method) {
            case 1:
                intentViewAnswer = new Intent(this, ViewAnswerTextActivity.class);
                break;
            case 2:
//                if (method == 2) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (ContextCompat.checkSelfPermission(this,
//                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(this,
//                                    new String[]{Manifest.permission.RECORD_AUDIO},
//                                    RECORD_REQUEST_CODE_VISUAL);
//                            return;
//                        }
//                    }
//
//
//                }
                intentViewAnswer = new Intent(this, ViewAnswerVoiceRecordActivity.class);
                break;
            case 3:
                intentViewAnswer = new Intent(this, ViewAnswerVideoActivity.class);
                break;
        }
        question.setViewed(true);
        intentViewAnswer.putExtra(Values.question_id, question.getId());
        intentViewAnswer.putExtra(Values.title, question.getTitle());
        startActivity(intentViewAnswer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == RECORD_REQUEST_CODE || requestCode == Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && Utils.checkPermission(this)) {
////                ActivityCompat.requestPermissions(this,
////                        new String[]{Manifest.permission.RECORD_AUDIO},
////                        RECORD_REQUEST_CODE);
//                    reply(method);
////                return;
//
//                } else {
//                    return;
//                }
//            } else {
//                reply(method);
//
//            }
//
//        }
//        if (requestCode == RECORD_REQUEST_CODE_VISUAL) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                    viewAnswer(method);
//                    return;
//                } else {
//                    viewAnswer(method);
//                }
//
//            }
//        }
//        if (requestCode == CAMERA_PERMISSION)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                        == PackageManager.PERMISSION_GRANTED && Utils.checkPermission(this)) {
//                    reply(method);
//                } else {
//                    return;
//                }
//            } else {
//                reply(method);
//            }
//
//
//        if (requestCode == Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
//            if (Utils.checkPermission(this)) {
//                reply(method);
//            }
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notMyQuestion1:
//                Toast.makeText(this, getString(R.string.comming_soon), Toast.LENGTH_SHORT).show();

                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    String uriImage = "";
                    if (question != null && question.getAttachments().length > 0) {
                        uriImage = Values.BASE_URL_AVATAR + question.getAttachments()[0].link;
                    }
                    Uri uri = Uri.parse("http://www.popdq.com/question_id="+question_id);
                    Uri uriImagee = Uri.parse(uriImage);
                    shareDialog.show(Utils.getShareLinkContent(this, uri, uriImagee, question.getTitle(), String.format(getString(R.string.des_share_fb), question.getUser_answer().getUsername())));
                } else {
                    Toast.makeText(ContentQuestionActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnDecline:
                if (question.getUser().getId() == myId || question.getUser_id_answer() == myId) {
                    reject();
                } else {
                    report();
                }
                break;

            case R.id.btnReply:
                reply(method);
                break;
            case R.id.layoutViewAnswer:
                ViewAnswerCheckCredits.checkViewAnswer(this, question, null);
                break;

            case R.id.btnViewProfile:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, question.getUser_answer().getId());
                startActivity(intent);
                break;

            case R.id.imgAvatarAsk:
                Intent intent2 = new Intent(this, UserProfileActivity.class);
                intent2.putExtra(Values.experts_id, question.getUser().getId());
                startActivity(intent2);
                break;


            case R.id.btnReport:
                if (question.getUser().getId() == myId || question.getUser_id_answer() == myId) {
                    reject();
                } else {
                    report();
                }

                break;
            case R.id.layoutCountView:
                Intent intent1 = new Intent(this, ListViewerActivity.class);
                intent1.putExtra(Values.question_id, question_id);
                startActivity(intent1);
                break;
//            case R.id.imgAvatar:
//                Intent intent = new Intent(this, ExpertProfileActivity.class);
//                intent.putExtra(Values.experts_id, question.getUser_id());
//                startActivity(intent);
//                break;

        }
    }

//    public void checkViewAnswer() {
//        if (question == null) return;
//        if (question.getTotal_answer() <= 0) {
//            Toast.makeText(ContentQuestionActivity.this, "No Answer!!!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (MyApplication.user == null) {
//            try {
//                MyApplication.user = Utils.getUser(this);
//            } catch (Exception e) {
//                startActivity(new Intent(this, LoginActivity.class));
//                e.printStackTrace();
//                finish();
//                return;
//            }
//        }
//        if (question.getUser().getId() == myId || question.getUser_id_answer() == myId || question.isViewed()) {
//            viewAnswer(method);
//        } else if (MyApplication.user.getCredit() >= FEE_VIEW_ANSWER) {
//            if (FEE_VIEW_ANSWER > 0 && !question.isViewed()) {
//                String title = getString(R.string.title_dialog_notice_view_answer);
//                String message = String.format(getString(R.string.message_dialog_view_answer), "$" + FEE_VIEW_ANSWER + "");
//                DialogNoticeCredit.showDialogAskOrTranfer(ContentQuestionActivity.this, title, message, new DialogNoticeCredit.OnClickDialogListener() {
//                    @Override
//                    public void onClickOk() {
//                        viewAnswer(method);
//                    }
//                });
//
//
////                        final Dialog dialog = new Dialog(this);
////                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
////                        dialog.setContentView(R.layout.layout_dialog_notice_view_answer);
////                        DecimalFormat df = new DecimalFormat("#.##");
////                        String fee = df.format(FEE_VIEW_ANSWER);
////                        ((TextViewNormal) dialog.findViewById(R.id.tvMessage)).setText(String.format(getString(R.string.you_will_fee_is), fee));
////                        ((Button) dialog.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                viewAnswer(method);
////                                dialog.dismiss();
////                            }
////                        });
////                        ((Button) dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                dialog.dismiss();
////                            }
////                        });
////
////                        ((Button) dialog.findViewById(R.id.btnCancel)).setTypeface(MyApplication.getInstanceTypeNormal(this));
////                        ((Button) dialog.findViewById(R.id.btnOk)).setTypeface(MyApplication.getInstanceTypeNormal(this));
////
////
////                        dialog.show();
//            } else {
//                viewAnswer(method);
//            }
//
////                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
////                    builder.setMessage(String.format(getString(R.string.you_will_fee_is), FEE_VIEW_ANSWER));
////                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialogInterface, int i) {
////                            viewAnswer(method);
////
////                        }
////                    });
////                    builder.setNegativeButton("Cancel", null);
////                    builder.show();
//        } else if (FEE_VIEW_ANSWER > MyApplication.user.getCredit() && FEE_VIEW_ANSWER < MyApplication.user.getCredit_earnings()) {
//            String title = getString(R.string.title_dialog_notice_view_answer);
//            String message = (getString(R.string.message_dialog_tranfer));
//            DialogNoticeCredit.showDialogAskOrTranfer(ContentQuestionActivity.this, title, message, new DialogNoticeCredit.OnClickDialogListener() {
//                @Override
//                public void onClickOk() {
//                    TranferCreditPopup.showDialogConvert(ContentQuestionActivity.this, new TranferCreditPopup.TranferListener() {
//                        @Override
//                        public void onSuccess(float credit) {
//                            checkViewAnswer();
//                        }
//                    });
//
//                }
//            });
//        } else {
//            Toast.makeText(ContentQuestionActivity.this, "Not enough credits...", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, BuyCreditActivityBackup.class));
//            return;
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    BroadCastReply broadCastReply;

    class BroadCastReply extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            question.setTotal_answer(question.getTotal_answer() + 1);
            setStatus(2);
            setActivityOfUser(false);
//            ((TextView) findViewById(R.id.tvReply)).setText(question.getTotal_answer() + "");

        }
    }
}
