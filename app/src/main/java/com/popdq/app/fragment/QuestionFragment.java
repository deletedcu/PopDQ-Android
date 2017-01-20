package com.popdq.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.QuestionAdapter;
import com.popdq.app.adapter.QuestionNoStatusAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ContentQuestionActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dang Luu on 18/07/2016.
 */
public class QuestionFragment extends Fragment {
    public int action;
    private RecyclerView listQuestion;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutBottom;
    private LinearLayout load;
    private QuestionAdapter adapter;
    private List<Question> questions;
    private EditText edtSearch;
    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    public int lastVisibleItem, totalItemCount;
    public String token;
    private User user;
    String keyword = "";
    private View view;
    private TextViewNormal tvNodata;

    public boolean isHome = false;
    private BroadcastReceiverUpdateCount broadcastReceiverUpdateCount;
    private BroadcastReceiver broadcastReceiver;
    private int positionClicked;


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userS = PreferenceUtil.getInstancePreference(getActivity()).getString(Values.user, "");
        try {
            user = new Gson().fromJson(userS, User.class);
        } catch (Exception e) {

        }

        token = PreferenceUtil.getInstancePreference(getActivity()).getString(Values.TOKEN, "");
//        if (token == null || token.equals("")) {
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//            return;
//        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    int currentTotalView = adapter.getQuestions().get(positionClicked).getTotal_view();
                    int newTotal = intent.getIntExtra("total_views", currentTotalView);
                    adapter.getQuestions().get(positionClicked).setTotal_view(newTotal);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("update_views");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_list_question, container, false);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(getContext()));
        layoutBottom = ((LinearLayout) view.findViewById(R.id.bottom));
        load = (LinearLayout) view.findViewById(R.id.load);
        tvNodata = (TextViewNormal) view.findViewById(R.id.tvNodata);
//        ((Button) view.findViewById(R.id.btnBottom)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), AskExpertActivity.class));
//
//            }
//        });
//        ((Button) view.findViewById(R.id.btnBottom)).setText(getString(R.string.find_out_something));
//        ((Button) view.findViewById(R.id.btnBottom)).setTypeface(MyApplication.getInstanceTypeNormal(getActivity()));
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

        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(getActivity());
//                    login();
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
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

        getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
        return view;
    }

    public void getQuestion(final boolean newLoad, String token, final String keyword, String tag, int limit, int offset) {

        QuestionUtil.search(url, getActivity(), token, keyword, tag, "2", limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                load.setVisibility(View.GONE);
                if (newLoad) {
//                    if (keyword.equals("")) {
                    questions = VolleyUtils.getListQuestionFromJson(reponse);
//                    } else {
//                        questionCurrent = VolleyUtils.getListQuestionFromJson(reponse);
//                    }
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
        listQuestion = (RecyclerView) view.findViewById(R.id.list);
        listQuestion.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (isHome) {
            adapter = new QuestionNoStatusAdapter(getActivity(), data);
        } else {
            adapter = new QuestionAdapter(getActivity(), data);
        }


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                positionClicked = position;
                Question question = data.get(position);
//                if (getActivity() instanceof MyAnswerActivity && question.getStatus() == 2) {
//                    Intent intentViewAnswer = null;
//                    switch (question.getMethod()) {
//                        case 1:
//                            intentViewAnswer = new Intent(getActivity(), ViewAnswerTextActivity.class);
//                            break;
//                        case 2:
//                            intentViewAnswer = new Intent(getActivity(), ViewAnswerVoiceRecordActivity.class);
//                            break;
//                        case 3:
//                            intentViewAnswer = new Intent(getActivity(), ViewAnswerVideoActivity.class);
//                            break;
//                    }
//                    intentViewAnswer.putExtra(Values.question_id, question.getId());
//                    intentViewAnswer.putExtra(Values.title, question.getTitle());
//                    startActivity(intentViewAnswer);
//                } else {
                Intent intent = new Intent(getActivity(), ContentQuestionActivity.class);
                long id = data.get(position).getId();
                intent.putExtra(Values.question_id, id);
                startActivity(intent);
//                }


            }
        });
        listQuestion.clearOnScrollListeners();
//        try {
//            listQuestion.addOnScrollListener(new HidingScrollListener((int) Utils.pxFromDp(getActivity(), 16 + 72)) {
//                @Override
//                public void onMoved(int distance) {
//
//                    layoutBottom.setTranslationY(distance);
//                }
//            });
//        } catch (Exception e) {
//
//        }
        listQuestion.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listQuestion.getLayoutManager();
        listQuestion.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


    public class BroadcastReceiverUpdateCount extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentTotalView = adapter.getQuestions().get(positionClicked).getTotal_view();
            adapter.getQuestions().get(positionClicked).setTotal_view(intent.getIntExtra("total_view", currentTotalView));
            adapter.notifyDataSetChanged();
        }
    }
}
