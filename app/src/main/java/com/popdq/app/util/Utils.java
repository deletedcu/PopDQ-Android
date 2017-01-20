package com.popdq.app.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.google.gson.Gson;
import com.popdq.app.AZGalleryActivity;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.User;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.ui.WebViewActivity;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;
import com.popdq.libs.Constant;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class Utils {

    public static void update(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
        context.startActivity(browserIntent);
    }

    public static ShareLinkContent getShareLinkContent(Context context, Uri content, Uri image, String title, String description) {
        ShareLinkContent shareLinkContent = null;
        String uriImage = "";
//        if (question != null && question.getAttachments().length > 0) {
//            uriImage = Values.BASE_URL_AVATAR + question.getAttachments()[0].link;
//        }
//        Uri uriImagee = Uri.parse(uriImage);

//        Uri ur1i = Uri.parse("android.resource://com.popdq.app/drawable/image_name");

        Uri uri = Uri.parse("http://api.popdq.com/api/static/img/logo_popdq.png");

//        if (uri != null && !uri.toString().equals("")) {
//            shareLinkContent = new ShareLinkContent.Builder()
//                    .setContentTitle(title)
//                    .setContentDescription(description)
//                    .setContentUrl(content)
//                    .setImageUrl(uri)
//                    .build();
//        } else {
            shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(content)
                    .setImageUrl(uri)
                    .build();
//        }
        return shareLinkContent;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void setEditTextNotSpace(final EditText editText) {
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilter.LengthFilter(20);

        InputFilter filter = new InputFilter() {

            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals("_") && !Character.toString(source.charAt(i)) .equals("-") && !Character.toString(source.charAt(i)) .equals(".")) {
                        return "";
                    }
                }
                return null;
            }
        };
        filterArray[1] = filter;


        editText.setFilters(filterArray);


    }

    public static void setLayoutCredit(float credit, LinearLayout layoutCredit, TextViewThin tvCredit) {
        if (credit == -1) {
            layoutCredit.setVisibility(View.GONE);
        } else if (credit == 0) {
            layoutCredit.setVisibility(View.VISIBLE);
            tvCredit.setText("FREE");
        } else {
            layoutCredit.setVisibility(View.VISIBLE);
            tvCredit.setText("$" + credit);
        }
    }

    public static void sendMail(Context context, String subject, String text) {
        User user = User.getInstance(context);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String myDeviceModel = android.os.Build.MODEL;
        String myDeviceVersion = Build.VERSION.RELEASE;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nDevice Model: Android");
        stringBuilder.append("\nDevice System: " + myDeviceVersion);
        stringBuilder.append("\nLogin Username/ID: " + user.getUsername() + "/" + user.getId());
        stringBuilder.append("\nLogin Email: " + user.getEmail() + "");
        stringBuilder.append("\nCountry/Language: " + user.getAddress() + "/" + user.getLanguage_answer());

        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        stringBuilder.append("\nApp Version: " + versionName);
        boolean enableLocation = Utils.isLocationEnabled(context);
        stringBuilder.append("\nLocation Services: " + (enableLocation ? "Enable" : "Disable"));
        text = text + stringBuilder.toString();
        Uri data = Uri.parse("mailto:" + context.getString(R.string.pop_dq_email) + "?subject=" + subject + "&body=" + text);
        intent.setData(data);
        context.startActivity(intent);

//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("text/plain");
//        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"popmeaquestion@popDQ.com"});
//        i.putExtra(Intent.EXTRA_SUBJECT, subject);
//        String myDeviceModel = android.os.Build.MODEL;
//        String myDeviceVersion = Build.VERSION.RELEASE;
//
//        i.putExtra(Intent.EXTRA_TEXT, text + "\n\nSend from: " + myDeviceModel + " ----- OS:" + myDeviceVersion);
//        try {
//            context.startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }
    }

    public static CharSequence[] getListCountry() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        CharSequence[] charSequences = countries.toArray(new CharSequence[countries.size()]);
//        CharSequence[] charSequences = new CharSequence[countries.size()];
//        for (int i=0; i<charSequences.length; i++) {
////            Log.e("COUNTRY", country);
//            countries.toArray()
//            charSequences[i] = countries.get(i);


        return charSequences;
    }

    public static void goToMyWebView(Context context, String title, String link) {
        Intent intent1 = new Intent(context, WebViewActivity.class);
        intent1.putExtra("url", link);
        intent1.putExtra("title", title);
        context.startActivity(intent1);
    }

    public static void startActivityExpertProfile(Context context, long experts_id) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(Values.experts_id, experts_id);
        context.startActivity(intent);
    }

    public static Bitmap getNormalOrientImage(Context context, final Uri selectedImageUri) {
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);


            ExifInterface exif = new ExifInterface(selectedImageUri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                default:
                    angle = 0;
                    break;
            }
            Matrix mat = new Matrix();
            if (angle == 0 && bm.getWidth() > bm.getHeight())
                mat.postRotate(90);
            else
                mat.postRotate(angle);

            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);

        } catch (IOException e) {
            Log.e("", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.e("", "-- OOM Error in setting image");
        }
        return null;
    }

    public static void setNotiIconShow(Context context, int method, ImageView icon) {
        icon.setVisibility(View.VISIBLE);
        int id = getMethodIcon(method);
//        Drawable drawable = ContextCompat.getDrawable(context, method);
        Glide.with(context).load(method).placeholder(id).dontAnimate().into(icon);

//        icon.setImageResource(getMethodIcon(method));
    }

    public static void checkStartActivityFromNotificationAndSendRead(Activity activity, VolleyUtils.OnRequestListenner onRequestListenner) {
        Intent intent = activity.getIntent();
        if (intent.hasExtra(Values.FROM_NOTIFICATION_BAR)) {
            long notificationId = intent.getLongExtra(Values.NOTIFICATION_ID, -1);
            NotificationUtil.readNotification(activity, PreferenceUtil.getToken(activity), notificationId, onRequestListenner);
        }
    }

    public static int getMethodIcon(int method) {
        switch (method) {
            case 1:
                return R.drawable.text;

            case 2:
                return R.drawable.voice;

            case 3:
                return R.drawable.video;
        }
        return R.drawable.text;
    }

    public static int getMethodIconWhite(int method) {
        switch (method) {
            case 1:
                return R.drawable.ic_text_white;

            case 2:
                return R.drawable.ic_audio_white;

            case 3:
                return R.drawable.ic_video_white;
        }
        return R.drawable.text;
    }

    public static void hideKeyBoard(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public static void showKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT > 23) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static String getAnonymousWord(String word) {
        if (word == null || word.length() < 2) {
            return word;
        }
        StringBuilder anonymousName = new StringBuilder();
        anonymousName.append(word.charAt(0));
        for (int i = 0; i < word.length() - 1; i++) {
            anonymousName.append("*");
        }
        return anonymousName.toString();
    }

    public static String getAnonymousName(String name) {
        String[] strings = name.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(getAnonymousWord(s) + " ");
        }
        return stringBuilder.toString();
    }

    public static void confirmExit(final Activity activity) {
        final DialogBase dialogBase = new DialogBase(activity);
        dialogBase.setTitle(activity.getString(R.string.notice));
        dialogBase.setMessage(activity.getString(R.string.do_you_want_to_exit));
        dialogBase.setTextOk("OK");
        dialogBase.setOnClickOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
            }
        });
        dialogBase.show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static User getUser(Context context) throws Exception {
        String userS = PreferenceUtil.getInstancePreference(context).getString(Values.user, "");


        return new Gson().fromJson(userS, User.class);

    }

    public static String getDurationFromLong(long dur) {
        String result;
        String seconds = String.valueOf((dur % 60000) / 1000);
        Log.v("seconds", seconds);
        String minutes = String.valueOf(dur / 60000);
        String out = minutes + ":" + seconds;
        if (seconds.length() == 1) {
            result = "0" + minutes + ":0" + seconds;
        } else {
            result = "0" + minutes + ":" + seconds;
        }
        Log.v("minutes", minutes);
        return result;
    }

    public static void showDialogShare(final Activity activity, final View.OnClickListener okClick) {
        // hide

        final DialogBase dialogBase = new DialogBase(activity);
        dialogBase.setTitle(activity.getString(R.string.notice));
        dialogBase.setMessage(activity.getString(R.string.share_message));
        dialogBase.setTextOk("YES");
        dialogBase.setOnClickOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (okClick != null) {
                    okClick.onClick(view);
                }
                dialogBase.dismiss();
                activity.finish();
            }
        });
        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
                activity.finish();
            }
        });
        dialogBase.show();
    }



//    public static void shareIntent(Context context,String uname) {
//
//        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
////        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "onelink.to/3h8uxk");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uname);
//        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
//
//    }

    public static void shareIntent(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "onelink.to/3h8uxk");
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }




    public static void openLink(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void setActionBar(final Activity activity, String text, int icon) {
        ((RelativeLayout) activity.findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        ((TextViewNormal) activity.findViewById(R.id.title)).setText(text);
        ((ImageView) activity.findViewById(R.id.iconNavigation)).setImageResource(icon);
    }

    public static void setBottomButton(Activity activity, String text, View.OnClickListener onClickListener) {
        Button button = ((Button) activity.findViewById(R.id.btnBottom));
        button.setOnClickListener(onClickListener);
        button.setText(text);
        button.setTypeface(MyApplication.getInstanceTypeNormal(activity));

    }

    public static void setActionEnterSearch(final Activity context, EditText editText) {
        editText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            hideKeyBoard(context);
                            return true;
                        case KeyEvent.KEYCODE_ENTER:
                            hideKeyBoard(context);
                            return true;
                        default:
                            hideKeyBoard(context);
//                            break;
                    }
                }
                return false;
            }
        });
    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void galleryIntent(FragmentActivity activity, int requestKey) {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
//        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), requestKey);
        Intent intent = new Intent(activity, AZGalleryActivity.class);
        activity.startActivityForResult(intent, requestKey);
    }

    public static void galleryIntentDefault(FragmentActivity activity, int requestKey) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), requestKey);
//        Intent intent = new Intent(activity, AZGalleryActivity.class);
//        activity.startActivityForResult(intent, requestKey);
    }

    public static void galleryIntentGallery(FragmentActivity activity, int requestKey) {
        Intent photoIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoIntent.setType("image/*");
        activity.startActivityForResult(photoIntent, requestKey);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static String getBase64Image(Bitmap bmp) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        String encode = Base64.encodeToString(byte_arr, 0);

        return encode;


    }

    /**
     * Get picture dir
     */
    public static File getAppSubDirectory(Context context, String subName) {
        File picDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            picDir = new File(getAppDirectory(context), subName);
            if (!picDir.exists()) {
                if (!picDir.mkdirs()) {
                    return context.getCacheDir();
                }
                if (!subName.equals(Constant.PICTURES)) {
                    File noMediaFile = new File(picDir, ".nomedia");
                    try {
                        noMediaFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (picDir == null) {
            picDir = context.getCacheDir();
        }
        return picDir;
    }

    /**
     * Get app directory
     */
    public static File getAppDirectory(Context context) {
        File picDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            picDir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
            if (!picDir.exists()) {
                if (!picDir.mkdirs()) {
                    return context.getCacheDir();
                }
            }
        }
        if (picDir == null) {
            picDir = context.getCacheDir();
        }
        return picDir;
    }

    /**
     * Start crop image activity for the given image.
     */
    public static void startCropImageActivity(FragmentActivity activity, Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(activity);
    }

}
