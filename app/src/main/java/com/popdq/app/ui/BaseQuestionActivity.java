//package com.azstack.quickanswer.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//
//import com.azstack.quickanswer.HidingScrollListener;
//import com.azstack.quickanswer.R;
//import com.azstack.quickanswer.adapter.QuestionAdapter;
//import com.azstack.quickanswer.connection.QuestionUtil;
//import com.azstack.quickanswer.connection.VolleyUtils;
//import com.azstack.quickanswer.interfaces.OnItemClickListener;
//import com.azstack.quickanswer.interfaces.OnLoadToBottomRecycleViewListenner;
//import com.azstack.quickanswer.model.Result;
//import com.azstack.quickanswer.model.User;
//import com.azstack.quickanswer.model.Question;
//import com.azstack.quickanswer.util.Utils;
//import com.azstack.quickanswer.values.Values;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BaseQuestionActivity extends AppCompatActivity {
//    private RecyclerView listQuestion;
//    private LinearLayout layoutBottom;
//    private QuestionAdapter adapter;
//    private List<Question> questions;
//    private List<Question> questionCurrent;
//    private Toolbar toolbar;
//    private String token;
//    private User user;
//    private EditText edtSearch;
//    public int LOAD_ITEM_EACH = 2;
//    private String url;
//    public boolean isLoading;
//    public int visibleThreshold = 1;
//    public int lastVisibleItem, totalItemCount;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base_question);
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void initView(){
//        edtSearch = (EditText) findViewById(R.id.edtSearch);
//        listQuestion = (RecyclerView) findViewById(R.id.list);
//        listQuestion.setLayoutManager(new LinearLayoutManager(this));
//        layoutBottom = ((LinearLayout) findViewById(R.id.bottom));
//        edtSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() >= 3) {
//                    searchQuestion(true, token, edtSearch.getText().toString(), "", 0, LOAD_ITEM_EACH);
//                } else {
//                    setData(questions);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//    }
//
//    public void setData(final List<Question> data) {
//        adapter = new QuestionAdapter(BaseQuestionActivity.this, data);
//        adapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onClick(int position) {
//                Intent intent = new Intent(BaseQuestionActivity.this, ContentQuestionActivity.class);
//                long id = data.get(position).getId();
//                intent.putExtra(Values.question_id, id);
//                startActivity(intent);
//
//            }
//        });
//
//        adapter.setOnLoadToBottomRecycleViewListenner(new OnLoadToBottomRecycleViewListenner() {
//            @Override
//            public void onLoadToBottom() {
//
//
//            }
//        });
//        listQuestion.addOnScrollListener(new HidingScrollListener((int) Utils.pxFromDp(this, 16 + 72)) {
//            @Override
//            public void onMoved(int distance) {
//                layoutBottom.setTranslationY(distance);
//            }
//        });
//        listQuestion.setAdapter(adapter);
//        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listQuestion.getLayoutManager();
//        listQuestion.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int totalItemCount = linearLayoutManager.getItemCount();
//                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//
//                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                    String key = edtSearch.getText().toString();
//                    int sizeCurrent = adapter.getItemCount();
//                    if (key.length() >= 3) {
//
//                        searchQuestion(false, token, key, "", sizeCurrent, LOAD_ITEM_EACH);
//                    } else {
//                        searchQuestion(false, token, "", "", sizeCurrent, LOAD_ITEM_EACH);
//                    }
//                    isLoading = true;
//                }
//            }
//        });
//    }
//    public void searchQuestion(final boolean searchNew, String token, final String keyword, String tag, int limit, int offset) {
//        questionCurrent = new ArrayList<>();
//        QuestionUtil.search(url, this, token, keyword, tag, limit, offset, new VolleyUtils.OnRequestListenner() {
//            @Override
//            public void onSussces(String reponse, Result result) {
//                if (searchNew) {
//                    if (keyword.equals("")) {
//                        questions = questionCurrent = VolleyUtils.getListQuestionFromJson(reponse);
//                    } else {
//                        questionCurrent = VolleyUtils.getListQuestionFromJson(reponse);
//                    }
//                    setData(questionCurrent);
//                } else {
//                    List<Question> newQuestion = VolleyUtils.getListQuestionFromJson(reponse);
//                    for (Question question : newQuestion) {
//                        adapter.getUsers().add(question);
//                    }
//                    if (newQuestion.size() > 0) {
//                        isLoading = false;
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });
//
//    }
//
//}
