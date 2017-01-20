package com.popdq.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.FileUtil;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.FileModel;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.RealPathUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.DialogNoticeCredit;
import com.popdq.app.view.TranferCreditPopup;
import com.popdq.app.view.VerticalScrollview;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconEditText;

/**
 * Created by Dang Luu on 20/07/2016.
 */
public class ProvideQuestionActivity extends BaseSearchActivity implements View.OnClickListener {

    public static int LIMIT_LENGTH_TITLE = 100;
    private static final int KEY_GET_IMAGE = 24;
    private static final int KEY_SELECT_CATEGORY = 12;
    //    private RelativeLayout btnAskAnonymous;
    private LinearLayout btnReplyMethod;
    private SwitchCompat cbFreePreview;
    private ImageView btnAddImage;
    private LinearLayout horizontalScoll;
    private ArrayList<String> listPathImage;

    private ImageView imgMethod;
    private TextViewNormal textMethod;
    private TextViewNormal tvLanguage;
    private TextViewThin tvCountWord;
//    private TextViewNormal tvAnonymous;

    private EmojiconEditText edtTitle;
    private EditText edtContent;
    private String token;
    private int replyMethod = 1;
    //    private String language = "en";
    private int anonymous_status = 0;
    private long user_answer_id;
    private String language,language_spoken;
    private HorizontalScrollView scrollView;
    private TextViewNormal tvCountImages;
    private TextViewNormal tvYouEarnFromView;
    private float textCredit, voiceCredit, videoCredit;
    private float FEE, FEE_VIEW;
    private String name;
    private String avatar;
    private String professional_field;
    private User your;
    private TextViewNormal edtCategoty;
    private Interest interest;
    private AppCompatCheckBox cbDisclaimer;
    List<User.CreditAnswer> credits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_question);
//        Utils.setBottomButton(this, getString(R.string.pop_this_question), this);
        Utils.setBottomButton(this, "Submit", this);
        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.ask_question));
        token = PreferenceUtil.getInstancePreference(this).getString(Values.TOKEN, "");
        your = User.getInstance(this);
        btnReplyMethod = (LinearLayout) findViewById(R.id.btnReplyMethod);
        horizontalScoll = (LinearLayout) findViewById(R.id.horizontalScoll);
        cbDisclaimer = (AppCompatCheckBox) findViewById(R.id.cbDisclaimer);

        cbFreePreview = (SwitchCompat) findViewById(R.id.cbFreePreview);
        btnAddImage = (ImageView) findViewById(R.id.btnAddImage);

        imgMethod = (ImageView) findViewById(R.id.imgMethod);
        textMethod = (TextViewNormal) findViewById(R.id.textMethod);
        edtCategoty = (TextViewNormal) findViewById(R.id.edtCategoty);
        tvLanguage = (TextViewNormal) findViewById(R.id.tvLanguage);
        tvCountWord = (TextViewThin) findViewById(R.id.tvCountWord);
        tvLanguage.setSelected(true);
        tvCountImages = (TextViewNormal) findViewById(R.id.tvCountImages);
        tvYouEarnFromView = (TextViewNormal) findViewById(R.id.tvYouEarnFromView);

        edtTitle = (EmojiconEditText) findViewById(R.id.edtTitle);
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtContent.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCountWord.setText(LIMIT_LENGTH_TITLE - edtTitle.length() + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtTitle.setTypeface(MyApplication.getInstanceTypeNormal(this));
        edtContent.setTypeface(MyApplication.getInstanceTypeNormal(this));
        edtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    try {
                        Utils.hideKeyBoard(ProvideQuestionActivity.this);
                    } catch (Exception e) {

                    }
                }
                return false;
            }
        });
        cbFreePreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvYouEarnFromView.setText(String.format(getString(R.string.you_earn_from_view_question), "$" + "0.0"  ));

                } else {
                    tvYouEarnFromView.setText(String.format(getString(R.string.you_earn_from_view_question), "$" + FEE_VIEW));

                }
            }
        });
        tvYouEarnFromView.setText(String.format(getString(R.string.you_earn_from_view_question), "$" + "0.0"));


        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);

        btnAddImage.setOnClickListener(this);
//        btnAskAnonymous.setOnClickListener(this);

        btnReplyMethod.setOnClickListener(this);
        edtCategoty.setOnClickListener(this);

        listPathImage = new ArrayList<>();
        fileModels = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        user_answer_id = bundle.getLong(Values.user_id_answer);
        language = bundle.getString(Values.language_written, "All");
        language_spoken = bundle.getString(Values.language_written, "All");

        avatar = bundle.getString(Values.avatar, "");
        professional_field = bundle.getString(Values.professional_field, "No information");
        name = bundle.getString(Values.name, "");

        textCredit = bundle.getFloat(Values.text_credit);
        voiceCredit = bundle.getFloat(Values.voice_credit);
        videoCredit = bundle.getFloat(Values.video_credit);
        FEE = textCredit;
        interest = new Interest(1, "");

        Intent intent = getIntent();
        if (intent.hasExtra(Values.category)) {
            interest = new Gson().fromJson(intent.getStringExtra(Values.category), Interest.class);
            edtCategoty.setText(interest.getName());
        }

        if (language.equals("")) language = "All";
        tvLanguage.setText(language);
//        tvAnonymous.setText(anonymous_status == 1 ? "Yes" : "No");

        ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtTitle.getText().toString().length() > 0 || edtContent.getText().toString().length() > 0 || (fileModels != null && fileModels.size() > 0))

                    Utils.confirmExit(ProvideQuestionActivity.this);
                else {
                    finish();
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((ScrollView) findViewById(R.id.scroll)).setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    try {
                        Utils.hideKeyBoard(ProvideQuestionActivity.this);
                    } catch (Exception e) {

                    }
                }
            });
        }

        ((ImageView) findViewById(R.id.iconNavigation)).setImageResource(R.drawable.btn_back);
        ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setText(name);
        ((TextViewNormal) findViewById(R.id.tvField)).setText(professional_field);
        ((TextViewNormal) findViewById(R.id.tvField)).setSelected(true);
        ((TextViewNormal) findViewById(R.id.tvNameUserAnswer)).setText(String.format(getString(R.string.replied_in), name));
        ((CircleImageView) findViewById(R.id.imgAvatar)).setOnClickListener(this);
        ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setOnClickListener(this);
        try {
            Glide.with(this).load(Values.BASE_URL_AVATAR + avatar).dontAnimate().placeholder(R.drawable.avatar).into(((CircleImageView) findViewById(R.id.imgAvatar)));
        } catch (Exception e) {

        }
        credits = new ArrayList<>();
        if (textCredit != -1)
            credits.add(new User.CreditAnswer(1, textCredit, getString(R.string.reply_method_text)));
        if (voiceCredit != -1)
            credits.add(new User.CreditAnswer(2, voiceCredit, getString(R.string.reply_method_voice)));
        if (videoCredit != -1)
            credits.add(new User.CreditAnswer(3, videoCredit, getString(R.string.reply_method_video)));
        if (credits != null && credits.size() > 0) {
            textMethod.setText(credits.get(0).summary + " $" + credits.get(0).price);
            setMethodView(credits.get(0).type);

        }

    }

    public void setMethodView(int i) {
        replyMethod = i;
        if (i == 1) {
            FEE_VIEW = 0.025f;
            imgMethod.setImageResource(R.drawable.ic_text_white);
            FEE = textCredit;

            Log.e("TEXT FEE", String.valueOf(FEE));

            if(String.valueOf(FEE)== "0.0" || String.valueOf(FEE).equals("0.0")){

                textMethod.setText(getString(R.string.reply_method_text) + " FREE ");
            } else {
                textMethod.setText(getString(R.string.reply_method_text) + " $" + FEE);
            }
        } else if (i == 2) {
            FEE_VIEW = 0.05f;

            imgMethod.setImageResource(R.drawable.ic_audio_white);
            FEE = voiceCredit; if(String.valueOf(FEE)== "0.0" || String.valueOf(FEE).equals("0.0")){

                textMethod.setText(getString(R.string.reply_method_voice) + " FREE ");
            } else {
                textMethod.setText(getString(R.string.reply_method_voice) + " $" + FEE);
            }
        } else if (i == 3) {
            FEE_VIEW = 0.075f;

            imgMethod.setImageResource(R.drawable.ic_video_white);
            FEE = videoCredit; if(String.valueOf(FEE)== "0.0" || String.valueOf(FEE).equals("0.0")){

                textMethod.setText(getString(R.string.reply_method_video) + " FREE ");
            } else {
                textMethod.setText(getString(R.string.reply_method_video) + " $" + FEE);
            }
        }
//        if (FEE == 0) {
//            FEE_VIEW = 0;
//        }
        if (!cbFreePreview.isChecked())
            tvYouEarnFromView.setText(String.format(getString(R.string.you_earn_from_view_question), "$" + FEE_VIEW));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (KEY_GET_IMAGE == requestCode)
        if (Utils.checkPermission(this) && requestCode == Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            Utils.galleryIntentDefault(this, KEY_GET_IMAGE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtCategoty:
                Intent intent = new Intent(this, SelectCategoryActivity.class);
                intent.putExtra(Values.GET_CATEGORY, true);
                startActivityForResult(intent, KEY_SELECT_CATEGORY);
                break;

            case R.id.btnAddImage:
                if (Utils.checkPermission(this)) {
                    Utils.galleryIntentDefault(this, KEY_GET_IMAGE);
                }
                break;
            case R.id.btnBottom:
                ArrayList<Integer> listNotComplete = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder(getString(R.string.not_complete_form));
                if (edtCategoty.length() < 1) {
                    listNotComplete.add(1);
                    stringBuilder.append(getString(R.string.notice_not_complete1));
                }
                if (edtTitle.length() < 1) {
                    listNotComplete.add(2);
                    stringBuilder.append(getString(R.string.notice_not_complete2));
                }

                if (!cbDisclaimer.isChecked()) {
                    listNotComplete.add(4);
                    stringBuilder.append(getString(R.string.notice_not_complete4));
                }

                if (listNotComplete.size() >= 1) {
                    final DialogBase dialogBase = new DialogBase(this);
                    dialogBase.setTitle(getString(R.string.notice));
                    dialogBase.setMessage(stringBuilder.toString());
                    dialogBase.setOnClickCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBase.dismiss();
                        }
                    });
                    dialogBase.setTextOk("OK");
                    dialogBase.setOnClickOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBase.dismiss();
                        }
                    });
                    dialogBase.show();
                } else {
                    checkEnoughCredit();
                }
//                loadImageAsyn();

                break;

//            case R.id.btnAskAnonymous:
//                final String[] anonymous = new String[]{"Yes", "No"};
//                showDialogchoice(getString(R.string.anonymous_mode), anonymous, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
////                        tvAnonymous.setText(anonymous[i]);
//                        if (i == 0) {
//                            anonymous_status = 1;
//                        } else anonymous_status = 0;
//                    }
//                });
//                break;

            case R.id.btnReplyMethod:
//                final String[] reply_methods = getResources().getStringArray(R.array.reply_methods);
////                for (int i = 0; i < reply_methods.length; i++) {
//                reply_methods[0] = reply_methods[0] + " ($" + textCredit + ")";
//                reply_methods[1] = reply_methods[1] + " ($" + voiceCredit + ")";
//                reply_methods[2] = reply_methods[2] + " ($" + videoCredit + ")";
//                }
                showDialogchoice(getString(R.string.reply_method), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

//                        replyMethod = i + 1;
                        setMethodView(credits.get(i).type);
                    }
                });
                break;
            case R.id.imgAvatar:
            case R.id.tvNameAnswer:
                Intent intent1 = new Intent(this, UserProfileActivity.class);
                intent1.putExtra(Values.experts_id, user_answer_id);
                startActivity(intent1);
                break;

        }
    }

    public void checkEnoughCredit() {
        your = User.getInstance(this);

        if (FEE == 0) {
            loadImageAsyn();
            return;
        }
        if (your.getCredit() < FEE && your.getCredit_earnings() > FEE) {
            String title = String.format(getString(R.string.title_ask_dialog), name);
            String message = getString(R.string.message_dialog_tranfer);
            DialogNoticeCredit.showDialogAskOrTranfer(this, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                @Override
                public void onClickOk() {
                    TranferCreditPopup.showDialogConvert(ProvideQuestionActivity.this, new TranferCreditPopup.TranferListener() {
                        @Override
                        public void onSuccess(float credit) {
                            checkEnoughCredit();
                        }
                    });
                }
            });

//            new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TranferCreditPopup.showDialogConvert(ProvideQuestionActivity.this, new TranferCreditPopup.TranferListener() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//                    });
//
//
////                    dialog.dismiss();
//                }
//            });
        } else if (your.getCredit() < FEE && your.getCredit_earnings() < FEE) {
            String title = getString(R.string.no_credit);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
            String message = String.format(getString(R.string.message_dialog_buy_credit), "$" + FEE + "");
            DialogNoticeCredit.showDialogAskOrTranfer(this, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                @Override
                public void onClickOk() {
                    startActivity(new Intent(ProvideQuestionActivity.this, BuyCreditActivityBackup.class));

                }
            });

//            new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        } else if (your.getCredit() > FEE) {
            String title = String.format(getString(R.string.title_ask_dialog), name);
            String message = String.format(getString(R.string.message_dialog_view_answer), "$" + FEE);
            DialogNoticeCredit.showDialogAskOrTranfer(ProvideQuestionActivity.this, title, message, new DialogNoticeCredit.OnClickDialogListener() {
                @Override
                public void onClickOk() {
                    loadImageAsyn();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_GET_IMAGE && resultCode == Activity.RESULT_OK) {
            addImages(data);
        }
        if (requestCode == KEY_SELECT_CATEGORY && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(Values.category);
            interest = new Gson().fromJson(result, Interest.class);
            edtCategoty.setText(interest.getName());
        }

    }

    public void loadImageAsyn() {
        MixPanelUtil.trackPopThisQuestion(this);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading image...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String reponse = null;
                try {
                    reponse = FileUtil.sendFile(Values.URL_FILE_UPLOAD, token, fileModels);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return reponse;
            }

            @Override
            protected void onPostExecute(String reponse) {
                super.onPostExecute(reponse);
//                try {
                progressDialog.dismiss();
//                    JSONObject jsonObject = new JSONObject(reponse);
//                    String attachments = jsonObject.getString("attachments");
                int freeForPreview = cbFreePreview.isChecked() ? 1 : 0;
                QuestionUtil.create(ProvideQuestionActivity.this, token, interest.getId() + "", user_answer_id,
                        edtTitle.getText().toString(),
                        edtContent.getText().toString(), reponse, "", anonymous_status, replyMethod, language,language,
                        freeForPreview, 2, new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String reponse, Result result) {
                                Log.e("ask respone:", reponse);
                                if (result.getCode() == 0) {
                                    Toast.makeText(ProvideQuestionActivity.this, getString(R.string.ask_success), Toast.LENGTH_SHORT).show();
                                    User newUserInfo = VolleyUtils.getUserInfo(reponse);
                                    User.changeCreditAndPutPrefernce(ProvideQuestionActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
                                    finish();
//                                    Intent intent = new Intent(ProvideQuestionActivity.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ProvideQuestionActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(ProvideQuestionActivity.this, getString(R.string.ask_failed), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }.execute();

    }

    private List<FileModel> fileModels;

    public void addImages(Intent data) {
        String realPath = "";
        try {
            realPath = RealPathUtil.getRealPathAll(this, data.getData());
        } catch (Exception e) {

        }
        if (realPath == null || realPath.equals("") && data != null && data.getData() != null) {
            realPath = data.getData().toString();
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
            bitmap = FileUtil.compressBitmap(bitmap);
            final CircleImageView image = new CircleImageView(this);
            int size = (int) Utils.pxFromDp(this, 100);
            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            image.setPadding(20, 0, 20, 0);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvideQuestionActivity.this);
                    builder.setMessage(getString(R.string.do_you_want_delete_this_image));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(ProvideQuestionActivity.this, image.getTag().toString(), Toast.LENGTH_SHORT).show();
                            horizontalScoll.removeView(image);
                            fileModels.remove(image.getTag());
                            tvCountImages.setText("(" + fileModels.size() + ")");


                        }
                    });
                    builder.setNegativeButton("CANCEL", null);
                    builder.show();


                }
            });
            String newPathCompressed = FileUtil.getPathBitmapCompressed(bitmap);

            String newPath = newPathCompressed != null ? newPathCompressed : realPath;

            File file = new File(newPath);
            FileModel fileModel = new FileModel(newPath, file.length(), 0);
            fileModels.add(fileModel);

            Glide.with(ProvideQuestionActivity.this).load(newPath).dontAnimate().into(image);
//            image.setImageBitmap(bitmap);
            image.setTag(fileModel);
            horizontalScoll.addView(image, fileModels.size() - 1);
            tvCountImages.setText("(" + fileModels.size() + ")");
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showDialogchoice(String title, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title.toUpperCase());
        ArrayAdapter<User.CreditAnswer> creditArrayAdapter = new ArrayAdapter<User.CreditAnswer>(this, R.layout.item_spiner_text_credit) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = ProvideQuestionActivity.this.getLayoutInflater().inflate(R.layout.item_spiner_text_credit, null);
                TextViewNormal tvName = (TextViewNormal) view.findViewById(R.id.text1);
                float credit = credits.get(position).price;
                if (credit <= 0) {
                    tvName.setText("" + credits.get(position).summary + " (FREE)");

                } else
                    tvName.setText("" + credits.get(position).summary + " ($" + credit + ")");


                return view;
            }

            @Override
            public int getCount() {
                return credits.size();
            }
        };
        builder.setAdapter(creditArrayAdapter, onClickListener);
        builder.show();

    }

    @Override
    public void onBackPressed() {
        if (edtTitle.getText().toString().length() > 0 || edtContent.getText().toString().length() > 0 || (fileModels != null && fileModels.size() > 0))

            Utils.confirmExit(ProvideQuestionActivity.this);
        else {
            finish();
        }
    }

    /*  public enum ACTION_SHOW_DIALOG {
        ANONYMOUS,
        LANGUAGE,
        REPLY_METHOD
    }
*/

//    Dialog dialog;

//    public void showDialogAskOrTranfer(String title, String message, View.OnClickListener okClick) {
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.layout_dialog_notice_ask);
//        ((TextViewNormal) dialog.findViewById(R.id.tvTitle)).setText(title);
//        ((TextViewNormal) dialog.findViewById(R.id.tvTotalCredit)).setText("$" + user.getCredit());
//        ((TextViewNormal) dialog.findViewById(R.id.tvMessage)).setText(message);
//        ((Button) dialog.findViewById(R.id.btnOk)).setOnClickListener(okClick);
//        ((Button) dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
}
