package com.popdq.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.popdq.app.R;
import com.popdq.app.adapter.ExpertAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

import java.util.List;

public class ListViewerActivity extends AppCompatActivity {
    private long question_id;
    private RecyclerView listExpert;
    private List<User> users;
    private ExpertAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question_id = getIntent().getLongExtra(Values.question_id, -1);
        setContentView(R.layout.activity_ask_expert);

        ((RelativeLayout)findViewById(R.id.layoutSearchBar)).setVisibility(View.GONE);
        Utils.setActionBar(this, getString(R.string.list_viewer).toUpperCase(), R.drawable.btn_back);
        listExpert = (RecyclerView) findViewById(R.id.listExpert);
        QuestionUtil.getListViewer(this, PreferenceUtil.getToken(this), question_id, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                listExpert.setLayoutManager(new LinearLayoutManager(ListViewerActivity.this));
                users = VolleyUtils.getListUserFromJsonNotMe(ListViewerActivity.this, response);
                setData(users);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void setData(List<User> users) {
        listExpert.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpertAdapter(this, users);
        listExpert.setAdapter(adapter);

    }
}
