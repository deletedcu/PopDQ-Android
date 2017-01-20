package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.QuestionNewDesignAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.ArrayList;
import java.util.List;

public class SearchByCategoryActivity extends BaseActivity {
    private RecyclerView list;
    private List<Question> questions;
    private QuestionNewDesignAdapter adapter;
    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    public String token;
    private User user;
    String keyword = "";
    private TextViewNormal tvNodata;
    private EditText edtSearch;
    private LinearLayout load;
    private String title;
    private int idCategory;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_category);
        title = getIntent().getStringExtra(Values.name);
        idCategory = getIntent().getIntExtra(Values.category_id, 0);
        Utils.setActionBar(this, title.toUpperCase(), R.drawable.btn_back);
        user = User.getInstance(this);
        url = Values.URL_QUESTION_SEARCH;
        token = PreferenceUtil.getInstancePreference(this).getString(Values.TOKEN, "");
        load = (LinearLayout) findViewById(R.id.load);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        tvNodata = (TextViewNormal) findViewById(R.id.tvNodata);
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(this));
        getQuestion(true, token, "", "", 0, 10);
//        Utils.setActionEnterSearch(this, edtSearch);


        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(SearchByCategoryActivity.this);
//                    submit();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3) {
                    keyword = edtSearch.getText().toString();
                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);

                } else if (charSequence.length() == 0) {
                    keyword = "";
                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public void getQuestion(final boolean newLoad, String token, final String keyword, String tag, int limit, int offset) {
        QuestionUtil.search(url, this,idCategory,  token, keyword, tag, 2, limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                load.setVisibility(View.GONE);
                if (newLoad) {
                    questions = VolleyUtils.getListQuestionFromJson(reponse);
                    setData(questions);
                    isLoading = false;

                } else {
                    List<Question> newQuestion = VolleyUtils.getListQuestionFromJson(reponse);
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
        if (questions == null || questions.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }
        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionNewDesignAdapter(this, data, new ArrayList<User>(), false);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
//                positionClicked = position;
                Intent intent = new Intent(SearchByCategoryActivity.this, ContentQuestionActivity.class);
                long id = data.get(position).getId();
                intent.putExtra(Values.question_id, id);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);
        list.clearOnScrollListeners();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) list.getLayoutManager();
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    int sizeCurrent = adapter.getItemCount();
                    getQuestion(false, token, keyword, "", sizeCurrent, LOAD_ITEM_EACH);
                    isLoading = true;
                    load.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
