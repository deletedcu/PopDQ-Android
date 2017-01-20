package com.popdq.app.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.FileUtil;
import com.popdq.app.model.FileModel;
import com.popdq.app.util.RealPathUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.view.textview.TextViewNormal;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProvideAnswerByTextActivity extends BaseProvideAnswerActivity implements View.OnClickListener {
    private static final int KEY_GET_IMAGE = 24;

    private EditText edtContent;
    private LinearLayout horizontalScoll;
    private ImageView btnAddImage;
    //    private RelativeLayout btnAskAnonymous;
//    private TextViewNormal tvAnonymous;
    private HorizontalScrollView scrollView;
    private TextViewNormal tvCountImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_text_answer);
        getDataFromIntent();
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtContent.setTypeface(MyApplication.getInstanceTypeNormal(this));

        btnAddImage = (ImageView) findViewById(R.id.btnAddImage);
        horizontalScoll = (LinearLayout) findViewById(R.id.horizontalScoll);
        btnAddImage.setOnClickListener(this);
//        btnAskAnonymous = (RelativeLayout) findViewById(R.id.btnAskAnonymous);
//        btnAskAnonymous.setOnClickListener(this);
//        tvAnonymous = (TextViewNormal) findViewById(R.id.tvAnonymous);
        tvCountImages = (TextViewNormal) findViewById(R.id.tvCountImages);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);
        setTitleBarText(getString(R.string.provide_text_answer));
        Utils.setBottomButton(this, getString(R.string.submit_answer), this);
        ((TextViewNormal) findViewById(R.id.tvTitle)).setText(title);

    }

    @Override
    protected boolean hasContent() {
        if (edtContent.getText().toString().length() > 0 || (fileModels != null && fileModels.size() > 0)) {
            return true;
        } else return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Utils.checkPermission(this) && requestCode == Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            Utils.galleryIntentDefault(this, KEY_GET_IMAGE);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnAddImage:
                if (Utils.checkPermission(this)) {
                    Utils.galleryIntentDefault(this, KEY_GET_IMAGE);
                }
                break;
            case R.id.btnBottom:

                submitAnswer(edtContent.getText().toString());
                break;
//            case R.id.btnAskAnonymous:
//                final String[] anonymous = new String[]{"Yes", "No"};
//                showDialogchoice(getString(R.string.anonymous_mode), anonymous, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        tvAnonymous.setText(anonymous[i]);
//                    }
//                });
//                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_GET_IMAGE && resultCode == Activity.RESULT_OK) {
            addImages(data);
        }

    }

    //    public void addImages(Intent data) {
//        String realPath = "";
//        try {
//            realPath = RealPathUtil.getRealPathAll(this, data.getData());
//        } catch (Exception e) {
//
//        }
//        if (realPath == null || realPath.equals("") && data != null && data.getData() != null) {
//            realPath = data.getData().toString();
//        }
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
//            final CircleImageView image = new CircleImageView(this);
//            int size = (int) Utils.pxFromDp(this, 100);
//            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
//            image.setPadding(20, 0, 20, 0);
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvideAnswerByTextActivity.this);
//                    builder.setMessage(getString(R.string.do_you_want_delete_this_image));
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
////                            Toast.makeText(ProvideQuestionActivity.this, image.getTag().toString(), Toast.LENGTH_SHORT).show();
//                            horizontalScoll.removeView(image);
//                            fileModels.remove(image.getTag());
//                            tvCountImages.setText("(" + fileModels.size() + ")");
//
//                        }
//                    });
//                    builder.setNegativeButton("CANCEL", null);
//                    builder.show();
//
//
//                }
//            });
//            String newPathCompressed = FileUtil.getPathBitmapCompressed(bitmap);
//            String newPath = newPathCompressed != null ? newPathCompressed : realPath;
//            File file = new File(newPath);
//            FileModel fileModel = new FileModel(realPath, file.length(), 0);
//            fileModels.add(fileModel);
//            Glide.with(ProvideAnswerByTextActivity.this).load(newPath).dontAnimate().into(image);
//            image.setTag(realPath);
//            horizontalScoll.addView(image, fileModels.size() - 1);
//            tvCountImages.setText("(" + fileModels.size() + ")");
//
//            scrollView.post(new Runnable() {
//                public void run() {
//                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvideAnswerByTextActivity.this);
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

            Glide.with(ProvideAnswerByTextActivity.this).load(newPath).dontAnimate().into(image);
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

    public void showDialogchoice(String title, String[] list, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(list, onClickListener);
        builder.setTitle(title);
        builder.show();
    }


}
