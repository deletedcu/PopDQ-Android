package com.popdq.app.service;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.popdq.app.R;

/**
 * Created by Kyald on 12/15/2016.
 */

public class AnalyticsApplication extends Application {

    private Tracker tracker;


    synchronized public Tracker getDefaultTracker(){
        if(tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }


}
