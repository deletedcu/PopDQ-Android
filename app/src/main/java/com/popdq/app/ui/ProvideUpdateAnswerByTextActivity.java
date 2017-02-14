package com.popdq.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.LruCache;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.AnswerUtil;
import com.popdq.app.connection.FileUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Answer;
import com.popdq.app.model.FileModel;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.model.VolleySingleton;
import com.popdq.app.util.DateUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.RealPathUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProvideUpdateAnswerByTextActivity extends BaseProvideAnswerActivity implements View.OnClickListener {
    private static final int KEY_GET_IMAGE = 24;

    private TextView edtContentOld;
    private EditText edtContent;
    private LinearLayout horizontalScoll;
    private ImageView btnAddImage;
    private HorizontalScrollView scrollView;
    private TextViewNormal tvCountImages;


    Bitmap bitmap;
    String newPathCompressed;
    String newPath;
    String tite;
    File file;
    FileModel fileModel;

    private Answer answer;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_text_answer_update);
        getDataFromIntent();
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtContentOld = (TextView) findViewById(R.id.edtContentOld);
        edtContent.setTypeface(MyApplication.getInstanceTypeNormal(this));
        edtContentOld.setTypeface(MyApplication.getInstanceTypeNormal(this));

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
//        edtContentOld.setText("\""+answer_content+"\"");
        edtContent.setText(answer_content);
        ((TextViewNormal) findViewById(R.id.tvTitle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (FileModel fileModel : fileModels) {
                    Log.e("LOG FILE MODELS","{\"fileSize\":" + fileModel.getFileSize() + ",\"duration\":" + fileModel.getDuration() + "},");
                }
            }
        });


        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading attachments...");
        progressDialog.show();

//        getImages();
        try {
            GetXMLTask task = new GetXMLTask();
//        String[] stringArray = new String[myList.size()];
//        stringArray = myList.toArray(stringArray);


            String[] stringArray = myList.toArray(new String[0]);

            task.execute(stringArray);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        for (int j = 0; j<myList.size();j++){
////
//            Log.e("GETINTET",myList.get(j));
//
//
////            getfk(myList.get(j));
//        }
    }
//
    public void getfk(String url){

//        RequestQueue queue = Volley.newRequestQueue(this);
//        ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
//            @Override
//            public Bitmap getBitmap(String url) {
//                return null;
//            }
//
//            @Override
//            public void putBitmap(String url, Bitmap bitmap) {
//
//            }
//        });
//        imageLoader.get("http://s017.radikal.ru/i413/1209/e7/648aa22cb947.jpg", ImageLoader.getImageListener(mImageView, R.drawable.ic_launcher, R.drawable.ic_launcher));
//

        ImageLoader imageLoader = VolleySingleton.getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {

            public void onErrorResponse(VolleyError error) {
//                imageView.setImageResource(R.drawable.icon_error); // set an error image if the download fails
            }

            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
//                    imageView.setImageBitmap(response.getBitmap());
                        newPathCompressed = FileUtil.getPathBitmapCompressed(response.getBitmap());
                        file = new File(newPathCompressed != null ? newPathCompressed : null);

                        addupdateimage(newPathCompressed, file.length());

                }
            }
        });

//        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
//        ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//            public Bitmap getBitmap(String url) {
//                return mCache.get(url);
//            }
//        });
//
//        Bitmap bmp = mImageLoader.get("http://someurl.com/image.png");

    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }



    public class GetXMLTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(String... urls) {

            ArrayList<Bitmap> map = new ArrayList<Bitmap>();

            for (String url : urls) {

                map.add(getBitmapFromURL(url));
            }

            return map;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            progressDialog.dismiss();
            for (int i = 0;i<result.size();i++){
                newPathCompressed = FileUtil.getPathBitmapCompressed(result.get(i));
                assert (newPathCompressed != null ? newPathCompressed : null) != null;
                file = new File(newPathCompressed);
                addupdateimage(newPathCompressed, file.length());

            }

        }
    }

//    protected ArrayList<String> doInBackground(String... urls) {
//        ArrayList<String> result = new ArrayList<>();
//        for(int i = 0; i< urls.length; i++){
//            try {
//                URL url = new URL(urls[i]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(line).append("\n");
//                    }
//                    bufferedReader.close();
//                    result.add(stringBuilder.toString());
//                } finally {
//                    urlConnection.disconnect();
//                }
//
//            } catch (Exception e) {
//                Log.e("ERROR", e.getMessage(), e);
//                result.add(null);
//            }
//        }
//        return result;
//    }

    private void getImages() {
        AnswerUtil.search(this, PreferenceUtil.getToken(this), question_id, 0, 100, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                final List<Answer> answers = AnswerUtil.getAnswerFromJsonRespone(reponse);
                if (answers != null && answers.size() > 0) {
                    answer = answers.get(answers.size() - 1);

                    final Question.Attachments[] name = answer.getAttachments();

                    for(int i = 0; i < name.length; i++) {
//                        addImage(name[i].link, name[i].info.getFileSize());

//                        bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
//                        Bitmap asd = getBitmapFromURL(Values.BASE_URL_AVATAR+name[i].link);

                        Bitmap images;
                        //                            images = Glide.
//                                    with(ProvideUpdateAnswerByTextActivity.this).
//                                    load(Values.BASE_URL_AVATAR+name[i].link).
//                                    asBitmap().
//                                    into(new SimpleTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                            newPathCompressed = FileUtil.getPathBitmapCompressed(resource);
//                                        }
//                                    });

                        final int finalI = i;
                        Glide.with(ProvideUpdateAnswerByTextActivity.this)
                                .load(Values.BASE_URL_AVATAR+name[i].link)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        // you can do something with loaded bitmap here

                                        // .....

                                        newPathCompressed = FileUtil.getPathBitmapCompressed(resource);

                                        addupdateimage(newPathCompressed, name[finalI].info.getFileSize());
                                    }
                                });

                        //                        new DownloadImageTask("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png")
//                                .execute();

                    }



                }
            }

            @Override
            public void onError(String error) {

            }
        });


    }
//
//    private class DownloadImageTask extends AsyncTask <String, Void, Bitmap> {
//
//        protected Bitmap doInBackground(String... url) {	            // Executes on background thread
//            return loadImageFromNetwork( url[0] );                         // Calls onPostExecute with return results
//        }
//
//        protected void onPostExecute(Bitmap result) {                  // Executes on UI thread
//            imageView.setImageBitmap(result);
//        }
//    }
//
//    public Bitmap loadImageFromNetwork(final String s) {
//        Bitmap bmp = null;
//        try {
//            URL url = new URL(s);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            bmp = BitmapFactory.decodeStream(is);
//        } catch (Exception e) {
//            System.out.println("getImage failure:" + e);
//            e.printStackTrace();
//        }
//        return bmp;
//    }


//
//    MyAsync obj = new MyAsync(){
//
//        @Override
//        protected void onPostExecute(Bitmap bmp) {
//            super.onPostExecute(bmp);
//
//            bitmap = FileUtil.compressBitmap(bmp);
//            newPathCompressed = FileUtil.getPathBitmapCompressed(bitmap);
//
////                        newPath = newPathCompressed != null;
//
////                        file = new File(newPath);
//
//            fileModel = new FileModel(newPathCompressed, name[i].info.getFileSize(), 0);
//            fileModels.add(fileModel);
//
//        }
//    };
//
//    public class MyAsync extends AsyncTask<Void, Void, Bitmap>{
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//
//            try {
//                URL src;
//                URL url = new URL(src);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//
//        }
//    }

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

                submitUpdateAnswer(edtContent.getText().toString());
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
            bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
            bitmap = FileUtil.compressBitmap(bitmap);
            final CircleImageView image = new CircleImageView(this);
            int size = (int) Utils.pxFromDp(this, 100);
            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            image.setPadding(20, 0, 20, 0);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvideUpdateAnswerByTextActivity.this);
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
            newPathCompressed = FileUtil.getPathBitmapCompressed(bitmap);

            newPath = newPathCompressed != null ? newPathCompressed : realPath;

            file = new File(newPath);
            fileModel = new FileModel(newPath, file.length(), 0);


            Log.e("filemode", newPath + "/n" + file.length());
            fileModels.add(fileModel);

            Glide.with(ProvideUpdateAnswerByTextActivity.this).load(newPath).dontAnimate().into(image);
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




    public void addupdateimage(String path, long filesize){

//        Bitmap bit = getBitmapFromURL(Values.BASE_URL_AVATAR+path);
//        bitmap = bit;
//
//        bitmap = FileUtil.compressBitmap(bit);
        final CircleImageView image = new CircleImageView(this);
        int size = (int) Utils.pxFromDp(this, 100);
        image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        image.setPadding(20, 0, 20, 0);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProvideUpdateAnswerByTextActivity.this);
                builder.setMessage(getString(R.string.do_you_want_delete_this_image));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(ProvideQuestionActivity.this, image.getTag().toString(), Toast.LENGTH_SHORT).show();
                        horizontalScoll.removeView(image);
                        fileModels.remove(image.getTag());
                        Log.e("FilemodelTagRemove", String.valueOf(image));
                        tvCountImages.setText("(" + fileModels.size() + ")");
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.show();


            }
        });
//
//            file = new File(path);
//            fileModel = new FileModel(newPath, file.length(), 0);

//        newPathCompressed = FileUtil.getPathBitmapCompressed(bitmap);
//
        Log.e("filemode 2", path + "/n" + filesize);
        fileModel = new FileModel(path, filesize, 0);

        fileModels.add(fileModel);

        Glide.with(ProvideUpdateAnswerByTextActivity.this).load(path).dontAnimate().into(image);
        image.setTag(fileModel);
        Log.e("FilemodelTag", String.valueOf(fileModel));
        horizontalScoll.addView(image, fileModels.size() - 1);
        tvCountImages.setText("(" + fileModels.size() + ")");
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

    }

    public void showDialogchoice(String title, String[] list, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(list, onClickListener);
        builder.setTitle(title);
        builder.show();
    }


//
//
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//        private ProgressDialog mDialog;
//        private String url;
//
//        public DownloadImageTask(String url) {
//            this.url = url;
//        }
//
//        protected void onPreExecute() {
//
//            mDialog = ProgressDialog.show(getApplicationContext(),"Please wait...", "Retrieving data ...", true);
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
////            Bitmap mIcon11 = null;
//            try {
//                URL url = new URL(urldisplay);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (IOException e) {
//                // Log exception
//                return null;
//            }
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            //set image of your imageview
////            bmImage.setImageBitmap(result);
//            //close
//            mDialog.dismiss();
//        }
//    }


}
