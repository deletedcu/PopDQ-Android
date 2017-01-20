package com.popdq.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.popdq.app.MainActivity;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.FileUtil;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Credit;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.libs.Constant;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

public class EditProfileActivity extends BaseSearchActivity implements View.OnClickListener {
    private static final int KEY_GET_IMAGE = 754;
    private static final String TAG = "ProfileInActivity";
    private static final int KEY_SELECT_CATEGORY = 65;
    private static final int KEY_GET_LANGUAGE = 22;
    private static final int ACTION_CROP_PICTURE = 1;
    private static final int REQUEST_CAPTURE_PHOTO = 2;
    public static final int REQUEST_PERMISSION_CAMERA = 3;

    private ScrollView scrollView;
    private TextViewNormal textCredit, voiceCredit, videoCredit;
    private String userName, firstName, lastName,uname;
    private float valueTextCredit, valueVoiceCredit, valueVideoCredit;
    private CheckBox status_anonymous;
    private int anonymous;
    private String urlAvartar;
    private String picturePath = "";

    private CircleImageView imgAvatar;
    private TextViewNormal tvLocation;
    private EditText edtDescription;
    private TextViewNormal edtProfesstionnalField;
    private EditText edtLastName, edtFirstName,edtUname;
    User user;
    //    private LinearLayout btnLanguage;
    private String language;
    private RelativeLayout btnLanguage,btnSave;
    private TextViewNormal tvLanguage;
    private float creditText = -1, creditVoice = -1, creditVideo = -1;
    private TextViewBold tvUserName;
    private LinearLayout btnProfession;
    private TextViewNormal tvEarnVideo, tvEarnVoice, tvEarnText;
    private String idCategories = "";
    private TextViewNormal tvWhy1, tvWhy2, tvWhy3;
    private View stubView;
    private ViewStub vStub;

    private List<Credit> credits;
    private Uri mCropImageUri;
    private InputForm newPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user = User.getInstance(this);
        userName = user.getUsername();
        anonymous = user.getStatus_anonymous();
        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(this);
        tvUserName = (TextViewBold) findViewById(R.id.tvUserName);
        edtUname = (EditText) findViewById(R.id.edtuname);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtFirstName.setTypeface(MyApplication.getInstanceTypeNormal(this));
        edtLastName.setTypeface(MyApplication.getInstanceTypeNormal(this));
        edtProfesstionnalField = (TextViewNormal) findViewById(R.id.edtProfesstionnalField);
        btnProfession = (LinearLayout) findViewById(R.id.btnProfession);
        tvLocation = (TextViewNormal) findViewById(R.id.tvLocation);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        newPassword = (InputForm) findViewById(R.id.newPassword);
        edtDescription.setTypeface(MyApplication.getInstanceTypeNormal(this));


        btnLanguage = (RelativeLayout) findViewById(R.id.btnLanguage);
        btnSave = (RelativeLayout) findViewById(R.id.btnEdit);
        tvLanguage = (TextViewNormal) findViewById(R.id.tvLanguage);
        btnLanguage.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        status_anonymous = (CheckBox) findViewById(R.id.status_anonymous);

//        tvLanguageSpoken.setTypeface(MyApplication.getInstanceTypeNormal(this));
//        tvLanguageWritten.setTypeface(MyApplication.getInstanceTypeNormal(this));
//        tvLanguageSpoken.setThreshold(1);
//        tvLanguageWritten.setThreshold(1);

        textCredit = (TextViewNormal) findViewById(R.id.textCredit);
        voiceCredit = (TextViewNormal) findViewById(R.id.voiceCredit);
        videoCredit = (TextViewNormal) findViewById(R.id.videoCredit);

        tvEarnText = (TextViewNormal) findViewById(R.id.tvEarnText);
        tvEarnVoice = (TextViewNormal) findViewById(R.id.tvEarnVoice);
        tvEarnVideo = (TextViewNormal) findViewById(R.id.tvEarnVideo);


        tvEarnText.setMovementMethod(LinkMovementMethod.getInstance());
        tvEarnVoice.setMovementMethod(LinkMovementMethod.getInstance());
        tvEarnVideo.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString content = new SpannableString(getString(R.string.why));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvWhy1 = (TextViewNormal) findViewById(R.id.tvWhy1);
        tvWhy2 = (TextViewNormal) findViewById(R.id.tvWhy2);
        tvWhy3 = (TextViewNormal) findViewById(R.id.tvWhy3);
        tvWhy1.setText(content);
        tvWhy2.setText(content);
        tvWhy3.setText(content);
        tvWhy1.setOnClickListener(this);
        tvWhy2.setOnClickListener(this);
        tvWhy3.setOnClickListener(this);

        tvLocation.setOnClickListener(this);
        textCredit.setOnClickListener(this);
        voiceCredit.setOnClickListener(this);
        videoCredit.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        vStub = (ViewStub) findViewById(R.id.v_stub);


        tvLanguage.setText(user.getLanguage_answer() + "");
        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.my_profile));
        credits = CreditUtils.getListCreditsAdded0AndDisable(this);

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 500);
//
//        Utils.setBottomButton(this, getString(R.string.save), new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateUserInfo();
//            }
//        });



        if (user == null) {
            return;
        } else {
            Glide.with(this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(imgAvatar);
            tvUserName.setText(user.getUsername());
            edtUname.setText(user.getUsername());
            tvLocation.setText(user.getRealAddress());
            edtFirstName.setText(user.getFirstname());
            edtLastName.setText(user.getLastname());
            edtProfesstionnalField.setText(user.getProfessional_field());
            idCategories = user.getIdCategoriesArr();
            edtDescription.setText(user.getRealDescription());
            btnProfession.setOnClickListener(this);
            edtDescription.setOnTouchListener(new View.OnTouchListener() {

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
            try {
                creditText = user.getConfig_charge()[0].price;
                if (creditText == 0) {
                    textCredit.setText("FREE " + getString(R.string.per_text_price));
                    tvEarnText.setText(String.format(getString(R.string.you_earn), "$0"));

                } else if (creditText == -1) {
                    textCredit.setText(getString(R.string.select_text_price));
                    tvEarnText.setText(String.format(getString(R.string.you_earn), "$0"));
                } else {
                    textCredit.setText(creditText + " " + getString(R.string.per_text_price));
                    tvEarnText.setText(String.format(getString(R.string.you_earn), "$" + creditText / 2));
                    Log.e(TAG, "textCredit settext " + user.getConfig_charge()[0].price);
                }
            } catch (Exception e) {
                Log.e(TAG, "text: " + e.toString());
                tvEarnText.setText(String.format(getString(R.string.you_earn), "$0"));

            }
            try {
                creditVoice = user.getConfig_charge()[1].price;
                if (creditVoice == 0) {
                    voiceCredit.setText("FREE " + getString(R.string.per_voice_price));
                    tvEarnVoice.setText(String.format(getString(R.string.you_earn), "$0"));


                } else if (creditVoice == -1) {
                    voiceCredit.setText(getString(R.string.select_voice_price));
                    tvEarnVoice.setText(String.format(getString(R.string.you_earn), "$0"));
                } else {
                    voiceCredit.setText(creditVoice + " " + getString(R.string.per_voice_price));
                    tvEarnVoice.setText(String.format(getString(R.string.you_earn), "$" + creditVoice / 2));
                    Log.e(TAG, "voice settext " + user.getConfig_charge()[1].price);
                }
            } catch (Exception e) {
                tvEarnVoice.setText(String.format(getString(R.string.you_earn), "$0"));

                Log.e(TAG, "voice: " + e.toString());

            }
            try {
                creditVideo = user.getConfig_charge()[2].price;
                if (creditVideo == 0) {
                    videoCredit.setText("FREE " + getString(R.string.per_video_price));
                    tvEarnVideo.setText(String.format(getString(R.string.you_earn), "$0"));
                } else if (creditVideo == -1) {
                    videoCredit.setText(getString(R.string.select_video_price));
                    tvEarnVideo.setText(String.format(getString(R.string.you_earn), "$0"));
                } else {
                    videoCredit.setText(creditVideo + " " + getString(R.string.per_video_price));
                    tvEarnVideo.setText(String.format(getString(R.string.you_earn), "$" + creditVideo / 2));

                    Log.e(TAG, "videoCredit settext error " + user.getConfig_charge()[2].price);
                }

            } catch (Exception e) {
                tvEarnVideo.setText(String.format(getString(R.string.you_earn), "$0"));

                Log.e(TAG, "video: " + e.toString());
            }


            status_anonymous.setChecked(user.getStatus_anonymous() == 0 ? true : false);
            hideSoftKeyboard();

//            Glide.with(this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).into(imgAvatar);
//setTextInfo
        }
//        idCategories = user.getCategoriesString();

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public String getConfig_charge() {
        List<User.CreditAnswer> answers = new ArrayList<>();

//        User.CreditAnswer creditAnswer1 = null, creditAnswer2 = null, creditAnswer3 = null;
//        String param1 = "", param2 = "", param3 = "";

        if (creditText >= -1) {
//            creditAnswer1 = ;
            answers.add(new User.CreditAnswer(1, creditText, ""));
        }
        if (creditVoice >= -1) {
            answers.add(new User.CreditAnswer(2, creditVoice, ""));
//            creditAnswer2 = new User.CreditAnswer(2, creditVoice, "");
//            Gson gson = new Gson();
//            param2 = gson.toJson(creditAnswer2)+ "," ;
        }
        if (creditVideo >= -1) {
            answers.add(new User.CreditAnswer(3, creditVideo, ""));

//            creditAnswer3 = new User.CreditAnswer(3, creditVideo, "");
//            Gson gson = new Gson();
//            param3 = gson.toJson(creditAnswer3);
        }
//        creditAnswers[0] = creditAnswer1;
//        creditAnswers[1] = creditAnswer2;
//        creditAnswers[2] = creditAnswer3;

//        if(creditAnswer1!=null){
//            answers.add(creditAnswer1);
//        }
        String s = "";
        if (answers == null || answers.size() < 1) {
        } else {
            Gson gson = new Gson();
            s = gson.toJson(answers);
        }


//        String result = "[" + param1  + param2 + param3 + "]";
        return s;
    }

    public void updateAvatar(final Bitmap bitmap) {
        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_UPDATE_INFO_USER);
        String base64Image = Utils.getBase64Image(FileUtil.scaleBitmap(bitmap));
        volleyUtils.addParam(Values.avatar, base64Image);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    imgAvatar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Glide.clear(imgAvatar);
                            imgAvatar.setImageBitmap(bitmap);
                        }
                    }, 100);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();
    }


    public void updateUserInfo() {
//        userName = edtUserName.getText().toString();
        uname = edtUname.getText().toString();

        if(uname.length() <= 0 || uname.isEmpty()){
            uname = user.getUsername();
        }

        firstName = edtFirstName.getText().toString();
        lastName = edtLastName.getText().toString();
        anonymous = status_anonymous.isChecked() ? 0 : 1;
        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_UPDATE_INFO_USER);
        volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(this).getString(Values.TOKEN, ""));
        volleyUtils.addParam(Values.username, uname);
        volleyUtils.addParam(Values.firstname, firstName);
        volleyUtils.addParam(Values.lastname, lastName);
        volleyUtils.addParam(Values.config_charge, getConfig_charge());
        volleyUtils.addParam(Values.professional_field, edtProfesstionnalField.getText().toString());
        volleyUtils.addParam(Values.language_written, tvLanguage.getText().toString());
        volleyUtils.addParam(Values.address, tvLocation.getText().toString());
        volleyUtils.addParam(Values.description, edtDescription.getText().toString());
        volleyUtils.addParam(Values.status_anonymous, anonymous + "");
        String uriAvatar = PreferenceUtil.getInstancePreference(this).getString(Values.avatar, "");
//        Uri uri = Uri.parse("file://" + uriAvatar);
        Uri uri = Uri.parse(uriAvatar);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), uri);
            int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
            ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
            bitmap = (FileUtil.compressBitmap(bitmap));
            String base64Image = Utils.getBase64Image(FileUtil.scaleBitmap(bitmap));
            volleyUtils.addParam(Values.avatar, base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(final String reponse, Result result) {


                Log.e("UPLOAD S", reponse);
                if (result.getCode() == 0) {
                    if (idCategories.equals("[]") || idCategories.equals("")) {
                        if (result.getCode() == 0) {
                            volleyUtils.getUserAndPushPreference(EditProfileActivity.this, reponse);
                            updateInfoSusscess();

                        }
                    } else {


                        UserUtil.updateCategories(EditProfileActivity.this, PreferenceUtil.getToken(EditProfileActivity.this), idCategories, new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String response, Result result) {
                                if (result.getCode() == 0) {
                                    UserUtil.getMyProfile(EditProfileActivity.this, PreferenceUtil.getToken(EditProfileActivity.this), new VolleyUtils.OnRequestListenner() {
                                        @Override
                                        public void onSussces(String response, Result result) {
                                            if (result.getCode() == 0) {
                                                volleyUtils.getUserAndPushPreference(EditProfileActivity.this, response);
                                                updateInfoSusscess();
                                            } else
                                                Toast.makeText(EditProfileActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                } else {
                    if (result.getCode() == 18) {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }


            }

            @Override
            public void onError(String error) {
                Log.e("UPLOAD R", error);
            }
        });
        volleyUtils.query();
    }

    public void updateInfoSusscess() {
        final String newPass = newPassword.getEdtText().getText().toString();
        if (newPass.length() > 0) {
            if (newPass.length() < 6 || newPass.length() > 20) {
                Toast.makeText(this, getString(R.string.password_more_than_6), Toast.LENGTH_SHORT).show();
                return;
            } else {
                String oldPass = PreferenceUtil.getInstancePreference(EditProfileActivity.this).getString(Values.password, "");
                UserUtil.updatePassword(EditProfileActivity.this, PreferenceUtil.getToken(EditProfileActivity.this), oldPass, newPass, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (result.getCode() == 0) {
                            PreferenceUtil.getInstanceEditor(EditProfileActivity.this).putString(Values.password, newPass);
                            PreferenceUtil.getInstanceEditor(EditProfileActivity.this).commit();
                            VolleyUtils.getUserAndPushPreference(EditProfileActivity.this, response);
//                            Intent intent = new Intent("change_user");
//                            LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(intent);
                            Toast.makeText(EditProfileActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            setResult(Activity.RESULT_OK);
//                            finish();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        } else {
//            Intent intent = new Intent("change_user");
//            LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(intent);
            Toast.makeText(EditProfileActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvWhy1:
            case R.id.tvWhy2:
            case R.id.tvWhy3:
                Utils.goToMyWebView(this, getString(R.string.how_work), Values.LINK_FAQ);
//                Toast.makeText(EditProfileActivity.this, "Why????", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tvLocation:
                chooseLocation();
                break;
            case R.id.btnProfession:
//                String idCategory = PreferenceUtil.getCategoryArr(this);
//                Log.e("USER LOGIN CATEGORY NEW", idCategory);
                Intent intent = new Intent(this, MyInterestActivity.class);
                intent.putExtra(Values.GET_CATEGORY, true);
//                intent.putExtra(Values.category, idCategories);
                intent.putExtra(Values.category, user.getIdCategoriesArr());

                startActivityForResult(intent, KEY_SELECT_CATEGORY);

//                String logcategory = String.valueOf(user.getCategories());
//                String logcategoryString = String.valueOf(user.getCategoriesString());
//                String logrealcategory = String.valueOf(user.getRealCategory());
//                String logIdCategory = String.valueOf(user.getIdCategoriesArr());
//                Log.e("Log Category", logcategory);
//                Log.e("Log Category String", logcategoryString);
//                Log.e("Log Real Category", logrealcategory);
//                Log.e("Log ID Category", logIdCategory);


                if(user.getCategories() != null){
                    for (int i=0;i<user.getCategories().size();i++){

                        Log.e("USER LOGIN CATEGORY", user.getCategories().get(i).getName());
                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getCategories()));
                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getIdCategoriesArr()));
                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getRealCategory()));
                        Log.e("USER LOGIN CATEGORY", String.valueOf(user.getCategoriesString()));
                        Log.e("USER LOGIN CHECK", user.getName());
                    }
                } else {
                    Log.e("USER LOGIN CATEGORY", "KOSONG COY");
                    Log.e("USER LOGIN CHECK", user.getName());
                }

                break;
            case R.id.btnEdit:

                updateUserInfo();

                break;
            case R.id.btnLanguage:
                Intent intent1 = new Intent(this, LanguageActivity.class);
                intent1.putExtra(Values.language_written, tvLanguage.getText().toString());
                startActivityForResult(intent1, KEY_GET_LANGUAGE);


//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMultiChoiceItems(Values.languages, null, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                        Toast.makeText(EditProfileActivity.this, Values.languages[i].toString() +" "+b, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.show();


//                showDialogchoice(getString(R.string.language_written), LanguageCommon.LANGUAGE, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        tvLanguage.setText(LanguageCommon.LANGUAGE[i]);
//                        language_written = LanguageCommon.LANGUAGE_CODE[i];
//                    }
//                });
                break;
            case R.id.imgAvatar:
                if (stubView == null) {
                    stubView = vStub.inflate();
                    stubView.setOnClickListener(this);
                    View vLibrary = stubView.findViewById(R.id.v_library);
                    vLibrary.setOnClickListener(this);
                    View vCamera = stubView.findViewById(R.id.v_camera);
                    vCamera.setOnClickListener(this);
                    View vCancel = stubView.findViewById(R.id.v_cancel);
                    vCancel.setOnClickListener(this);
                } else {
                    vStub.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.v_library:
                if (Utils.checkPermission(this)) {
                    Utils.galleryIntentGallery(this, PICK_IMAGE_CHOOSER_REQUEST_CODE);
                }
                vStub.setVisibility(View.GONE);
                break;
            case R.id.v_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    List<String> lstPermissions = new ArrayList<>();

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        lstPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        lstPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        lstPermissions.add(Manifest.permission.CAMERA);
                    }

                    if (lstPermissions.size() == 0) {
                        File file = new File(Utils.getAppSubDirectory(this, Constant.PICTURES), System
                                .currentTimeMillis() + ".jpg");
                        picturePath = file.getPath();
                        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intent2, REQUEST_CAPTURE_PHOTO);
                    } else {
                        String[] permissions = new String[lstPermissions.size()];
                        for (int i = 0; i < lstPermissions.size(); i++) {
                            permissions[i] = lstPermissions.get(i);
                        }
                        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CAMERA);
                    }
                } else {
                    File file = new File(Utils.getAppSubDirectory(this, Constant.PICTURES), System
                            .currentTimeMillis() + ".jpg");
                    picturePath = file.getPath();
                    Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent3.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent3, REQUEST_CAPTURE_PHOTO);
                }
                vStub.setVisibility(View.GONE);
                break;
            case R.id.textCredit:
                chooseCredit(getString(R.string.text_price), 1, textCredit, tvEarnText);
                break;
            case R.id.voiceCredit:
                chooseCredit(getString(R.string.voice_price), 2, voiceCredit, tvEarnVoice);

                break;
            case R.id.videoCredit:
                chooseCredit(getString(R.string.video_price), 3, videoCredit, tvEarnVideo);

                break;

            case R.id.v_cancel:
                vStub.setVisibility(View.GONE);
                break;
        }

    }

    private void chooseLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.country));
        builder.setItems(MyApplication.getInstanceListCountries(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvLocation.setText(MyApplication.getInstanceListCountries()[i]);
            }
        });
        builder.show();

    }


    public void chooseCredit(String title, final int type, final TextViewNormal tvCredit, final TextViewNormal tvEarn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (type == 1) {
            credits.get(credits.size() - 1).setCredit(getString(R.string.disable_text_reply));
        } else if (type == 2) {
            credits.get(credits.size() - 1).setCredit(getString(R.string.disable_voice_reply));

        } else if (type == 3) {
            credits.get(credits.size() - 1).setCredit(getString(R.string.disable_video_reply));

        }
        builder.setTitle(title.toUpperCase());
        ArrayAdapter<Credit> creditArrayAdapter = new ArrayAdapter<Credit>(this, R.layout.item_spiner_text_credit) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = EditProfileActivity.this.getLayoutInflater().inflate(R.layout.item_spiner_text_credit, null);
                TextViewNormal tvName = (TextViewNormal) view.findViewById(R.id.text1);
                tvName.setText("" + credits.get(position).getCredit());
                return view;
            }

            @Override
            public int getCount() {
                return credits.size();
            }
        };

        builder.setAdapter(creditArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        float credit = -2;
                        String perText = "";
                        if (i == 0) {
                            credit = 0;
                        } else if (i == credits.size() - 1) {
                            credit = -1;
                        } else {
                            try {
                                credit = Float.parseFloat(credits.get(i).getCredit().replace("$", ""));
                            } catch (Exception e) {
                                credit = 0;
                            }
                        }
                        if (type == 1) {
                            perText = getString(R.string.per_text_price);
                            creditText = credit;
                        } else if (type == 2) {
                            creditVoice = credit;
                            perText = getString(R.string.per_voice_price);

                        } else if (type == 3) {
                            creditVideo = credit;
                            perText = getString(R.string.per_video_price);

                        }
                        if (i == 0) {
                            tvCredit.setText("FREE " + perText);
                            tvEarn.setText(String.format(getString(R.string.you_earn), "$0"));
                        } else if (i == credits.size() - 1) {
                            tvCredit.setText(credits.get(i).getCredit());
                            tvEarn.setText(String.format(getString(R.string.you_earn), "$0"));

                        } else {
                            tvCredit.setText(credit + " " + perText);
                            tvEarn.setText(String.format(getString(R.string.you_earn), "$" + credit / 2 + ""));

                        }
//                        Toast.makeText(EditProfileActivity.this, credit + "", Toast.LENGTH_SHORT).show();
                    }
                }

        );
        builder.show();
    }

    public void showDialogchoice(String title, String[] list, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(list, onClickListener);
        builder.setTitle(title);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            boolean isGranted = false;
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    } else {
                        isGranted = true;
                    }
                }
            }

            if (isGranted) {
                File file = new File(Utils.getAppSubDirectory(this, Constant.PICTURES),
                        System.currentTimeMillis() + ".jpg");
                picturePath = file.getPath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
            }
        }

        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            Utils.startCropImageActivity(this, mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_SELECT_CATEGORY && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra(Values.category_name);
            idCategories = data.getStringExtra(Values.category_id);
            Log.e("ID CATEGORIES", idCategories);
            edtProfesstionnalField.setText(name);
        }
        if (requestCode == KEY_GET_LANGUAGE) {
            if (data != null && data.hasExtra(Values.language_written))
                tvLanguage.setText(data.getStringExtra(Values.language_written));
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_CHOOSER_REQUEST_CODE:
                    try {
                        Uri imageUri = CropImage.getPickImageResultUri(this, data, "");
                        // For API >= 23 we need to check specifically that we have permissions to read external storage.
                        if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                            mCropImageUri = imageUri;
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        } else {
                            Utils.startCropImageActivity(this, imageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    break;
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        String uri = result.getUri().toString();
                        imgAvatar.setImageURI(result.getUri());
                        PreferenceUtil.getInstanceEditor(EditProfileActivity.this).putString(Values.avatar, uri);
                        PreferenceUtil.getInstanceEditor(EditProfileActivity.this).commit();
                        updateUserInfo();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, "Upload failed: " + result.getError(), Toast.LENGTH_LONG).show();
                    }
                    break;

                case REQUEST_CAPTURE_PHOTO: {

                    try {
                        Uri imageUri = Uri.parse("file://" + picturePath);
                        if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                            mCropImageUri = imageUri;
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        } else {
                            Utils.startCropImageActivity(this, imageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
