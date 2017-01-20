package com.popdq.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.ExpertAdapter;
import com.popdq.app.connection.CategoryUtils;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoryHasSearchActivity extends BaseSearchActivity {
    private static final String TAG = "MyInterestActivity";
    private ListView listInterest;
    private List<Interest> myInterests;
    private SharedPreferences sharedPreferences;
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
    //    private TextViewNormal layoutTitle;
    private String expertCategory;
    private String tokenFb = "";
    private boolean isSearChFbFriend = false;
    private static int PAGE_LOAD;
    private static final int LOAD_ITEM_EACH = 10;
    private TextViewNormal btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category_has_search);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listInterest = (ListView) findViewById(R.id.listInterest);
        token = sharedPreferences.getString(Values.TOKEN, "");
        Utils.setActionBar(this, getString(R.string.select_category).toUpperCase(), R.drawable.btn_back);
        initData();

        listExpert = (RecyclerView) findViewById(R.id.listExpert);
        edtSearch = ((EditText) findViewById(R.id.edtSearch));
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(this));
        tvNodata = (TextViewNormal) findViewById(R.id.tvNodata);
        edtSearch.setHint(getString(R.string.hint_search_users));
        token = PreferenceUtil.getToken(this);

        btnCancel = (TextViewNormal) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtSearch.getText().toString().length() > 0) {
                    edtSearch.setText("");
                    btnCancel.setVisibility(View.GONE);
                }
                btnCancel.setVisibility(View.GONE);
            }
        });
       listExpert.setVisibility(View.GONE);

        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(SelectCategoryHasSearchActivity.this);
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
                if (charSequence.length() >= 1) {
                    listExpert.setVisibility(View.VISIBLE);
                    listInterest.setVisibility(View.GONE);
                    keyword = edtSearch.getText().toString();
//                    btnBottom.setVisibility(View.GONE);
                    getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);

//                    adapter.visibleTitleWhenSearch(true);
                } else if (charSequence.length() == 0) {
                    keyword = "";
//                    getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);
                    listExpert.setVisibility(View.GONE);
                    listInterest.setVisibility(View.VISIBLE);
//                    btnBottom.setVisibility(View.GONE);
//                    adapter.visibleTitleWhenSearch(false);
//                    btnSearchByArea.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    btnCancel.setVisibility(View.VISIBLE);

                } else {
                    if (keyword.equals(""))
                        btnCancel.setVisibility(View.GONE);

                }

            }
        });
//        getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);



    }

    public void initData() {
        myInterests = new ArrayList<>();
        getData();
    }

    public void setDataList() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(SelectCategoryHasSearchActivity.this, R.layout.item_category_search) {
            @Override
            public int getCount() {
                return myInterests.size();
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.item_category_search, null);
                }
                RelativeLayout root = (RelativeLayout) v.findViewById(R.id.root);
                final Interest myInterest = myInterests.get(position);
                TextViewNormal tvName = (TextViewNormal) v.findViewById(R.id.tvNameCategory);
                tvName.setText(myInterest.getName());
                root.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                MixPanelUtil.trackCategory(SelectCategoryHasSearchActivity.this, myInterest);

                                                if (getIntent().getBooleanExtra(Values.GET_CATEGORY, false)) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra(Values.category, new Gson().toJson(myInterest));
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                } else if (getIntent().hasExtra(Values.find_user_category)) {
//                                                    Intent intent = new Intent(getContext(), SearchByCategoryActivity.class);
//                                                    intent.putExtra(Values.name, myInterest.getName());
//                                                    intent.putExtra(Values.category_id, myInterest.getId());
                                                    Intent intent = new Intent(getContext(), AskExpertActivity.class);
                                                    intent.putExtra(Values.find_user_category, true);
                                                    String jsonInterest = new Gson().toJson(new Interest(myInterest.getId(),myInterest.getName()));
                                                    intent.putExtra(Values.category, jsonInterest);
//                                                    intent.putExtra(Values.expertCategory, myInterest.getId()+"");
                                                    startActivity(intent);


                                                } else {
                                                    Intent intent = new Intent(getContext(), SearchByCategoryActivity.class);
                                                    intent.putExtra(Values.name, myInterest.getName());
                                                    intent.putExtra(Values.category_id, myInterest.getId());
                                                    startActivity(intent);
                                                }
                                            }

                                        }
                );
                return v;
            }
        };
        listInterest.setAdapter(adapter);
    }

    public void getData() {
        myInterests = CategoryUtils.getListFromLocal(this);
        if (myInterests == null || myInterests.size() < 1) {
            CategoryUtils.getListCategories(this, token, new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String response, Result result) {
                    myInterests = CategoryUtils.getListFromRespone(SelectCategoryHasSearchActivity.this, response);
                    setDataList();
                }

                @Override
                public void onError(String error) {

                }
            });
        } else setDataList();


    }

    public void getExpert(final boolean newLoad, String token, final String keyword, int limit, int offset) {
        PAGE_LOAD++;
        if (isSearChFbFriend) {
            UserUtil.searchFriendFacebook(this, token, tokenFb, keyword, "", limit, offset, "", new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String response, Result result) {

                    if (newLoad) {
                        listExpert.setLayoutManager(new LinearLayoutManager(SelectCategoryHasSearchActivity.this));
                        users = VolleyUtils.getListUserFromJsonNotMe(SelectCategoryHasSearchActivity.this, response);
//                    listExpert.setAdapter(new ExpertAdapter(AskExpertActivity.this, users));
                        isLoading = false;
                        setData(users);

                    } else {
                        List<User> newUser = VolleyUtils.getListUserFromJsonNotMe(SelectCategoryHasSearchActivity.this, response);
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
            UserUtil.search(this, token, keyword, expertCategory, limit, offset, verifed, new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String reponse, Result result) {

                    if (newLoad) {
                        listExpert.setLayoutManager(new LinearLayoutManager(SelectCategoryHasSearchActivity.this));
                        users = VolleyUtils.getListUserFromJsonNotMe(SelectCategoryHasSearchActivity.this, reponse);
//                    listExpert.setAdapter(new ExpertAdapter(AskExpertActivity.this, users));
                        isLoading = false;
                        setData(users);

                    } else {
                        List<User> newUser = VolleyUtils.getListUserFromJsonNotMe(SelectCategoryHasSearchActivity.this, reponse);
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
        listExpert.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    int sizeCurrent = PAGE_LOAD*LOAD_ITEM_EACH;

                    getExpert(false, token, keyword, sizeCurrent, LOAD_ITEM_EACH);
                    isLoading = true;
//                    load.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
