package com.popdq.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.libs.Constant;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

public class InsertProfileActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final int KEY_GET_IMAGE = 754;
    private static final int ACTION_CROP_PICTURE = 1;
    private static final int REQUEST_CAPTURE_PHOTO = 2;
    public static final int REQUEST_PERMISSION_CAMERA = 3;
    private ScrollView scrollView;
    private String userName, firstName, lastName;
    private CheckBox status_anonymous;
    private int anonymous;
    private String urlAvartar;

    private CircleImageView imgAvatar;
    private EditText edtUserName;
    private EditText edtLastName, edtFirstName;
    private User user;
    private String picturePath = "";
    private Uri mCropImageUri;

//    private LinearLayout layoutBottom;
//    private AutoCompleteTextView tvLanguageWritten, tvLanguageSpoken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userS = PreferenceUtil.getInstancePreference(this).getString(Values.user, "");
        try {
            user = new Gson().fromJson(userS, User.class);
            userName = user.getUsername();
            anonymous = user.getStatus_anonymous();
        } catch (Exception e) {

        }

        setContentView(R.layout.activity_insert_profile);

        ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.profile));
        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(this);

//        layoutBottom = (LinearLayout) findViewById(R.id.bottom);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        status_anonymous = (CheckBox) findViewById(R.id.status_anonymous);

        vStub = (ViewStub) findViewById(R.id.v_stub);


        edtFirstName.setOnFocusChangeListener(this);
        edtFirstName.setOnFocusChangeListener(this);
        edtLastName.setOnFocusChangeListener(this);
        Utils.setEditTextNotSpace(edtUserName);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Utils.setBottomButton(this, getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        if (user == null) {
            return;
        } else {
            Glide.with(this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(imgAvatar);
            edtUserName.setText(user.getFirstname());
            edtFirstName.setText(user.getFirstname());
            edtLastName.setText(user.getLastname());
            status_anonymous.setChecked(user.getStatus_anonymous() == 0 ? true : false);
        }

    }


    public void updateUserInfo() {
        userName = edtUserName.getText().toString();
        firstName = edtFirstName.getText().toString();
        lastName = edtLastName.getText().toString();

        if (userName.length() <= 0) {
            Toast.makeText(InsertProfileActivity.this, getString(R.string.please_input_user_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName.length() < 5) {
            Toast.makeText(InsertProfileActivity.this, getString(R.string.min_values_username), Toast.LENGTH_SHORT).show();
            return;
        }

        anonymous = status_anonymous.isChecked() ? 0 : 1;
        final VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_UPDATE_INFO_USER);
        volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getInstancePreference(this).getString(Values.TOKEN, ""));
        volleyUtils.addParam(Values.username, userName);
        volleyUtils.addParam(Values.firstname, firstName);
        volleyUtils.addParam(Values.lastname, lastName);
        volleyUtils.addParam(Values.status_anonymous, anonymous + "");
        String uriAvatar = PreferenceUtil.getInstancePreference(this).getString(Values.avatar, "");
        Uri uri = Uri.parse(uriAvatar);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), uri);
            int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
            ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
            bitmap = (Utils.getCroppedBitmap(bitmap));
            String avatar = Utils.getBase64Image(Bitmap.createScaledBitmap(bitmap, Values.SIZE_AVATAR, Values.SIZE_AVATAR, true));
            volleyUtils.addParam(Values.avatar, avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                Log.e("UPLOAD S", reponse);
                if (VolleyUtils.requestSusscess(reponse)) {
                    try {
                        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(InsertProfileActivity.this, Values.PROJECT_TOKEN_MIXPANEL);
                        MixpanelAPI.getInstance(InsertProfileActivity.this, Values.PROJECT_TOKEN_MIXPANEL).alias(userName, mixpanelAPI.getDistinctId());
                    } catch (Exception e) {

                    }
                    volleyUtils.getUserAndPushPreference(InsertProfileActivity.this, reponse);
                    Toast.makeText(InsertProfileActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(InsertProfileActivity.this, MyInterestActivity.class);
                    intent.putExtra("isNew", true);
                    startActivity(intent);
                    finish();
                } else {
                    if (result.getCode() == 18) {
                        Toast.makeText(InsertProfileActivity.this, getString(R.string.unique_username), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InsertProfileActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    Uri destination;

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

    private View stubView;
    private ViewStub vStub;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.v_cancel:
                vStub.setVisibility(View.GONE);
                break;
        }

    }

    public void dialogSelectCropOrNon(final Intent data) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] listActionCrop = getResources().getStringArray(R.array.action_crop);
        builder.setItems(listActionCrop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        File folderImageEvent = new File(Environment.getExternalStorageDirectory().getPath() + "/LoveDays/Avatar");
                        if (!folderImageEvent.exists()) {
                            folderImageEvent.mkdirs();
                        }
                        destination = Uri.fromFile(new File(folderImageEvent, System.currentTimeMillis() + ".jpg"));
//                        Crop.of(data.getData(), destination).asSquare().start(InsertProfileActivity.this);
                        break;
                    case 1:

                }
            }
        });
        builder.show();

    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    boolean isOpened = false;


    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
//                    String path = data.getExtras().getString("path");
//                    Bitmap bitmap = BitmapUtils.rotate(this, path);
//                    if (bitmap == null) {
//                        return;
//                    }
//                    int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
//                    ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
//                    final Bitmap finalBitmap = bitmap;
//
//                    imgAvatar.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Glide.clear(imgAvatar);
//                            imgAvatar.setImageBitmap(finalBitmap);
//                        }
//                    }, 100);
//                    String realPath = "";
//                    try {
//                        realPath = RealPathUtil.getRealPathAll(InsertProfileActivity.this, data.getData());
//                    } catch (Exception e) {
//
//                    }
//                    if (realPath == null || realPath.equals("") && data != null && data.getData() != null) {
//                        realPath = data.getData().toString();
//                    }
//                    PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).putString(Values.avatar, path);
//                    PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).commit();
//                    updateUserInfo();
//                    break;
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        String uri = result.getUri().toString();
                        Glide.clear(imgAvatar);
                        imgAvatar.setImageURI(result.getUri());
                        PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).putString(Values.avatar, uri);
                        PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).commit();
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


//        if (requestCode == KEY_GET_IMAGE && resultCode == Activity.RESULT_OK) {
//
//
//
//            try {
//                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(InsertProfileActivity.this.getApplicationContext().getContentResolver(), data.getData());
//                int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
//                ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
//                imgAvatar.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.clear(imgAvatar);
//                        imgAvatar.setImageBitmap(Utils.getCroppedBitmap(bitmap));
//                    }
//                }, 100);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String realPath = "";
//            try {
//                realPath = RealPathUtil.getRealPathAll(InsertProfileActivity.this, data.getData());
//            } catch (Exception e) {
//
//            }
//            if (realPath == null || realPath.equals("") && data != null && data.getData() != null) {
//                realPath = data.getData().toString();
//            }
//            PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).putString(Values.avatar, realPath);
//            PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).commit();
//
//        }

//        if (requestCode == 6709 && resultCode == Activity.RESULT_OK) {
//            try {
//                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), destination);
//                int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
//                ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
//                imgAvatar.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.clear(imgAvatar);
//                        imgAvatar.setImageBitmap(Utils.getCroppedBitmap(bitmap));
//                    }
//                }, 100);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).putString(Values.avatar, destination.toString());
//            PreferenceUtil.getInstanceEditor(InsertProfileActivity.this).commit();
//
//            int i = 0;
//            i++;
//        }

            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
//                Utils.hideKeyBoard(this);

                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        Log.d("focus", "touchevent");
                        v.clearFocus();
//                        layoutBottom.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                layoutBottom.setVisibility(View.VISIBLE);
//                            }
//                        }, 200);
                        Utils.hideKeyBoard(this);
                    } else {


//                        layoutBottom.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                layoutBottom.setVisibility(View.GONE);
//                            }
//                        });
                    }
                }
            }
        } catch (Exception e) {

        }
        return super.dispatchTouchEvent(event);
    }


}
