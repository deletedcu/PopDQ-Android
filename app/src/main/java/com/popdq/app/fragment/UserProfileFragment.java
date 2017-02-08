package com.popdq.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.EditProfileActivity;
import com.popdq.app.ui.FavoriteListActivity;
import com.popdq.app.ui.LoginActivity;
import com.popdq.app.ui.ProvideQuestionActivity;
import com.popdq.app.ui.ViewImageActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private static final int UPDATE_INFO = 156;
    private CircleImageView imgAvatar;
    private ImageView imgVerified;
    private long expert_id;
    private ViewPager pager;
    protected User user;
    protected ImageView imgFollow;
    protected TextViewNormal tvNodata;
    private boolean areYourAcc = false;
    private LinearLayout load;
    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    private List<Question> questions;
    private Button btnAsk;
    private View view;
    private RelativeLayout root;
    private RelativeLayout btnEdit;
    private SwipeRefreshLayout swipeRefreshLayout;

    private BroadcastReceiver changeCreditBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
//            try {
//                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("change_user"));
//            } catch (Exception e) {
//
//            }
        }
    };
    private TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_info_fragment, container, false);

        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        imgVerified = (ImageView) view.findViewById(R.id.imgVerified);
        imgFollow = (ImageView) view.findViewById(R.id.imgFollow);
        tvNodata = (TextViewNormal) view.findViewById(R.id.tvNodata);
        load = (LinearLayout) view.findViewById(R.id.load);
        btnEdit = ((RelativeLayout) view.findViewById(R.id.btnEdit));

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        imgFollow.setOnClickListener(this);
        pager = (ViewPager) view.findViewById(R.id.pager);
        root = (RelativeLayout) view.findViewById(R.id.root);
        pager.postDelayed(new Runnable() {
            @Override
            public void run() {
                int height = ((View) view.findViewById(R.id.view_space)).getHeight();
                ViewGroup.LayoutParams params = pager.getLayoutParams();
                params.height = height - tabLayout.getHeight();
                pager.setLayoutParams(params);

            }
        }, 500);
        btnEdit.setOnClickListener(this);
        getData();

        IntentFilter intentFilter2 = new IntentFilter("change_user");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(changeCreditBroadCast, intentFilter2);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);


                getData();

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(changeCreditBroadCast);
        } catch (Exception e) {

        }
    }

    public void getData() {
        pager.setAdapter(new QuestionPager(getChildFragmentManager()));
        tabLayout.setupWithViewPager(pager);

        UserUtil.getMyProfile(getActivity(), PreferenceUtil.getToken(getActivity()), new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                VolleyUtils.getUserAndPushPreference(getActivity(), response);
                user = MyApplication.user;
                setData();
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("change_user_more_tab"));
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void setData() {
        if (user == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(getContext(), getString(R.string.nodata), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        if (User.getInstance(getActivity()).getId() != user.getId()) {
            btnAsk = (Button) view.findViewById(R.id.btnAsk);
            btnAsk.setVisibility(View.VISIBLE);
            imgFollow.setVisibility(View.VISIBLE);
            btnAsk.setOnClickListener(UserProfileFragment.this);
            areYourAcc = false;
        } else areYourAcc = true;


        String name = user.getDisplayName();
        ((TextViewBold) view.findViewById(R.id.tvNameAnswer)).setText(user.getUsername());
//        ((TextViewNormal) view.findViewById(R.id.title)).setText(user.getUsername().toUpperCase());
        if (user.getStatus_anonymous() == 0 && !areYourAcc) {
        } else {
            final TextViewNormal tvDescription = ((TextViewNormal) view.findViewById(R.id.tvDescription));
            String professtion = user.getProfessional_field();
            ((TextViewNormal) view.findViewById(R.id.tvProfesstion)).setSelected(true);

            if (professtion == null || professtion.equals("")) {
                ((TextViewNormal) view.findViewById(R.id.tvProfesstion)).setText(" ");
            } else {
                ((TextViewNormal) view.findViewById(R.id.tvProfesstion)).setText(user.getProfessional_field());
            }
            String location = user.getAddress();
//            if (location == null || location.equals("")) {
//                ((TextViewNormal) view.findViewById(R.id.tvLocation)).setText(" ");
//            } else {
            ((TextViewNormal) view.findViewById(R.id.tvLocation)).setText(user.getAddress() + "");
//            }
            ((TextViewNormal) view.findViewById(R.id.tvLocation)).setSelected(true);
            ((TextViewNormal) view.findViewById(R.id.tvName)).setText(user.getName());

            ((TextViewThin) view.findViewById(R.id.tvFollowers)).setText(getString(R.string.follower) + " " + user.getTotal_favorite());
            ((TextViewThin) view.findViewById(R.id.tvFollowing)).setText(getString(R.string.following) + " " + user.getTotal_follow());

            ((TextViewThin) view.findViewById(R.id.tvFollowers)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FavoriteListActivity.class);
                    intent.putExtra(Values.favorite, user.getId() + "");
                    startActivity(intent);
                }
            });

            ((TextViewThin) view.findViewById(R.id.tvFollowing)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FavoriteListActivity.class);
                    intent.putExtra(Values.user_id, user.getId() + "");
                    startActivity(intent);
                }
            });

            try {
                Utils.setLayoutCredit(user.getConfig_charge()[0].price, (LinearLayout) view.findViewById(R.id.layoutTextCredit), (TextViewThin) view.findViewById(R.id.tvTextPrice));
            } catch (Exception e) {
                Utils.setLayoutCredit(0, (LinearLayout) view.findViewById(R.id.layoutTextCredit), (TextViewThin) view.findViewById(R.id.tvTextPrice));
            }
            try {
                Utils.setLayoutCredit(user.getConfig_charge()[1].price, (LinearLayout) view.findViewById(R.id.layoutVoiceCredit), (TextViewThin) view.findViewById(R.id.tvVoicePrice));
            } catch (Exception e) {
                Utils.setLayoutCredit(0, (LinearLayout) view.findViewById(R.id.layoutVoiceCredit), (TextViewThin) view.findViewById(R.id.tvVoicePrice));

            }
            try {
                Utils.setLayoutCredit(user.getConfig_charge()[2].price, (LinearLayout) view.findViewById(R.id.layoutVideoCredit), (TextViewThin) view.findViewById(R.id.tvVideoPrice));
            } catch (Exception e) {
                Utils.setLayoutCredit(0, (LinearLayout) view.findViewById(R.id.layoutVideoCredit), (TextViewThin) view.findViewById(R.id.tvVideoPrice));

            }

//            if (user.getDescription().equals("")) {
//                tvDescription.setText(" ");
//
//            } else
            tvDescription.setText(user.getDescription() + "");
        }
        try {
            Glide.with(UserProfileFragment.this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(imgAvatar);
        } catch (Exception e) {

        }

        imgFollow.setImageResource(user.isFavorite() ? R.drawable.ic_following : R.drawable.ic_follow);
        imgVerified.setImageResource(user.getVerified() == 1 ? R.drawable.ic_verified : android.R.color.transparent);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getAvatar() == null || user.getAvatar().equals("")) {
                    Toast.makeText(getActivity(), "No avatar!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                ArrayList<String> strings = new ArrayList<>();
                strings.add(user.getAvatar());
                intent.putStringArrayListExtra("listImages", strings);
                intent.putExtra(Values.index, strings);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                startActivityForResult(new Intent(getActivity(), EditProfileActivity.class), UPDATE_INFO);
                break;

            case R.id.btnAsk:

                Intent intent = new Intent(getActivity(), ProvideQuestionActivity.class);
                intent.putExtra(Values.user_id_answer, user.getId());
                intent.putExtra(Values.language_written, user.getLanguage_answer());
                intent.putExtra(Values.name, user.getDisplayName());
                intent.putExtra(Values.avatar, user.getAvatar());
                intent.putExtra(Values.professional_field, user.getCategoriesString());

                try {
                    intent.putExtra(Values.text_credit, user.getConfig_charge()[0].price);
                } catch (Exception e) {
                    intent.putExtra(Values.text_credit, 0);
                }
                try {
                    intent.putExtra(Values.voice_credit, user.getConfig_charge()[1].price);
                } catch (Exception e) {
                    intent.putExtra(Values.text_credit, 0);
                }
                try {
                    intent.putExtra(Values.video_credit, user.getConfig_charge()[2].price);
                } catch (Exception e) {
                    intent.putExtra(Values.text_credit, 0);
                }

                startActivity(intent);
                break;

            case R.id.imgFollow:
                if (!user.isFavorite())
                    FavoriteUtils.add(getActivity(), PreferenceUtil.getToken(getActivity()), expert_id, new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (VolleyUtils.requestSusscess(response)) {
//                                Toast.makeText(getActivity(), getString(R.string.add_to_favorite_succes), Toast.LENGTH_SHORT).show();
                                imgFollow.setImageResource(R.drawable.ic_following);
                                user.setFavorite(true);
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                else
                    FavoriteUtils.remove(getActivity(), PreferenceUtil.getToken(getActivity()), expert_id, new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (VolleyUtils.requestSusscess(response)) {
//                                Toast.makeText(getActivity(), getString(R.string.remove_to_favorite_succes), Toast.LENGTH_SHORT).show();
                                imgFollow.setImageResource(R.drawable.ic_follow);
                                user.setFavorite(false);
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_INFO) {

            getData();
        }
    }

    public class QuestionPager extends FragmentPagerAdapter {
        QuestionFragmentNewDesign question, answer;

        public QuestionPager(FragmentManager fm) {
            super(fm);
            question = new QuestionFragmentNewDesign();

            question.isHideSearchBar = true;
            question.setUrl(Values.URL_QUESTION_MY_LIST);
            question.setMyAnswer(false);
//            question.setUrl(Values.URL_QUESTION_ANSWERED);
//            question.setMyAnswer(true);

            answer = new QuestionFragmentNewDesign();
            answer.isHideSearchBar = true;
            answer.setUrl(Values.URL_QUESTION_ANSWERED);
            answer.setMyAnswer(true);
//            answer.setUrl(Values.URL_QUESTION_MY_LIST);
//            answer.setMyAnswer(false);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return question;

                case 0:
                    return answer;

            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.my_question).toUpperCase();
                case 0:
                    return getString(R.string.my_answer).toUpperCase();
                default:
                    return getString(R.string.my_question).toUpperCase();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}