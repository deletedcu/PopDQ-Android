package com.popdq.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popdq.app.R;
import com.popdq.app.adapter.QuestionNewDesignAdapter;
import com.popdq.app.connection.ExpertUtils;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.EndlessScrollView;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int UPDATE_INFO = 156;
    private CircleImageView imgAvatar;
    private ImageView imgVerified;
    private long expert_id;
    private RecyclerView listQuestion;
    private QuestionNewDesignAdapter adapter;
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
    private boolean isLoadedLastItem = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setSupportActionBar(((Toolbar) findViewById(R.id.toolbar)));
        getSupportActionBar().setTitle("");

        Utils.setActionBar(this, getString(R.string.profile), R.drawable.btn_back);
//        ((TextViewNormal) findViewById(R.id.optionName)).setText(getString(R.string.profile_infomation));

        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
        imgVerified = (ImageView) findViewById(R.id.imgVerified);
        imgFollow = (ImageView) findViewById(R.id.imgFollow);
        tvNodata = (TextViewNormal) findViewById(R.id.tvNodata);
        load = (LinearLayout) findViewById(R.id.load);
        imgFollow.setOnClickListener(this);
        listQuestion = (RecyclerView) findViewById(R.id.list);
        btnAsk = (Button) findViewById(R.id.btnAsk);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra(Values.experts_id)) {
            expert_id = bundle.getLong(Values.experts_id);
            areYourAcc = false;
        } else {
            expert_id = PreferenceUtil.getUserId(this);
            areYourAcc = true;

        }
        getData();


    }

    public void getData() {
        ExpertUtils.detail(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                user = VolleyUtils.getUserInfo(response);
                if (user == null) {
                    Toast.makeText(UserProfileActivity.this, getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (User.getInstance(UserProfileActivity.this).getId() != user.getId()) {

                    btnAsk.setVisibility(View.VISIBLE);
                    imgFollow.setVisibility(View.VISIBLE);
                    btnAsk.setOnClickListener(UserProfileActivity.this);
                    areYourAcc = false;
                } else areYourAcc = true;
                if (user.isDisableAllMethod()) {
                    btnAsk.setVisibility(View.GONE);
                }

                invalidateOptionsMenu();

//                String name = user.getDisplayName();
                ((TextViewBold) findViewById(R.id.tvUserName)).setText(user.getUsername());
                if (user.getUsername() != null)
                    ((TextViewNormal) findViewById(R.id.title)).setText(user.getUsername().toUpperCase());
                ((TextViewNormal) findViewById(R.id.tvName)).setText(user.getName());
                if (user.getStatus_anonymous() == 0 && !areYourAcc) {
                } else {
                    final TextViewNormal tvDescription = ((TextViewNormal) findViewById(R.id.tvDescription));

//                    String professtion = user.getCategoriesString();
                    String professtion = user.getProfessional_field();
                    ((TextViewNormal) findViewById(R.id.tvProfesstion)).setSelected(true);

//                    Log.e("PROFESSION",professtion);

                    if (professtion == null || professtion.equals("")) {
                        ((TextViewNormal) findViewById(R.id.tvProfesstion)).setText(" ");
                    } else {
                        ((TextViewNormal) findViewById(R.id.tvProfesstion)).setText(user.getProfessional_field());
                    }
                    String location = user.getAddress();
                    if (location == null || location.equals("")) {
                        ((TextViewNormal) findViewById(R.id.tvLocation)).setText(" ");
                    } else {
                        ((TextViewNormal) findViewById(R.id.tvLocation)).setText(user.getAddress() + "");
                    }
                    ((TextViewNormal) findViewById(R.id.tvLocation)).setSelected(true);

                    ((TextViewThin) findViewById(R.id.tvFollowers)).setText(getString(R.string.follower) + " " + user.getTotal_favorite());
                    ((TextViewThin) findViewById(R.id.tvFollowing)).setText(getString(R.string.following) + " " + user.getTotal_follow());

                    ((TextViewThin) findViewById(R.id.tvFollowers)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserProfileActivity.this, FavoriteListActivity.class);
                            intent.putExtra(Values.favorite, user.getId() + "");
                            startActivity(intent);
                        }
                    });

                    ((TextViewThin) findViewById(R.id.tvFollowing)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserProfileActivity.this, FavoriteListActivity.class);
                            intent.putExtra(Values.user_id, user.getId() + "");
                            startActivity(intent);
                        }
                    });

                    try {
                        Utils.setLayoutCredit(user.getConfig_charge()[0].price, (LinearLayout) findViewById(R.id.layoutTextCredit), (TextViewThin) findViewById(R.id.tvTextPrice));
                    } catch (Exception e) {
                        Utils.setLayoutCredit(0, (LinearLayout) findViewById(R.id.layoutTextCredit), (TextViewThin) findViewById(R.id.tvTextPrice));
                    }
                    try {
                        Utils.setLayoutCredit(user.getConfig_charge()[1].price, (LinearLayout) findViewById(R.id.layoutVoiceCredit), (TextViewThin) findViewById(R.id.tvVoicePrice));
                    } catch (Exception e) {
                        Utils.setLayoutCredit(0, (LinearLayout) findViewById(R.id.layoutVoiceCredit), (TextViewThin) findViewById(R.id.tvVoicePrice));

                    }
                    try {
                        Utils.setLayoutCredit(user.getConfig_charge()[2].price, (LinearLayout) findViewById(R.id.layoutVideoCredit), (TextViewThin) findViewById(R.id.tvVideoPrice));
                    } catch (Exception e) {
                        Utils.setLayoutCredit(0, (LinearLayout) findViewById(R.id.layoutVideoCredit), (TextViewThin) findViewById(R.id.tvVideoPrice));

                    }

//
//                    if (user.getDescription().equals("")) {
//                        tvDescription.setText(" ");
//
//                    } else
                    tvDescription.setText(user.getDescription() + "");

                }
                try {
                    Glide.with(UserProfileActivity.this).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(imgAvatar);
                } catch (Exception e) {

                }

                imgFollow.setImageResource(user.isFavorite() ? R.drawable.ic_following : R.drawable.ic_follow);
                imgVerified.setImageResource(user.getVerified() == 1 ? R.drawable.ic_verified : android.R.color.transparent);
                imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user.getAvatar() == null || user.getAvatar().equals("")) {
                            Toast.makeText(UserProfileActivity.this, "No avatar!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(UserProfileActivity.this, ViewImageActivity.class);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(user.getAvatar());
                        intent.putStringArrayListExtra("listImages", strings);
                        intent.putExtra(Values.index, strings);
                        startActivity(intent);
                    }
                });
                getQuestion(true, 0, LOAD_ITEM_EACH);
            }

            @Override
            public void onError(String error) {

            }
        });


    }

//    public void setLayoutCredit(float credit, LinearLayout layoutCredit, TextViewThin tvCredit) {
//        if (credit == -1) {
//            layoutCredit.setVisibility(View.GONE);
//        } else if (credit == 0) {
//            layoutCredit.setVisibility(View.VISIBLE);
//            tvCredit.setText("FREE");
//        } else {
//            layoutCredit.setVisibility(View.VISIBLE);
//            tvCredit.setText("$" + credit);
//        }
//    }

    public void getQuestion(final boolean newLoad, int limit, int offset) {
        QuestionUtil.getListAnsweredOfUser(this, PreferenceUtil.getToken(this), expert_id, limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                load.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ((TextViewNormal) findViewById(R.id.tvTitleListQuestion)).setText(getString(R.string.my_answerd_question) + " (" + jsonObject.getString(Values.total) + ")");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (newLoad) {
                    questions = VolleyUtils.getListQuestionFromJson(response);
                    setData(questions);
                    isLoading = false;
                    isLoadedLastItem = false;

                } else {
                    List<Question> newQuestion = VolleyUtils.getListQuestionFromJson(response);

                    for (Question question : newQuestion) {
                        adapter.getQuestions().add(question);
                    }
                    if (newQuestion.size() > 0) {
                        isLoading = false;
                        adapter.notifyDataSetChanged();
                        isLoadedLastItem = false;
                    } else {
                        isLoadedLastItem = true;
                    }
                }

            }

            @Override
            public void onError(String error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAsk:

                Intent intent = new Intent(this, ProvideQuestionActivity.class);
                intent.putExtra(Values.user_id_answer, user.getId());
                intent.putExtra(Values.language_written, user.getLanguage_answer());
                intent.putExtra(Values.name, user.getDisplayName());
                intent.putExtra(Values.avatar, user.getAvatar());
                intent.putExtra(Values.professional_field, user.getCategoriesString());

                if (getIntent().hasExtra(Values.category)) {
                    intent.putExtra(Values.category, getIntent().getStringExtra(Values.category));
                }


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
                    FavoriteUtils.add(this, PreferenceUtil.getToken(this), expert_id, new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (VolleyUtils.requestSusscess(response)) {
//                                Toast.makeText(UserProfileActivity.this, getString(R.string.add_to_favorite_succes), Toast.LENGTH_SHORT).show();
                                imgFollow.setImageResource(R.drawable.ic_following);
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
//                                Toast.makeText(UserProfileActivity.this, getString(R.string.remove_to_favorite_succes), Toast.LENGTH_SHORT).show();
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

    public void setData(final List<Question> data) {
        if (data == null || data.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
            tvNodata.setText(String.format(getString(R.string.nodata_another_user), user.getUsername(),  user.getUsername()));
            listQuestion.setVisibility(View.GONE);

        } else {
            tvNodata.post(new Runnable() {
                @Override
                public void run() {
                    tvNodata.setVisibility(View.GONE);
                }
            });

            EndlessScrollView myScrollView = (EndlessScrollView) findViewById(R.id.rootScroll);
            myScrollView.setScrollViewListener(new EndlessScrollView.EndlessScrollListener() {
                @Override
                public void onScrollChanged(EndlessScrollView scrollView, int x, int y, int oldx, int oldy) {
                    // We take the last son in the scrollview
                    View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                    int distanceToEnd = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                    // if diff is zero, then the bottom has been reached
                    if (distanceToEnd == 0 && !isLoadedLastItem) {
                        int sizeCurrent = adapter.getItemCount();
                        getQuestion(false, sizeCurrent, LOAD_ITEM_EACH);
                        isLoading = true;
                        load.setVisibility(View.VISIBLE);
//                        Toast.makeText(UserProfileActivity.this, distanceToEnd+"", Toast.LENGTH_SHORT).show();
                        // do stuff your load more stuff

                    }
                }
            });
            listQuestion.setVisibility(View.VISIBLE);
            listQuestion.setNestedScrollingEnabled(false);
            listQuestion.setLayoutManager(new LinearLayoutManager(this));
            adapter = new QuestionNewDesignAdapter(this, data, new ArrayList<User>(), true);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(UserProfileActivity.this, ContentQuestionActivity.class);
                    long id = data.get(position).getId();
                    intent.putExtra(Values.question_id, id);
                    startActivity(intent);

                }
            });
            listQuestion.clearOnScrollListeners();
            listQuestion.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listQuestion.getLayoutManager();
//            listQuestion.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int current_page) {
//                    Toast.makeText(UserProfileActivity.this,current_page+"", Toast.LENGTH_SHORT).show();
//                }
//            });

//            listQuestion.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    if(dy > 0) //check for scroll down
//                    {
//                        int visibleItemCount = linearLayoutManager.getChildCount();
//                        int totalItemCount = linearLayoutManager.getItemCount();
//                        int lastVisibleItem  = linearLayoutManager.findFirstVisibleItemPosition();
//
//                        if (isLoading)
//                        {
//                            if ( (visibleItemCount + lastVisibleItem) >= totalItemCount)
//                            {
//                                isLoading = false;
//                                Log.v("...", "Last Item Wow !");
//                                //Do pagination.. i.e. fetch new data
//                            }
//                        }
//                    }
//                    super.onScrolled(recyclerView, dx, dy);
//                    int totalItemCount = linearLayoutManager.getItemCount();
//                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                        int sizeCurrent = adapter.getItemCount();
//                        getQuestion(false, sizeCurrent, LOAD_ITEM_EACH);
//                        isLoading = true;
//                        load.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        if (User.getInstance(UserProfileActivity.this).getId() == user.getId()) {
//        if (areYourAcc) {
//            getMenuInflater().inflate(R.menu.menu_my_profile, menu);
//            return super.onCreateOptionsMenu(menu);
//        } else
////            for (int i = 0; i < menu.size(); i++)
////                menu.getItem(i).setVisible(false);
//            return false;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_edit) {
//            startActivityForResult(new Intent(this, EditProfileActivity.class), UPDATE_INFO);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_INFO) {
            getData();
        }
    }
}

