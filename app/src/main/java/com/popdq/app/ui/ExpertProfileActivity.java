package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.adapter.QuestionAdapter;
import com.popdq.app.connection.ExpertUtils;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExpertProfileActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView imgAvatar;
    private ImageView imgVerified, imgFavorite;
//    private LinearLayout btnAddToFav;
//    private TextViewNormal tvName, tvProfession, tvExparienceYear, tvDescription, tvLocation;
    private long expert_id;
    private RecyclerView listQuestion;
    private QuestionAdapter adapter;
    private User user;
    private boolean isExpert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);

        Utils.setActionBar(this, getString(R.string.profile), R.drawable.btn_back);
//        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.profile_infomation));

        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
        imgVerified = (ImageView) findViewById(R.id.imgVerified);
        imgFavorite = (ImageView) findViewById(R.id.imgFavorite);

        ((LinearLayout) findViewById(R.id.btnAddToFav)).setOnClickListener(ExpertProfileActivity.this);

        Bundle bundle = getIntent().getExtras();

        expert_id = bundle.getLong(Values.experts_id);
        getData();


    }

    public void getData() {
        ExpertUtils.detail(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                user = VolleyUtils.getUserInfo(response);
                if (user == null) {
                    Toast.makeText(ExpertProfileActivity.this, getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = user.getDisplayName();
                ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setText(name);
                ((TextViewNormal) findViewById(R.id.tvNameAnswer)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.startActivityExpertProfile(ExpertProfileActivity.this, user.getId());
                    }
                });
                if (user.getStatus_anonymous() == 0) {

                } else {
                    final TextViewNormal tvDescription = ((TextViewNormal) findViewById(R.id.tvDescription));
                    ((TextViewNormal) findViewById(R.id.tvProfesstion)).setText(user.getCategoriesString());
                    ((TextViewNormal) findViewById(R.id.tvExperienceYear)).setText(user.getExperience() + "");
                    ((TextViewNormal) findViewById(R.id.tvLocation)).setText(user.getAddress() + "");
                    try {
                        ((TextViewNormal) findViewById(R.id.tvTextPrice)).setText("$ " + user.getConfig_charge()[0].price + "");

                    } catch (Exception e) {
                        ((TextViewNormal) findViewById(R.id.tvTextPrice)).setText("");

                    }
                    try {
                        ((TextViewNormal) findViewById(R.id.tvVoicePrice)).setText("$ " + user.getConfig_charge()[1].price + "");


                    } catch (Exception e) {
                        ((TextViewNormal) findViewById(R.id.tvVoicePrice)).setText("");

                    }
                    try {
                        ((TextViewNormal) findViewById(R.id.tvVideoPrice)).setText("$ " + user.getConfig_charge()[2].price + "");


                    } catch (Exception e) {
                        ((TextViewNormal) findViewById(R.id.tvVideoPrice)).setText("");

                    }
                    tvDescription.setText(user.getDescription() + "");
                    tvDescription.setMovementMethod(new ScrollingMovementMethod());
                    ScrollView rootScroll = (ScrollView) findViewById(R.id.rootScroll);
                    final ScrollView SCROLLER_ID = (ScrollView) findViewById(R.id.SCROLLER_ID);
                    rootScroll.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            tvDescription.getParent().requestDisallowInterceptTouchEvent(false);
                            return false;
                        }
                    });
                    tvDescription.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                }
                try {
                    Glide.with(ExpertProfileActivity.this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(imgAvatar);
                } catch (Exception e) {

                }
                imgFavorite.setImageResource(user.isFavorite() ? R.drawable.add_to_favorite_favlist_added_icon : R.drawable.add_to_favorite_favlist_not_added_icon);
                imgVerified.setImageResource(user.getVerified() == 1 ? R.drawable.ic_verified : R.drawable.ic_not_verified);
                imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user.getAvatar() == null || user.getAvatar().equals("")) {
                            Toast.makeText(ExpertProfileActivity.this, "No avatar!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(ExpertProfileActivity.this, ViewImageActivity.class);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(user.getAvatar());
                        intent.putStringArrayListExtra("listImages", strings);
                        intent.putExtra(Values.index, strings);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });

//        QuestionUtil.getListAnswerOfUser(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
//            @Override
//            public void onSussces(String response, Result result) {
//                setData(VolleyUtils.getListQuestionFromJson(response));
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddToFav:
                if (!user.isFavorite())
                    FavoriteUtils.add(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (VolleyUtils.requestSusscess(response)) {
//                                Toast.makeText(ExpertProfileActivity.this, getString(R.string.add_to_favorite_succes), Toast.LENGTH_SHORT).show();

                                imgFavorite.setImageResource(R.drawable.add_to_favorite_favlist_added_icon);
                                user.setFavorite(true);
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                else
                    FavoriteUtils.remove(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (VolleyUtils.requestSusscess(response)) {
//                                Toast.makeText(ExpertProfileActivity.this, getString(R.string.remove_to_favorite_succes), Toast.LENGTH_SHORT).show();
                                imgFavorite.setImageResource(R.drawable.add_to_favorite_favlist_not_added_icon);
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

    public void setData(final List<Question> data) {
        listQuestion = (RecyclerView) findViewById(R.id.list);
        listQuestion.setNestedScrollingEnabled(false);
        listQuestion.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuestionAdapter(this, data);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ExpertProfileActivity.this, ContentQuestionActivity.class);
                long id = data.get(position).getId();
                intent.putExtra(Values.question_id, id);
                startActivity(intent);

            }
        });
        listQuestion.setAdapter(adapter);
    }
}


//986.52 976.53 986.52