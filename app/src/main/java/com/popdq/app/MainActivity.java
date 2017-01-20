package com.popdq.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.crash.FirebaseCrash;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.fragment.ExploreFragment;
import com.popdq.app.fragment.FeedFragment;
import com.popdq.app.fragment.MoreFragment;
import com.popdq.app.fragment.UserProfileFragment;
import com.popdq.app.model.Notification;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.service.AnalyticsApplication;
import com.popdq.app.ui.BaseSearchActivity;
import com.popdq.app.ui.InsertProfileActivity;
import com.popdq.app.ui.SelectCategoryHasSearchActivity;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.textview.TextViewNormal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseSearchActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Tracker mTracker;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.icon_tab_home,
            R.drawable.voice,
            R.drawable.video
    };
    private String titleName[];
    //    private TextViewNormal title;
    private User user;
//    private Toolbar toolbar;

    private ExploreFragment exploreFragment;
    private FeedFragment feedFragment;
    private MixpanelAPI mixpanel;
    private LinearLayout btnBottom;
    private BroadcastReceiver broadcastReceiverUpdateCountUnread = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra(Values.COUNT_NOTIFICATION, 0);
            setCountNotification(count);
        }
    };

    BranchUniversalObject branchUniversalObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Utils.getListCountry();

        FirebaseCrash.report(new Exception("PopDQ FirebaseCrash"));
//        Branch.getAutoInstance(this);
//        Branch branch = Branch.getInstance();

        mixpanel = MixpanelAPI.getInstance(this, Values.PROJECT_TOKEN_MIXPANEL);//e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

//      Hook up your share button to initiate sharing
//        branch.initSession(new Branch.BranchReferralInitListener(){
//            @Override
//            public void onInitFinished(JSONObject referringParams, BranchError error) {
//                if (error == null) {
//                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
//                    // params will be empty if no data found
//                    // ... insert custom logic here ...
//                } else {
//                    Log.i("MyApp", error.getMessage());
//                }
//            }
//        }, this.getIntent().getData(), this);

        user = User.getInstance(this);
        if (user == null || user.getUsername() == null || user.getUsername().equals("")) {
            User.userNull(this);
            finish();
            return;
        }
        if (user.getUsername() == null || user.getUsername().equals("")) {
            Intent intent = new Intent(this, InsertProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        if (user != null) {
//            if (user.getCategories() != null && user.getCategories().length <= 0) {
//                Intent intent = new Intent(this, MyInterestActivity.class);
//                startActivity(intent);
//            }

            mixpanel.getPeople().identify(mixpanel.getDistinctId() + "");
            exploreFragment = new ExploreFragment();
            setContentView(R.layout.activity_main);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
//            toolbar = (Toolbar) findViewById(R.id.toolbar);

            btnBottom = (LinearLayout) findViewById(R.id.btnBottom);
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setTitle("");
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tablayout);
//            title = (TextViewNormal) findViewById(R.id.title);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(5);
            setupTabIcons();
            getArrTitleName();
//            title.setText(titleName[0]);

//            findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    initiateSharing();
//                }
//            });
//            contentLoaded();


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
//                    title.setText(titleName[position]);
                    if (position != 0) {
                        feedFragment.setEmptySearchBar();
                    }
                    if (position != 1) {
                        exploreFragment.setEmptySearchBar();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            btnBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, SelectCategoryHasSearchActivity.class);
                    intent.putExtra(Values.find_user_category, true);
                    startActivity(intent);
                }
            });
            Log.e(TAG, "user token: " + user.getCurrent_token());

            CreditUtils.getListCreditCanBuyToLocal(this);
            CreditUtils.getListCategories(this);
        }
        checkNewVersion();

        IntentFilter intentFilter = new IntentFilter("update_unread_has_count");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverUpdateCountUnread, intentFilter);
    }

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


        LinkProperties linkProperties = new LinkProperties()
                .addControlParameter("$deeplink_path", "user_profile/"+user.getId())
                .setAlias(user.getUsername())
                .setFeature("sharing");

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                }
            }
        });



    }

    // This is the function to handle sharing when a user clicks the share button
    void initiateSharing() {
        // Create your link properties
        // More link properties available at https://dev.branch.io/getting-started/configuring-links/guide/#link-control-parameters
        LinkProperties linkProperties = new LinkProperties()
                .setFeature("sharing");

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                }
            }
        });

        // Customize the appearance of your share sheet
        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(this, "Check this out!", "Hey friend - I know you'll love this: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy link", "Link added to clipboard!")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        // Show the share sheet for the content you want the user to share. A link will be automatically created and put in the message.
        branchUniversalObject.showShareSheet(this, linkProperties, shareSheetStyle, new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() { }
            @Override
            public void onShareLinkDialogDismissed() { }
            @Override
            public void onChannelSelected(String channelName) { }
            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                // The link will be available in sharedLink
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
//        Branch branch = Branch.getInstance();
//        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
//            @Override
//            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
//                if (error == null && branchUniversalObject != null) {
//                    // This code will execute when your app is opened from a Branch deep link, which
//                    // means that you can route to a custom activity depending on what they clicked.
//                    // In this example, we'll just print out the data from the link that was clicked.
//
//                    Log.i("BranchTestBed", "referring Branch Universal Object: " + branchUniversalObject.toString());
//
//                    // check if the item is contained in the metadata
//                    if (branchUniversalObject.getMetadata().containsKey(Values.experts_id)) {
//                        Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
//                        i.putExtra(Values.experts_id, Long.valueOf(branchUniversalObject.getMetadata().get(Values.experts_id)));
//                        startActivity(i);
//                    }
//                }
//            }
//        }, this.getIntent().getData(), this);
    }



    public void hideBtnAsk(boolean hide) {
        if (hide) {
            btnBottom.setVisibility(View.GONE);
        } else {
            btnBottom.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverUpdateCountUnread);
        } catch (Exception e) {

        }
    }

    public void getArrTitleName() {
        titleName = new String[]{getString(R.string.home), getString(R.string.explore),
                getString(R.string.profile), getString(R.string.more)};
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        feedFragment = new FeedFragment();
        feedFragment.setUrl(Values.URL_QUESTION_SEARCH);
        adapter.addFragment(feedFragment, "");
        adapter.addFragment(exploreFragment, "");
        adapter.addFragment(new UserProfileFragment(), "");
        adapter.addFragment(new MoreFragment(), "");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(R.layout.tab_home);
        tabLayout.getTabAt(1).setCustomView(R.layout.tab_explore);
        tabLayout.getTabAt(2).setCustomView(R.layout.tab_profile);
        tabLayout.getTabAt(3).setCustomView(R.layout.tab_more);
    }

    public void setCountNotification(int count) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_more, null);
        TextViewNormal tvCountNoti = (TextViewNormal) view.findViewById(R.id.tvCountNoti);
        tvCountNoti.setSelected(true);
        if (count <= 0) {
            tvCountNoti.setVisibility(View.GONE);
        } else {
            tvCountNoti.setVisibility(View.VISIBLE);

            tvCountNoti.setText(count + "");
        }
//        TypedValue tv = new TypedValue();
//        int actionBarHeight = -1;
//        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//        }
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParam);
//        float dpMarginBottom = getResources().getDimension(R.dimen.magrin_tab_bottom);
        view.setPadding(0, 0, 0, 8);
//        view.setPadding(0,0,0, (int)Utils.pxFromDp(this, dpMarginBottom));

        View custom = tabLayout.getTabAt(3).getCustomView();

        if (custom != null) {
            ViewParent icon = custom.getParent();
            if (icon != this) {
                if (icon != null) {
                    // You wont get here
                    ((ViewGroup) icon).removeView(custom);
                }

//                ((ViewGroup) icon).addView(view);
            }
            tabLayout.getTabAt(3).setCustomView(view);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (exploreFragment != null)
            exploreFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==3){
            viewPager.setCurrentItem(0);
        }else {
            super.onBackPressed();

        }
    }
    public void nextPage(int tabNumber)
    {
        viewPager.setCurrentItem(tabNumber);
    }

    public void checkNewVersion() {
        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_GET_LAST_VERSION);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {

                if(result.getCode() == 2 ){
                    LoginUtil.logOut(getApplicationContext(),null);
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject newVersion = data.getJSONObject("newVersion");
                    int codeLast = newVersion.getInt("code");
                    String nameLast = newVersion.getString("name");


                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String currentVer = pInfo.versionName;
                    int currentCode = pInfo.versionCode;


                    Log.e("CURRENT CODE", String.valueOf(currentCode));
                    Log.e("LATEST CODE", String.valueOf(codeLast));

                    String version_last_not_show = PreferenceUtil.getInstancePreference(MainActivity.this).getString(Values.VERSION_UPDATE_NOT_SHOW, "");
                    if (currentCode < codeLast && !version_last_not_show.equals(nameLast)) {
                        showDialogUpdate(nameLast);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();
    }

    public void showDialogUpdate(final String lastVersionName) {
        final DialogBase dialogBase = new DialogBase(this);
        dialogBase.setTitle(getString(R.string.title_update));
        dialogBase.setMessage(String.format(getString(R.string.description_update), lastVersionName));
        dialogBase.getBtnCancel().setText(getString(R.string.btn_next_time));
        dialogBase.getBtnOk().setText(getString(R.string.ok));
        dialogBase.setOnClickOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.update(MainActivity.this);
            }
        });
        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtil.getInstanceEditor(MainActivity.this).putString(Values.VERSION_UPDATE_NOT_SHOW, lastVersionName);
                PreferenceUtil.getInstanceEditor(MainActivity.this).commit();
                dialogBase.dismiss();
            }
        });
        dialogBase.show();
    }


}
