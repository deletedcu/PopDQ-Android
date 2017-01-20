package com.popdq.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.connection.CategoryUtils;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.PushNotificationUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.fragment.FeedFragment;
import com.popdq.app.model.Credit;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.textview.TextViewNormal;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import io.fabric.sdk.android.Fabric;

public class HomeActivity extends BaseSearchActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private LinearLayout creditleftlayout;
    private User user;
    private TextViewNormal tvEarnings, tvDeposits;
    private ChangeCreditBroadCast changeCreditBroadCast;
    private Gson gson;
    CircleImageView imgAvatar;
    private TextViewNormal tvCountNoti;
    private BroadcastReceiver broadcastReceiverUpdateCountUnread;
    private MainActivity mainActivity;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        FirebaseCrash.report(new Exception("PopDQ crash"));
        Branch.getAutoInstance(this);

        contentLoaded();
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        String userS = PreferenceUtil.getInstancePreference(this).getString(Values.user, "");
        gson = new Gson();
        try {
            user = gson.fromJson(userS, User.class);
        } catch (Exception e) {

        }

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
//            if (user.getEmail() != null || user.getEmail().equals("")) {
//                if (user.getActivate() == 0) {
//                    startActivity(new Intent(this, EmailVerifiActivity.class));
//                    finish();
//                    return;
//                }
//            }
            setContentView(R.layout.activity_home);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            tvCountNoti = (TextViewNormal) findViewById(R.id.tvCountNoti);
            tvEarnings = (TextViewNormal) findViewById(R.id.tvEarnings);
            tvDeposits = (TextViewNormal) findViewById(R.id.tvDeposits);
            imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
            imgAvatar.setOnClickListener(this);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            creditleftlayout = (LinearLayout) findViewById(R.id.creditleftlayout);
            creditleftlayout.setOnClickListener(this);
            if ((user.getFirstname() + user.getLastname() + user.getUsername()).equalsIgnoreCase("") || user.getUsername() == null || user.getUsername().equals("")) {
                Intent intent = new Intent(this, InsertProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }
            String a = Values.BASE_URL_AVATAR + user.getAvatar();
            Glide.with(this).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatar);
        }

        tvDeposits.setText("$" + user.getCredit() + "");
        tvEarnings.setText("$" + user.getCredit_earnings() + "");

        ((LinearLayout) findViewById(R.id.btnProfile)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnMyQuestion)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnMyAnswer)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnMyPreView)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnBuyCredit)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnTransaction)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnShare)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnMyInterest)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnFavorite)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnWithDraw)).setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnClose)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnLogOut)).setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.btnNotification)).setOnClickListener(this);
//        searchQuestion(true, token, "", "", 0, LOAD_ITEM_EACH);

        FeedFragment questionFragment = new FeedFragment();
        questionFragment.setUrl(Values.URL_QUESTION_SEARCH);
//        questionFragment.isHome = true;

        getSupportFragmentManager().beginTransaction().add(R.id.content, questionFragment).commit();
        try {
            changeCreditBroadCast = new ChangeCreditBroadCast();
            IntentFilter intentFilter = new IntentFilter("change_user");
            LocalBroadcastManager.getInstance(this).registerReceiver(changeCreditBroadCast, intentFilter);
        } catch (Exception e) {

        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        getListCreditCanBuyToLocal();
        getListCategories();
        getCountUnread();

        broadcastReceiverUpdateCountUnread = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getCountUnread();
            }
        };
        IntentFilter intentFilter = new IntentFilter("update_unread");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverUpdateCountUnread, intentFilter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void getCountUnread() {
        NotificationUtil.getCountUnread(this, PreferenceUtil.getToken(this), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                int count = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    count = jsonObject.getInt("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (count <= 0) {
                    tvCountNoti.setVisibility(View.GONE);
                } else {
                    tvCountNoti.setText(count + "");
                    tvCountNoti.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public void getListCreditCanBuyToLocal() {
        // get list credit can buy
        CreditUtils.getListCanBuy(this, PreferenceUtil.getToken(this), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    List credits = VolleyUtils.getListModelFromRespone(response, Credit.class);
                    PreferenceUtil.getInstanceEditor(HomeActivity.this).putString(Values.LIST_CREDIT_CAN_BUY, new Gson().toJson(credits));
                    PreferenceUtil.getInstanceEditor(HomeActivity.this).commit();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getListCategories() {
        CategoryUtils.getListCategories(this, PreferenceUtil.getToken(this), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                PreferenceUtil.getInstanceEditor(HomeActivity.this).putString(Values.category_respone, response);
                PreferenceUtil.getInstanceEditor(HomeActivity.this).commit();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(changeCreditBroadCast);
        } catch (Exception e) {

        }

        try {
            unregisterReceiver(broadcastReceiverUpdateCountUnread);
        } catch (Exception e) {

        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfile:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, UserProfileActivity.class));

                break;
            case R.id.btnMyQuestion:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, MyQuestionActivity.class));
                break;

            case R.id.btnMyAnswer:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, MyAnswerActivity.class));
                break;
            case R.id.btnMyPreView:
                startActivity(new Intent(this, MyPreviewActivity.class));
                mDrawerLayout.closeDrawers();

                break;
            case R.id.btnBuyCredit:
                startActivity(new Intent(this, BuyCreditActivityBackup.class));
                mDrawerLayout.closeDrawers();

                break;
            case R.id.btnTransaction:
                startActivity(new Intent(this, TransactionViewActivity.class));
                mDrawerLayout.closeDrawers();
                break;
            case R.id.btnShare:
                mDrawerLayout.closeDrawers();

//
//                LinkProperties linkProperties = new LinkProperties()
//                        .addControlParameter("$deeplink_path", "user_profile/"+user.getId())
//                        .setAlias(user.getUsername())
//                        .setFeature("sharing");
//
//
//                branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
//                    @Override
//                    public void onLinkCreate(String url, BranchError error) {
//                        if (error == null) {
//                            Log.i("MyApp", "got my Branch link to share: " + url);
//                            Utils.shareIntent(getApplicationContext(),String.valueOf(url));
//                        }
//                    }
//                });


                Utils.shareIntent(getApplicationContext());

                break;
            case R.id.btnMyInterest:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, MyInterestActivity.class));
                break;
            case R.id.btnFavorite:
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(this, FavoriteListActivity.class);
                intent.putExtra(Values.user_id, PreferenceUtil.getUserId(this) + "");
                startActivity(intent);
                break;
            case R.id.btnWithDraw:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, WithdrawActivity.class));
                break;
            case R.id.btnMenu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.btnClose:
                mDrawerLayout.closeDrawers();
                break;
            case R.id.imgAvatar:
                mDrawerLayout.closeDrawers();

                startActivity(new Intent(this, EditProfileActivity.class));
                break;
            case R.id.btnLogOut:
                final DialogBase dialogBase = new DialogBase(this);
                dialogBase.setTitle(getString(R.string.log_out));
                dialogBase.setMessage(getString(R.string.do_you_want_logout));
                dialogBase.getBtnOk().setText("OK");
                dialogBase.setOnClickOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tokenPush = PreferenceUtil.getInstancePreference(HomeActivity.this).getString(Values.token_push, "");
                        PushNotificationUtil.unRegisterPush(HomeActivity.this, PreferenceUtil.getToken(HomeActivity.this), tokenPush, getPackageName(), new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String response, Result result) {
                                Log.e(TAG, "unregisterpush: " + response);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                        try {
                            if (user.getFacebook_id() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                        PreferenceUtil.getInstanceEditor(HomeActivity.this).putString(Values.TOKEN, "");
                        PreferenceUtil.getInstanceEditor(HomeActivity.this).putString(Values.user, "");
                        PreferenceUtil.getInstanceEditor(HomeActivity.this).commit();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                        return;
                    }
                });
                dialogBase.setOnClickCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogBase != null && dialogBase.isShowing())
                            dialogBase.dismiss();
                    }
                });
                try {
                    dialogBase.show();

                } catch (Exception e) {

                }

                break;
            case R.id.creditleftlayout:

                break;
            case R.id.btnNotification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
        }
    }

    BranchUniversalObject branchUniversalObject;

    // This would be your own method where you've loaded the content for this page
    void contentLoaded() {
        // Initialize a Branch Universal Object for the page the user is viewing
        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("user_profile/"+user.getId())
                .setTitle("Profile check")

                .setContentDescription("Check out this awesome piece of content")
//                .setContentImageUrl("https://example.com/mycontent-12345.png")
//                .addContentMetadata(Values.experts_id, String.valueOf(145))
                .addContentMetadata(Values.experts_id, String.valueOf(user.getId()));

        // Trigger a view on the content for analytics tracking
        branchUniversalObject.registerView();

        // List on Google App Indexing
        branchUniversalObject.listOnGoogleSearch(this);


    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.azstack.quickanswer.ui/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);


        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null && branchUniversalObject != null) {
                    // This code will execute when your app is opened from a Branch deep link, which
                    // means that you can route to a custom activity depending on what they clicked.
                    // In this example, we'll just print out the data from the link that was clicked.

                    Log.i("BranchTestBed", "referring Branch Universal Object: " + branchUniversalObject.toString());

//                    // check if the item is contained in the metadata
//                    if (branchUniversalObject.getMetadata().containsKey(Values.experts_id)) {
//                        Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
//                        i.putExtra(Values.experts_id, Long.valueOf(branchUniversalObject.getMetadata().get(Values.experts_id)));
//                        startActivity(i);
//                    }
                }
            }
        }, this.getIntent().getData(), this);

    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.azstack.quickanswer.ui/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    class ChangeCreditBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String userS = PreferenceUtil.getInstancePreference(HomeActivity.this).getString(Values.user, "");
                user = gson.fromJson(userS, User.class);
            } catch (Exception e) {

            }
            String a = Values.BASE_URL_AVATAR + user.getAvatar();
            try {
                Glide.clear(imgAvatar);
                Glide.with(HomeActivity.this).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatar);
            } catch (Exception e) {

            }
            tvEarnings.setText("$" + user.getCredit_earnings() + "");
            tvDeposits.setText("$" + user.getCredit() + "");
        }
    }


}
