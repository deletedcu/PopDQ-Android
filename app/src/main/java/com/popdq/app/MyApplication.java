package com.popdq.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.popdq.app.model.User;
import com.popdq.app.util.FontUtils;
import com.popdq.app.util.Utils;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class MyApplication extends Application {
    public static User user;

    private Tracker tracker;


    synchronized public Tracker getDefaultTracker(){
        if(tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;

    private static MyApplication mInstance;

    private static Typeface TYPEFACE_NORMAL;
    private static Typeface TYPEFACE_BOLD;
    private static Typeface TYPEFACE_THIN;
    public static CharSequence[] listCountries;

    public static CharSequence[] getInstanceListCountries() {
        if (listCountries == null ) {
            listCountries = Utils.getListCountry();
        }
        return listCountries;
    }

    public static Typeface getInstanceTypeNormal(Context context) {
        if (TYPEFACE_NORMAL == null) {
            TYPEFACE_NORMAL = FontUtils.getTypefaceNormal(context);
        }
        return TYPEFACE_NORMAL;
    }

    public static Typeface getInstanceTypeBold(Context context) {
        if (TYPEFACE_BOLD == null) {
            TYPEFACE_BOLD = FontUtils.getTypefaceBold(context);
        }
        return TYPEFACE_BOLD;
    }

    public static Typeface getInstanceTypeThin(Context context) {
        if (TYPEFACE_THIN == null) {
            TYPEFACE_THIN = FontUtils.getTypefaceThin(context);
        }
        return TYPEFACE_THIN;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Branch.getAutoInstance(this);


    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
