package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.adapter.UserProfileAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

import java.util.List;

public class UserProfileTestActivity extends BaseActivity {
    private RecyclerView profile;
    private UserProfileAdapter adapter;

    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    private List<Question> questions;
    private long expert_id;
    private boolean areYourAcc;
    private LinearLayout load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_test);
        Utils.setActionBar(this, getString(R.string.profile), R.drawable.btn_back);
        profile = (RecyclerView) findViewById(R.id.profile);
        load = (LinearLayout) findViewById(R.id.load);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra(Values.experts_id)) {
            expert_id = bundle.getLong(Values.experts_id);
            areYourAcc = false;
        } else {
            expert_id = PreferenceUtil.getUserId(this);
            areYourAcc = true;

        }
        getQuestion(true, 0, 5);
    }

    public void getQuestion(final boolean newLoad, int limit, int offset) {
        QuestionUtil.getListAnswerOfUser(this, PreferenceUtil.getToken(this), expert_id, limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                load.setVisibility(View.GONE);
                if (newLoad) {
                    questions = VolleyUtils.getListQuestionFromJson(response);
                    setData(questions);
                    isLoading = false;
                } else {
                    List<Question> newQuestion = VolleyUtils.getListQuestionFromJson(response);
                    for (Question question : newQuestion) {
                        adapter.getQuestions().add(question);
                    }
                    if (newQuestion.size() > 0) {
                        isLoading = false;
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onError(String error) {

            }
        });

    }

    public void setData(final List<Question> data) {
        if (data == null || data.size() < 0) {
//            profile.setVisibility(View.GONE);
        } else {
//            tvNodata.post(new Runnable() {
//                @Override
//                public void run() {
//                    tvNodata.setVisibility(View.GONE);
//                }
//            });
//            ((TextViewNormal) findViewById(R.id.tvTitleListQuestion)).setText(getString(R.string.my_answerd_question) + " (" + data.size() + ")");
            profile.setVisibility(View.VISIBLE);
            profile.setNestedScrollingEnabled(false);
            profile.setLayoutManager(new LinearLayoutManager(this));
            adapter = new UserProfileAdapter(this, data, expert_id);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(UserProfileTestActivity.this, ContentQuestionActivity.class);
                    long id = data.get(position).getId();
                    intent.putExtra(Values.question_id, id);
                    startActivity(intent);
                }
            });
            profile.clearOnScrollListeners();
            profile.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) profile.getLayoutManager();
            profile.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        int sizeCurrent = adapter.getItemCount();
                        getQuestion(false, sizeCurrent, LOAD_ITEM_EACH);
                        isLoading = true;
                        adapter.setCountDisplay();
                        load.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

}
