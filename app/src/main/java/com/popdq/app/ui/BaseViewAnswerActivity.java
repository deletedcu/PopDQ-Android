package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.popdq.app.R;
import com.popdq.app.connection.AnswerUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Answer;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.RateView;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Dang Luu on 25/07/2016.
 */
public class BaseViewAnswerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BaseViewAnswerActivity";
    protected RateView rate;
    protected long question_id;
    protected String token;
    protected long answer_id;
    protected Answer answer;
    protected String titleQuestion;
    protected ImageView imgAvatar,imgAnswer;

    @Override
    public void onClick(View view) {

    }

    protected interface OnLoadAnswerListener {
        void onSuccess(Answer answer);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkStartActivityFromNotificationAndSendRead(this, null);

        token = PreferenceUtil.getToken(this);

        question_id = getIntent().getExtras().getLong(Values.question_id);
        titleQuestion = getIntent().getExtras().getString(Values.title);
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

    public void setRateListener() {
        rate = (RateView) findViewById(R.id.rate);
        rate.setOnRateListiner(new RateView.OnRateListiner() {
            @Override
            public void onClickStar(int numberStar) {
                rate(numberStar);
            }
        });
    }

    public void initData() {

    }

    public void initView() {

    }

    public void rate(int stars) {
        AnswerUtil.rating(this, token, answer.getId(), "", stars * 2, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                if (result.getCode() == 0)
                    Toast.makeText(BaseViewAnswerActivity.this, getString(R.string.thanks_rate), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(BaseViewAnswerActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getData(final OnLoadAnswerListener onLoadAnswerListener) {
        AnswerUtil.search(this, token, question_id, 0, 100, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {

                final List<Answer> answers = AnswerUtil.getAnswerFromJsonRespone(reponse);
                if (answers != null && answers.size() > 0) {
                    answer = answers.get(answers.size() - 1);
                    String name = answer.getUser().getDisplayName();
                    imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
                    imgAnswer = (ImageView) findViewById(R.id.imgAnswer);
                    try {
                        Glide.with(BaseViewAnswerActivity.this).load(Values.BASE_URL_AVATAR + answer.getUser().getAvatar()).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatar);
                        Glide.with(BaseViewAnswerActivity.this).load(Values.BASE_URL_AVATAR + answer.getUser().getAvatar())
//                                .bitmapTransform(new BlurTransformation(getApplicationContext()))
                                .placeholder(R.drawable.list_avatar)
                                .dontAnimate()
                                .into(imgAnswer);
                    } catch (Exception e) {

                    }

                    Log.e("ANSWER VIEW LOG :", Arrays.toString(answer.getAttachments()));

                    if (answer.getUser().getStatus_anonymous() == 0) {
                        ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setText(Utils.getAnonymousName(name));
                    } else {
                        ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setText(name);
                    }
                    ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(BaseViewAnswerActivity.this, UserProfileActivity.class);
                            intent.putExtra(Values.experts_id, answer.getUser_id());
                            startActivity(intent);
                        }
                    });
                    ((TextViewNormal) findViewById(R.id.tvField)).setSelected(true);
                    ((TextViewNormal) findViewById(R.id.tvField)).setText(answer.getUser().getCategoriesString());
                    if (onLoadAnswerListener != null) {
                        onLoadAnswerListener.onSuccess(answer);
                    }
                    imgAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(BaseViewAnswerActivity.this, UserProfileActivity.class);
                            intent.putExtra(Values.experts_id, answer.getUser_id());
                            startActivity(intent);
                        }
                    });
                    if (answer.getUser_id() == PreferenceUtil.getUserId(BaseViewAnswerActivity.this)) {
                        ((TextView) findViewById(R.id.tvGiveRate)).setVisibility(View.GONE);
                        ((RateView) findViewById(R.id.rate)).setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.tvGiveRate)).setVisibility(View.VISIBLE);
                        ((RateView) findViewById(R.id.rate)).setVisibility(View.VISIBLE);
                    }
                    User newUserInfo = VolleyUtils.getUserInfo(reponse);
                    User.changeCreditAndPutPrefernce(BaseViewAnswerActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
//                    User.changeUser(BaseViewAnswerActivity.this, newUserInfo);
                    if (answer.getMyRating() != null)
                        rate.setRate(answer.getMyRating().getRating() / 2);

                } else {
                    Toast.makeText(BaseViewAnswerActivity.this, "No answer!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                //share on first view
//                ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (!getIntent().getBooleanExtra(Values.isViewed, false)) {
//                            Utils.showDialogShare(BaseViewAnswerActivity.this, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    if (ShareDialog.canShow(ShareLinkContent.class)) {
//                                        String uriImage = "";
//                                        if (answer != null && answer.getAttachments().length > 0) {
//                                            uriImage = Values.BASE_URL_AVATAR + answer.getAttachments()[0].link;
//                                        }
//                                        Uri uri = Uri.parse("http://www.popdq.com/");
//                                        Uri uriImagee = Uri.parse(uriImage);
//                                        shareDialog.show(Utils.getShareLinkContent(BaseViewAnswerActivity.this, uri, uriImagee, titleQuestion, String.format(getString(R.string.des_share_fb), answer.getUser().getUsername())));
//                                    } else {
//                                        Toast.makeText(BaseViewAnswerActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        } else finish();
//                    }
//                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if (!getIntent().getBooleanExtra(Values.isViewed, false)) {
//            Utils.showDialogShare(BaseViewAnswerActivity.this, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (ShareDialog.canShow(ShareLinkContent.class)) {
//                        String uriImage = "";
//                        if (answer != null && answer.getAttachments().length > 0) {
//                            uriImage = Values.BASE_URL_AVATAR + answer.getAttachments()[0].link;
//                        }
//                        Uri uri = Uri.parse("http://www.popdq.com/");
//                        Uri uriImagee = Uri.parse(uriImage);
//                        shareDialog.show(Utils.getShareLinkContent(BaseViewAnswerActivity.this, uri, uriImagee, titleQuestion, String.format(getString(R.string.des_share_fb), answer.getUser().getUsername())));
//                    } else {
//                        Toast.makeText(BaseViewAnswerActivity.this, "Can't share this question", Toast.LENGTH_SHORT).show();
//                    }
//                    finish();VV
//                }
//            });
//        } else
//            super.onBackPressed();
//    }

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}