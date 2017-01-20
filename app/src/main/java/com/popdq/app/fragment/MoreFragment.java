package com.popdq.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.connection.LoginUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.BuyCreditActivityBackup;
import com.popdq.app.ui.EditProfileActivity;
import com.popdq.app.ui.FavoriteListActivity;
import com.popdq.app.ui.LoginActivity;
import com.popdq.app.ui.MyAnswerActivity;
import com.popdq.app.ui.MyInterestActivity;
import com.popdq.app.ui.MyPreviewActivity;
import com.popdq.app.ui.MyQuestionActivity;
import com.popdq.app.ui.NotificationActivity;
import com.popdq.app.ui.TransactionViewActivity;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.ui.WithdrawActivity;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.ItemNavigationMenu;
import com.popdq.app.view.textview.TextViewNormal;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by Dang Luu on 9/15/2016.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {
    private User user;
    private static final String TAG = "MoreFragment";
    private CircleImageView imgAvatar;
    private TextViewNormal tvEarnings, tvDeposits;
    private TextViewNormal tvCountNoti;
    private RelativeLayout btnNotification;
    private String requestIdToken = "698177898809-pj0vpko7tp65pvcsi904npeogshnqav0.apps.googleusercontent.com";
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance(getActivity());
//        contentLoaded();
//
//        if (user != null && user.getUsername() == null) {
//
////            VolleyUtils.forcelogout(getContext());
//
//        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(requestIdToken)
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext() /* Context */)
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_more_fragment, container, false);
//        ((LinearLayout) view.findViewById(R.id.btnProfile)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnMyQuestion)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnMyAnswer)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnMyPreView)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnBuyCredit)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnTransaction)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnShare)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnMyInterest)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnFavorite)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnWithDraw)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnHowWork)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnContact)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnAboutPopDQ)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.btnJoinExpert)).setOnClickListener(this);
        ((ItemNavigationMenu) view.findViewById(R.id.btnAboutPopDQ)).setText(getString(R.string.about_popdq).toUpperCase());
        ((LinearLayout) view.findViewById(R.id.btnLogOut)).setOnClickListener(this);
        btnNotification = ((RelativeLayout) view.findViewById(R.id.btnNotification));
        tvCountNoti = (TextViewNormal) view.findViewById(R.id.tvCountNoti);
        tvEarnings = (TextViewNormal) view.findViewById(R.id.tvEarnings);
        tvDeposits = (TextViewNormal) view.findViewById(R.id.tvDeposits);

        tvEarnings.setOnClickListener(this);
        tvDeposits.setOnClickListener(this);
        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        setProfile();
        getCountUnread();
        IntentFilter intentFilter = new IntentFilter("update_unread");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiverUpdateCountUnread, intentFilter);

        IntentFilter intentFilter2 = new IntentFilter("change_user_more_tab");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(changeCreditBroadCast, intentFilter2);

        return view;
    }

    private BroadcastReceiver changeCreditBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setProfile();
        }
    };
    private BroadcastReceiver broadcastReceiverUpdateCountUnread = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCountUnread();
        }
    };

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
        branchUniversalObject.listOnGoogleSearch(getContext());


    }

    public void setProfile() {
        User user = User.getInstance(getActivity());

//        if (user != null && user.getCredit_earnings() == 0.0) {
//
//        }

        String a = null;
        if (user != null) {
            a = Values.BASE_URL_AVATAR + String.valueOf(user.getAvatar());
        } else {
//            Toast.makeText(getContext(),"Your session has expired, please re-login again, thanks.", Toast.LENGTH_LONG).show();
//            LoginUtil.logOut(getContext(),null);
        }

        Glide.with(getActivity()).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(imgAvatar);
        if (user != null) {
            tvEarnings.setText("$" + String.format("%.2f", user.getCredit_earnings()) + "");
            tvDeposits.setText("$" + String.format("%.2f", user.getCredit()) + "");
        }


//        Log.e("LOG USER", String.valueOf(user.getAvatar()));
//        Log.e("LOG USER", user.getUsername());
//        Log.e("LOG USER", String.valueOf(user.getDisplayName()));
//        Log.e("LOG AVATAR", a);
//        Log.e("LOG AVATAR USER", String.valueOf(user.getAvatar()));
//        Log.e("LOG EARNINGS", String.valueOf(user.getCredit_earnings()));
//        Log.e("LOG DEPOSITS", String.valueOf(user.getCredit()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(changeCreditBroadCast);
        } catch (Exception e) {
        }
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiverUpdateCountUnread);
        } catch (Exception e) {

        }
    }

    BranchUniversalObject branchUniversalObject;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfile:
                startActivity(new Intent(getActivity(), UserProfileActivity.class));

                break;
            case R.id.btnMyQuestion:

                startActivity(new Intent(getActivity(), MyQuestionActivity.class));
                break;

            case R.id.btnMyAnswer:

                startActivity(new Intent(getActivity(), MyAnswerActivity.class));
                break;
            case R.id.btnMyPreView:
                startActivity(new Intent(getActivity(), MyPreviewActivity.class));


                break;
            case R.id.btnBuyCredit:
                startActivity(new Intent(getActivity(), BuyCreditActivityBackup.class));


                break;
            case R.id.btnTransaction:
                startActivity(new Intent(getActivity(), TransactionViewActivity.class));

                break;
            case R.id.btnShare:

//                LinkProperties linkProperties = new LinkProperties()
//                        .addControlParameter("$deeplink_path", "user_profile/"+user.getId())
//                        .setAlias(user.getUsername())
//                        .setFeature("sharing");
//
//
//                branchUniversalObject.generateShortUrl(getContext(), linkProperties, new Branch.BranchLinkCreateListener() {
//                    @Override
//                    public void onLinkCreate(String url, BranchError error) {
//                        if (error == null) {
//                            Log.i("MyApp", "got my Branch link to share: " + url);
//                            Utils.shareIntent(getContext(),String.valueOf(url));
//                        }
//                    }
//                });
                Utils.shareIntent(getActivity());

                break;
            case R.id.btnMyInterest:


                startActivity(new Intent(getActivity(), MyInterestActivity.class));

                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.nextPage(0);

                break;
            case R.id.btnFavorite:
                final Intent intent = new Intent(getActivity(), FavoriteListActivity.class);
                intent.putExtra(Values.user_id, PreferenceUtil.getUserId(getActivity()) + "");
                startActivity(intent);
                break;
            case R.id.btnWithDraw:
//                if (User.getInstance(getActivity()).getEmail_verified() == 1) {
                startActivity(new Intent(getActivity(), WithdrawActivity.class));
//                } else {
//                    final DialogBase dialogBaseVerify = new DialogBase(getActivity());
//                    dialogBaseVerify.setMessage(getString(R.string.email_non_verified));
//                    dialogBaseVerify.setTitle(getString(R.string.verifi_email));
//                    dialogBaseVerify.setTextOk("OK");
//                    dialogBaseVerify.setOnClickOkListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            VolleyUtils volleyUtils = new VolleyUtils(getActivity(), Values.URL_USER_SEND_CODE_VERIFY);
//                            volleyUtils.addParam(Values.TOKEN, PreferenceUtil.getToken(getActivity()));
//                            volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
//                                @Override
//                                public void onSussces(String response, Result result) {
//                                    if (result.getCode() == 0)
//                                        startActivity(new Intent(getActivity(), EmailVerifiActivity.class));
//                                    else
//                                        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onError(String error) {
//
//                                }
//                            });
//                            volleyUtils.query();
//                            dialogBaseVerify.dismiss();
//                        }
//                    });
//                    dialogBaseVerify.setOnClickCancelListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            dialogBaseVerify.dismiss();
//                        }
//                    });
//                    dialogBaseVerify.show();
//                }


                break;
            case R.id.imgAvatar:

                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;

            case R.id.btnHowWork:
                Utils.goToMyWebView(getActivity(), getString(R.string.how_work), Values.LINK_HOW_WORK);

                break;
            case R.id.btnContact:
                Utils.sendMail(getActivity(), "", "");
                break;
            case R.id.btnJoinExpert:
                Utils.goToMyWebView(getActivity(), getString(R.string.join_experts), Values.LINK_JOIN);
                break;
            case R.id.btnAboutPopDQ:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.about_popdq));
                final String[] strings = getActivity().getResources().getStringArray(R.array.about_list);
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Utils.goToMyWebView(getActivity(), getString(R.string.how_work), Values.LINK_HOW_WORK);
                                break;
                            case 1:
                                Utils.goToMyWebView(getActivity(), getString(R.string.introduction), Values.LINK_INTRO);
                                break;
                            case 2:
                                Utils.goToMyWebView(getActivity(), getString(R.string.term_conditions), Values.LINK_TERMS);
                                break;
                            case 3:
                                Utils.goToMyWebView(getActivity(), getString(R.string.privacy_policy), Values.LINK_PRIVACY);
                                break;
                        }


                    }
                });
                builder.show();
                break;

            case R.id.btnLogOut:
                final DialogBase dialogBase = new DialogBase(getActivity());
                dialogBase.setTitle(getString(R.string.log_out));
                dialogBase.setMessage(getString(R.string.do_you_want_logout));
                dialogBase.getBtnOk().setText("OK");
                dialogBase.setOnClickOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginUtil.logOut(getActivity(),mGoogleApiClient);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
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
                startActivity(new Intent(getActivity(), NotificationActivity.class));

//                tvCountNoti.setVisibility(View.GONE);
                break;
            case R.id.tvEarnings:
            case R.id.tvDeposits:
                startActivity(new Intent(getActivity(), TransactionViewActivity.class));
                break;
        }
    }

    public void getCountUnread() {
        NotificationUtil.getCountUnread(getActivity(), PreferenceUtil.getToken(getActivity()), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                int count = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    count = jsonObject.getInt("total");
                    Intent intent = new Intent("update_unread_has_count");
                    intent.putExtra(Values.COUNT_NOTIFICATION, count);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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

}
