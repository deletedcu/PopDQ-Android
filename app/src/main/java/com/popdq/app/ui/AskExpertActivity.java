package com.popdq.app.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.ExpertAdapter;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.List;

public class AskExpertActivity extends BaseSearchActivity {
    private static final int LOAD_ITEM_EACH = 10;
    private RecyclerView listExpert;
    private List<User> users;
    private EditText edtSearch;
    private String token;
    private String keyword = "";
    boolean isLoading;
    private ExpertAdapter adapter;
    public int visibleThreshold = 1;
    private TextViewNormal tvNodata;
    private String verifed = "";
    private Interest category;
    private String tokenFb = "";
    private boolean isSearChFbFriend = false;
    private static int PAGE_LOAD;
    private String idCategory = "";
    private String search = "";
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_expert);

//        search = getIntent().getStringExtra(Values.search);

        PAGE_LOAD = 0;
        if (getIntent().hasExtra(Values.verified)) {
            if (getIntent().getBooleanExtra(Values.verified, true)) {
                verifed = "1";
                Utils.setActionBar(this, getString(R.string.featured_users), R.drawable.btn_back);
            } else {
                verifed = "0";
                Utils.setActionBar(this, getString(R.string.all_users), R.drawable.btn_back);
            }
        } else {
            Utils.setActionBar(this, getString(R.string.ask_expert).toUpperCase(), R.drawable.btn_back);
        }

        try {
            if (getIntent().hasExtra(Values.category)) {
                category = new Gson().fromJson(getIntent().getStringExtra(Values.category), Interest.class);
                idCategory = category.getId() + "";
            }
//            expertCategory = getIntentNotificationBar().getExtras().getString(Values.expertCategory, "");
//            if (!expertCategory.equals("")) {
//                categoryName = getIntentNotificationBar().getExtras().getString(Values.category_name);
//                ((TextViewNormal) findViewById(R.id.title)).setText(categoryName.toUpperCase());
//            }
        } catch (Exception e) {

        }

        try {
            if (getIntent().hasExtra(Values.fbToken)) {
                ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.title_friend));

                isSearChFbFriend = true;
                tokenFb = getIntent().getStringExtra(Values.fbToken);
            } else isSearChFbFriend = false;
        } catch (Exception e) {

        }


        listExpert = (RecyclerView) findViewById(R.id.listExpert);
        edtSearch = ((EditText) findViewById(R.id.edtSearch));
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(this));
        tvNodata = (TextViewNormal) findViewById(R.id.tvNodata);
        edtSearch.setHint(getString(R.string.hint_search_users));
//        edtSearch.setText(search);
        token = PreferenceUtil.getToken(this);
//        Utils.setActionEnterSearch(this, edtSearch);


        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(AskExpertActivity.this);
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
//                if (charSequence.length() >= 3) {
                keyword = edtSearch.getText().toString();
                getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);
//                } else if (charSequence.length() == 0) {
//                    keyword = "";
//                    getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);
//
//                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        if (getIntent().hasExtra(Values.keyword)) {
//            keyword = getIntent().getStringExtra(Values.keyword);
//            edtSearch.setText(keyword);
//        }

        if (getIntent().hasExtra(Values.search)) {
            keyword = getIntent().getStringExtra(Values.search);
            edtSearch.setText(keyword);
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);   
    }

    public void getExpert(final boolean newLoad, String token, final String keyword, int limit, int offset) {
        PAGE_LOAD++;
        if (isSearChFbFriend) {
            Log.e("fbtoken", tokenFb);
            UserUtil.searchFriendFacebook(this, token, tokenFb, keyword, "", limit, offset, "", new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String response, Result result) {

                    if (newLoad) {
                        listExpert.setLayoutManager(new LinearLayoutManager(AskExpertActivity.this));
                        users = VolleyUtils.getListUserFromJsonNotMe(AskExpertActivity.this, response);
//                    listExpert.setAdapter(new ExpertAdapter(AskExpertActivity.this, users));
                        isLoading = false;
                        setData(users);

                    } else {
                        List<User> newUser = VolleyUtils.getListUserFromJsonNotMe(AskExpertActivity.this, response);
                        for (User user : newUser) {
                            adapter.getExpertProfiles().add(user);
                        }
                        if (newUser.size() > 0) {
                            isLoading = false;
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            UserUtil.search(this, token, keyword, idCategory, limit, offset, verifed, new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String reponse, Result result) {

                    if (newLoad) {
                        listExpert.setLayoutManager(new LinearLayoutManager(AskExpertActivity.this));
                        users = VolleyUtils.getListUserFromJsonNotMe(AskExpertActivity.this, reponse);
//                    listExpert.setAdapter(new ExpertAdapter(AskExpertActivity.this, users));
                        isLoading = false;
                        setData(users);

                    } else {
                        List<User> newUser = VolleyUtils.getListUserFromJsonNotMe(AskExpertActivity.this, reponse);
                        for (User user : newUser) {
                            adapter.getExpertProfiles().add(user);
                        }
                        if (newUser.size() > 0) {
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

    }


    public void setData(final List<User> data) {
        if (data == null || data.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }
        listExpert = (RecyclerView) findViewById(R.id.listExpert);
        listExpert.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpertAdapter(this, data);
        adapter.setInterest(category);
//        adapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onClick(int position) {
//                Intent intent = new Intent(getActivity(), ContentQuestionActivity.class);
//                long id = data.get(position).getId();
//                intent.putExtra(Values.question_id, id);
//                startActivity(intent);
//
//            }
//        });
        listExpert.clearOnScrollListeners();

        listExpert.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listExpert.getLayoutManager();
        if (!isSearChFbFriend)
            listExpert.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        int sizeCurrent = PAGE_LOAD * LOAD_ITEM_EACH;

                        getExpert(false, token, keyword, sizeCurrent, LOAD_ITEM_EACH);
                        isLoading = true;
//                    load.setVisibility(View.VISIBLE);
                    }
                }
            });
    }

}
