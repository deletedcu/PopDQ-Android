package com.popdq.app.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.popdq.app.R;
import com.popdq.app.adapter.FavoriteAdapter;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.FavoriteModel;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.List;

public class FavoriteListActivity extends BaseActivity {

    private RecyclerView listExperts;
    private FavoriteAdapter adapter;
    private List<FavoriteModel> favoriteModels;
    private TextViewNormal tvNodata;
    private EditText edtSearch;
    private boolean isMyFavorite;
    private String user_id;
    private String favorite;
    public boolean isLoading;
    private int visibleThreshold = 1;
    public int LOAD_ITEM_EACH = 10;
    public String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        Utils.setActionBar(this, getString(R.string.favorite_list), R.drawable.btn_back);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtSearch.setHint(getString(R.string.hint_search_users));
//        Utils.setActionEnterSearch(this, edtSearch);

        listExperts = (RecyclerView) findViewById(R.id.list);
        tvNodata = (TextViewNormal) findViewById(R.id.tvNodata);
        listExperts.setLayoutManager(new LinearLayoutManager(this));

        user_id = getIntent().getExtras().getString(Values.user_id, "");
        favorite = getIntent().getExtras().getString(Values.favorite, "");
        if (!user_id.equals("") && user_id.equals(User.getInstance(this).getId() + "")) {
            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.favorite_list));
        } else if (user_id.equals("")) {
            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.follower_up));
        } else {
            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.following_up));

        }

        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(FavoriteListActivity.this);
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
                    name = edtSearch.getText().toString();
                    getData(true, 0, LOAD_ITEM_EACH);
//                } else if (charSequence.length() == 0) {
//                    name = "";
//                    getData(true, 0, LOAD_ITEM_EACH);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getData(true, 0, LOAD_ITEM_EACH);


    }

    private void getData(final boolean newLoad, int limit, int offset) {

        FavoriteUtils.search(this, PreferenceUtil.getToken(this), user_id, favorite, name, limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (newLoad) {
                    favoriteModels = FavoriteUtils.getListFavoriteFromJson(response);
                    setData();
                    isLoading = false;


                } else {
                    List<FavoriteModel> newList = FavoriteUtils.getListFavoriteFromJson(response);
                    for (FavoriteModel favoriteModel : newList) {
                        adapter.getFavoriteModels().add(favoriteModel);
                    }


                    if (newList.size() > 0) {
                        isLoading = false;
                        adapter.notifyDataSetChanged();
                    }
                }


            }

            @Override
            public void onError(String error) {

            }
        });


//        userFavorites = new ArrayList<>();
//        FavoriteModel userFavorite = new FavoriteModel();
//        userFavorite.setUsername("McDonalds");
//        userFavorite.setCountFavorite(123);
//        userFavorites.add(userFavorite);
//
//        FavoriteModel userFavorite2 = new FavoriteModel();
//        userFavorite2.setUsername("StarBucks");
//        userFavorite2.setCountFavorite(1553);
//        userFavorites.add(userFavorite2);
//
//        FavoriteModel userFavorite3 = new FavoriteModel();
//        userFavorite3.setUsername("ABC Company");
//        userFavorite3.setCountFavorite(23);
//        userFavorites.add(userFavorite3);
//
//        FavoriteModel userFavorite4 = new FavoriteModel();
//        userFavorite4.setUsername("Amazon");
//        userFavorite4.setCountFavorite(153);
//        userFavorites.add(userFavorite4);

    }

    public void setData() {
        if (user_id.equals("")) {
            adapter = new FavoriteAdapter(FavoriteListActivity.this, favoriteModels, false);
        } else if (favorite.equals("")) {
            adapter = new FavoriteAdapter(FavoriteListActivity.this, favoriteModels, true);
        }
        listExperts.setAdapter(adapter);
        if (favoriteModels == null || favoriteModels.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }
        listExperts.clearOnScrollListeners();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listExperts.getLayoutManager();
        listExperts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    int sizeCurrent = adapter.getItemCount();
                    getData(false, sizeCurrent, LOAD_ITEM_EACH);
                    isLoading = true;
                }
            }
        });
    }


}
