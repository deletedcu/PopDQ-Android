package com.popdq.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.popdq.app.R;
import com.popdq.app.connection.CategoryUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.google.gson.Gson;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoryActivity extends BaseActivity {
    private static final String TAG = "MyInterestActivity";
    private ListView listInterest;
    private List<Interest> myInterests;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listInterest = (ListView) findViewById(R.id.listInterest);
        token = sharedPreferences.getString(Values.TOKEN, "");
        Utils.setActionBar(this, getString(R.string.select_category).toUpperCase(), R.drawable.btn_back);
        initData();
    }

    public void initData() {
        myInterests = new ArrayList<>();
        getData();
    }

    public void setDataList() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(SelectCategoryActivity.this, R.layout.item_category_search) {
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
                                                MixPanelUtil.trackCategory(SelectCategoryActivity.this, myInterest);

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
                                                    intent.putExtra(Values.category_name, myInterest.getName());
                                                    intent.putExtra(Values.expertCategory, myInterest.getId() + "");
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
                    myInterests = CategoryUtils.getListFromRespone(SelectCategoryActivity.this, response);
                    setDataList();
                }

                @Override
                public void onError(String error) {

                }
            });
        } else setDataList();


    }
}
