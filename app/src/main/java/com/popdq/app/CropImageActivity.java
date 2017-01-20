//package com.popdq.app;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.View.OnClickListener;
//
//import com.popdq.libs.BitmapUtils;
//import com.popdq.libs.Constant;
//import com.popdq.app.util.Utils;
//import com.edmodo.cropper.CropImageView;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class CropImageActivity extends Activity {
//
//    private String filepath;
//    private CropImageView cropImageView;
//    private Handler handler = new Handler();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_crop_image);
//
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            filepath = extras.getString("photo_path");
//        }
//
//        cropImageView = (CropImageView) findViewById(R.id.im_crop);
//        Bitmap bitmap = BitmapUtils.rotate(this, filepath);
//        try {
//            File file = new File(Utils.getAppSubDirectory(this,
//                    Constant.PICTURES), System.currentTimeMillis() + ".jpg");
//            filepath = file.getPath();
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        cropImageView.setBitmap(bitmap);
//
//        View btnDone = findViewById(R.id.tv_done);
//        View btnCancel = findViewById(R.id.tv_cancel);
//        btnDone.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                String path = cropImageView.getCroppedImage();
//                Intent intent = new Intent();
//                intent.putExtra("path", path);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
//        btnCancel.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putString("photo_path", filepath);
//    }
//}
