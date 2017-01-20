package com.popdq.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.popdq.app.R;
import com.popdq.app.connection.AnswerUtil;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Answer;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.DateUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.ReportDialog;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAnswerTextActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ViewAnswerTextActivity";
    private LinearLayout layoutAttachmentImage, listImage;
    private long question_id;
    private Answer answer;
    private CircleImageView imgAvatarAsk, imgAvatarAnswer;
    private RelativeLayout questionLayout,editanswerlayout;
    private RelativeLayout btnReport;
    protected RatingBar rate;
    private String avatarUseAsk;
    private String titleQuestion;
    private String askUserName;
    protected String language_written;
    private int method;
    private float FEE_VIEW_ANSWER;
    private ImageView icMethod;
    private ImageView imgedit;
    private TextViewBold tvTotalRate;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private TextViewThin reply_edit;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer_text);
        layoutAttachmentImage = (LinearLayout) findViewById(R.id.layoutAttachmentImage);
        listImage = (LinearLayout) findViewById(R.id.listImage);
        Utils.checkStartActivityFromNotificationAndSendRead(this, null);
        ((TextViewThin) findViewById(R.id.btnRateText)).setText(getString(R.string.rate_this_answer));
        Utils.setActionBar(this, getString(R.string.text_answer), R.drawable.btn_back);
        layoutAttachmentImage = (LinearLayout) findViewById(R.id.layoutAttachmentImage);
        listImage = (LinearLayout) findViewById(R.id.listImage);
        icMethod = (ImageView) findViewById(R.id.icMethod);
        tvTotalRate = (TextViewBold) findViewById(R.id.tvTotalRate);
        btnReport = (RelativeLayout) findViewById(R.id.btnReport);
        rate = (RatingBar) findViewById(R.id.rate);
        editanswerlayout = (RelativeLayout) findViewById(R.id.id_edit_answer) ;
        rate.setIsIndicator(false);

        reply_edit=(TextViewThin)findViewById(R.id.reply_edit);
        imgedit = (ImageView)findViewById(R.id.imgedit);
        user = User.getInstance(this);

        imgAvatarAsk = (CircleImageView) findViewById(R.id.imgAvatarAsk);
        imgAvatarAnswer = (CircleImageView) findViewById(R.id.imgAvatarAnswer);
        imgAvatarAnswer.setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnViewProfile)).setOnClickListener(this);;
        ((RelativeLayout) findViewById(R.id.id_edit_answer)).setOnClickListener(this);


        btnReport.setOnClickListener(this);
        questionLayout = (RelativeLayout) findViewById(R.id.question);
        questionLayout.post(new Runnable() {
            @Override
            public void run() {
                questionLayout.setMinimumHeight(((LinearLayout) findViewById(R.id.content_height)).getHeight());

            }
        });
        Bundle bundle = getIntent().getExtras();
        question_id = bundle.getLong(Values.question_id, -1);
        avatarUseAsk = bundle.getString(Values.avatar, "");
        titleQuestion = bundle.getString(Values.title, "");
        askUserName = bundle.getString(Values.name, "");
        method = bundle.getInt(Values.method, -1);
        method = bundle.getInt(Values.method, -1);
        FEE_VIEW_ANSWER = bundle.getFloat(Values.FEE_VIEW_ANSWER);
//        getData();


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
        ((LinearLayout) findViewById(R.id.notMyQuestion1)).setOnClickListener(this);

        //share when first view
//        ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!getIntent().getBooleanExtra(Values.isViewed, false)) {
//                    Utils.showDialogShare(ViewAnswerTextActivity.this, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (ShareDialog.canShow(ShareLinkContent.class)) {
//                                String uriImage = "";
//                                if (answer != null && answer.getAttachments().length > 0) {
//                                    uriImage = Values.BASE_URL_AVATAR + answer.getAttachments()[0].link;
//                                }
//                                Uri uri = Uri.parse("http://www.popdq.com/");
//                                Uri uriImagee = Uri.parse(uriImage);
//                                shareDialog.show(Utils.getShareLinkContent(ViewAnswerTextActivity.this, uri, uriImagee, titleQuestion, String.format(getString(R.string.des_share_fb), answer.getUser().getUsername())));
//                            } else {
//                                Toast.makeText(ViewAnswerTextActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } else finish();
//            }
//        });

//        if (!getIntentNotificationBar().getBooleanExtra(Values.isViewed, false))
//            ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.showDialogShare(ViewAnswerTextActivity.this);
//                }
//            });


    }

    @Override
    public void onResume(){
        super.onResume();
        getData();
    }

    public void getData() {
        AnswerUtil.search(this, PreferenceUtil.getToken(this), question_id, 0, 100, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                final List<Answer> answers = AnswerUtil.getAnswerFromJsonRespone(reponse);
                if (answers != null && answers.size() > 0) {
                    answer = answers.get(answers.size() - 1);
                    String name = answer.getUser().getDisplayName();
                    try {
                        Glide.with(ViewAnswerTextActivity.this).load(Values.BASE_URL_AVATAR + answer.getUser().getAvatar()).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatarAnswer);
                        Glide.with(ViewAnswerTextActivity.this).load(Values.BASE_URL_AVATAR + avatarUseAsk).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatarAsk);
                    } catch (Exception e) {

                    }


                    if(answer.getUser().getDisplayName().equals(user.getDisplayName())){
                        reply_edit.setText("EDIT");
                        imgedit.setVisibility(View.VISIBLE);
                        editanswerlayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(ViewAnswerTextActivity.this, ProvideUpdateAnswerByTextActivity.class);
                                i.putExtra(Values.question_id, question_id);
                                i.putExtra(Values.method, method);
                                i.putExtra(Values.title, titleQuestion);
                                i.putExtra(Values.language_written, language_written);
                                i.putExtra(Values.answer_id,answer.getId());
                                i.putExtra(Values.answer_content,answer.getContent());

                                Log.e("extras","extra_qid : "+question_id+"answer_id : "+answer.getId());

                                startActivity(i);

                            }
                        });
                    } else {
                        imgedit.setVisibility(View.GONE);
                    }

                    if (answer.getUser().getStatus_anonymous() == 0) {
                        ((TextViewBold) findViewById(R.id.tvAnswerName)).setText(Utils.getAnonymousName(name));
                    } else {
                        ((TextViewBold) findViewById(R.id.tvAnswerName)).setText(name);
                    }
                    ((TextViewBold) findViewById(R.id.tvAnswerName)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.startActivityExpertProfile(ViewAnswerTextActivity.this, answer.getUser().getId());
                        }
                    });
                    ((TextViewBold) findViewById(R.id.tvTitle)).setText(titleQuestion);
                    ((TextViewNormal) findViewById(R.id.textAnswer)).setVisibility(View.VISIBLE);
                    ((TextViewNormal) findViewById(R.id.tvCountImages)).setText("(" + answer.getAttachments().length + ")");
                    ((TextViewNormal) findViewById(R.id.tvContent)).setText(answer.getContent());
                    ((TextViewThin) findViewById(R.id.tvView)).setText(answer.getTotalView() + " " + getString(R.string.views));
                    setListImageAttachment(answer.getAttachments());
                    ((TextViewThin) findViewById(R.id.tvAskName)).setSelected(true);
                    ((TextViewThin) findViewById(R.id.tvAskName)).setText(askUserName);
//                    String date = DateUtil.getBriefRelativeTimeSpanString(ViewAnswerTextActivity.this, Locale.getDefault(), answer.getCreated_timestamp());
                  String date = DateUtil.getTimeAgo(answer.getCreated_timestamp(),ViewAnswerTextActivity.this);

                    ((TextViewThin) findViewById(R.id.tvDate)).setText(date);
                    String professional = answer.getUser().getCategoriesString();
                    if (professional == null || professional.length() <= 0) {
                        professional = "No information";
                    }
                    ((TextViewThin) findViewById(R.id.tvProfesstion)).setSelected(true);
                    ((TextViewThin) findViewById(R.id.tvProfesstion)).setText(professional);
                    imgAvatarAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewAnswerTextActivity.this, UserProfileActivity.class);
                            intent.putExtra(Values.experts_id, answer.getUser_id());
                            startActivity(intent);
                        }
                    });
                    if (answer.getUser_id() == PreferenceUtil.getUserId(ViewAnswerTextActivity.this)) {
                        ((LinearLayout) findViewById(R.id.layout_rate)).setVisibility(View.GONE);
                    } else {
                        ((LinearLayout) findViewById(R.id.layout_rate)).setVisibility(View.VISIBLE);

                    }
                    if (answer.getUser().getVerified() == 1) {
                        ((ImageView) findViewById(R.id.imgVerified)).setVisibility(View.VISIBLE);
                    } else {
                        ((ImageView) findViewById(R.id.imgVerified)).setVisibility(View.GONE);

                    }
                    User newUserInfo = VolleyUtils.getUserInfo(reponse);
                    Log.e("new user info", String.valueOf(newUserInfo.getCredit_earnings()));
                    Log.e("new user info", String.valueOf(newUserInfo.getCredit()));
                    User.changeCreditAndPutPrefernce(ViewAnswerTextActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
                    if (answer.getMyRating() != null) {
                        rate.setRating(answer.getMyRating().getRating() / 2);
                    }
                    rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            rate("", v);
                        }
                    });
                    setMethodImage(method);
                    ((TextViewBold) findViewById(R.id.tvFee)).setText("$" + String.format("%.2f", FEE_VIEW_ANSWER));
                } else {
                    Toast.makeText(ViewAnswerTextActivity.this, "No answer!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void setMethodImage(int method) {
        if (method == 1) {
            icMethod.setImageResource(R.drawable.text);
        } else if (method == 2) {
            icMethod.setImageResource(R.drawable.voice);
        } else {
            icMethod.setImageResource(R.drawable.video);
        }
    }


    public void setListImageAttachment(Question.Attachments[] listImageAttachment) {

        final ArrayList<String> strings = new ArrayList<>();
        strings.clear();

        listImage.removeAllViews();
        listImage.invalidate();

        Log.e("ATT LOG", String.valueOf(listImageAttachment.length));

        if (answer.getAttachments() == null || answer.getAttachments().length <= 0) {
            layoutAttachmentImage.setVisibility(View.GONE);
        } else {
            layoutAttachmentImage.setVisibility(View.VISIBLE);
            for (Question.Attachments attachments : listImageAttachment) {
                strings.add(attachments.link);
            }
            for (int i = 0; i < listImageAttachment.length; i++) {
                CircleImageView circleImageView = new CircleImageView(this);
                int size = (int) Utils.pxFromDp(this, 100);
                circleImageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                circleImageView.setPadding(20, 0, 20, 0);
                try {
                    Glide.with(this).load(Values.BASE_URL_AVATAR + listImageAttachment[i].link).placeholder(R.drawable.loading).dontAnimate().into(circleImageView);
                } catch (Exception e) {

                }
                final int finalI = i;
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewAnswerTextActivity.this, ViewImageActivity.class);
                        intent.putStringArrayListExtra("listImages", strings);
                        intent.putExtra(Values.index, finalI);
                        startActivity(intent);
                    }
                });
                listImage.addView(circleImageView);
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notMyQuestion1:
//                Toast.makeText(this, getString(R.string.comming_soon), Toast.LENGTH_SHORT).show();

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    String uriImage = "";
                    if (answer != null && answer.getAttachments().length > 0) {
                        uriImage = Values.BASE_URL_AVATAR + answer.getAttachments()[0].link;
                    }
                    Uri uri = Uri.parse("http://www.popdq.com/");
                    Uri uriImagee = Uri.parse(uriImage);
                    shareDialog.show(Utils.getShareLinkContent(this, uri, uriImagee, titleQuestion, String.format(getString(R.string.des_share_fb), answer.getUser().getUsername())));
                } else {
                    Toast.makeText(ViewAnswerTextActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnViewProfile:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, answer.getUser().getId());
                startActivity(intent);
                break;
            case R.id.btnViewQuestion:
                finish();
                break;
            case R.id.id_edit_answer:

//                if(answer.getUser().getDisplayName().equals(user.getDisplayName())) {
//
//                    Intent i = new Intent(ViewAnswerTextActivity.this, ProvideUpdateAnswerByTextActivity.class);
//                    i.putExtra(Values.question_id, question_id);
//                    i.putExtra(Values.method, method);
//                    i.putExtra(Values.title, titleQuestion);
//                    i.putExtra(Values.language_written, language_written);
//                    i.putExtra(Values.answer_id,answer.getId());
//
//                    Log.e("extras","extra_qid : "+question_id+"answer_id : "+answer.getId());
//
//                    startActivity(i);
//
//
//                }

                break;


            case R.id.btnReport:
                final ReportDialog reportDialog = new ReportDialog(this);
                reportDialog.setTitleDialog(getString(R.string.report_title_answer));

                reportDialog.setBtnOkClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String message = reportDialog.getMessageReport();
                        if (message == null || message.equals("")) {
                            Toast.makeText(ViewAnswerTextActivity.this, getString(R.string.report_message_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AnswerUtil.report(ViewAnswerTextActivity.this, PreferenceUtil.getToken(ViewAnswerTextActivity.this), answer.getId(), message, new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String reponse, Result result) {
                                Log.e(TAG, " message:" + reportDialog.getMessageReport());
                                Toast.makeText(ViewAnswerTextActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                reportDialog.dismiss();
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                    }
                });
                break;
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (!getIntent().getBooleanExtra(Values.isViewed, false)) {
//            Utils.showDialogShare(ViewAnswerTextActivity.this, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (ShareDialog.canShow(ShareLinkContent.class)) {
//                        String uriImage = "";
//                        if (answer != null && answer.getAttachments().length > 0) {
//                            uriImage = Values.BASE_URL _AVATAR + answer.getAttachments()[0].link;
//                        }
//                        Uri uri = Uri.parse("http://www.popdq.com/");
//                        Uri uriImagee = Uri.parse(uriImage);
//                        shareDialog.show(Utils.getShareLinkContent(ViewAnswerTextActivity.this, uri, uriImagee, titleQuestion, String.format(getString(R.string.des_share_fb), answer.getUser().getUsername())));
//                    } else {
//                        Toast.makeText(ViewAnswerTextActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        } else super.onBackPressed();
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void rate(String content, float star) {
        AnswerUtil.rating(this, PreferenceUtil.getToken(this), answer.getId(), content, star * 2, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                Toast.makeText(ViewAnswerTextActivity.this, getString(R.string.thanks_rate), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {

            }
        });

    }
}
