package com.popdq.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.popdq.app.util.PreferenceUtil;
import com.popdq.mixpanelutil.MixPanelUtil;

import io.branch.referral.Branch;

/**
 * Created by Dang Luu on 10/6/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public static boolean STATUS_FORDGROUND;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MixPanelUtil.trackActivity(this, this.getClass());
        // Initialize Branch automatic session tracking
        Branch.getAutoInstance(this);

    }

    @Override
    protected void onResume() {
        PreferenceUtil.setStatusApp(this,true);
//        STATUS_FORDGROUND = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        PreferenceUtil.setStatusApp(this,false);
        super.onPause();
    }

    @Override
    protected void onStop() {
//        STATUS_FORDGROUND = false;
        super.onStop();
    }
}
