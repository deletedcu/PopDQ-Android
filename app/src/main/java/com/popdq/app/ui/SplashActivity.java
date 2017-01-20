package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.popdq.app.MainActivity;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.popdq.mixpanelutil.MixPanelUtil;

import org.json.JSONObject;

import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = null;

        try {
            user = Utils.getUser(this);
            token = user.getCurrent_token();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null || user.getUsername() == null || user.getUsername().equals("")) {
            User.userNull(this);
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
//        if (checkPlayServices()) {
////             Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }


        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }



    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
