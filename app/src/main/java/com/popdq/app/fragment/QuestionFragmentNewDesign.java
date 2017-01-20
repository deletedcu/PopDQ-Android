package com.popdq.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.MyQuestionAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ContentQuestionActivity;
import com.popdq.app.ui.SignupActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.List;

/**
 * Created by Dang Luu on 18/07/2016.
 */
public class QuestionFragmentNewDesign extends Fragment {
    public int action;
    private RecyclerView listQuestion;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout load;
    private MyQuestionAdapter adapter;
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
    private boolean isMyAnswer;

    private RelativeLayout layoutSearchBar;
    public boolean isHideSearchBar = true;
    private TextViewNormal btnCancel;


    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMyAnswer() {
        return isMyAnswer;
    }

    public void setMyAnswer(boolean myAnswer) {
        isMyAnswer = myAnswer;
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
        load = (LinearLayout) view.findViewById(R.id.load);
        layoutSearchBar = (RelativeLayout) view.findViewById(R.id.layoutSearchBar);
        if (isHideSearchBar) {
            layoutSearchBar.setVisibility(View.GONE);
        } else {
            layoutSearchBar.setVisibility(View.VISIBLE);
        }
        tvNodata = (TextViewNormal) view.findViewById(R.id.tvNodata);
//        Utils.setActionEnterSearch(getActivity(), edtSearch);

        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sv.scrollTo(0, sv.getMaxScrollAmount());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyBoard(getActivity());
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
                    keyword = edtSearch.getText().toString();
                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
                } else if (charSequence.length() == 0) {
                    keyword = "";
                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
                }
//                if (charSequence.length() > 0) {
//                    btnCancel.setVisibility(View.VISIBLE);
//                } else {
//                    btnCancel.setVisibility(View.GONE);
//                }

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
        btnCancel = (TextViewNormal) view.findViewById(R.id.btnCancel);
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
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
//                swipeRefreshLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 2000);
//            }
//        });

        getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
        return view;
    }

    public void hideSearchBar() {
        layoutSearchBar.setVisibility(View.GONE);
    }

    public void getQuestion(final boolean newLoad, String token, final String keyword, String tag, int limit, int offset) {
        try {
            if (isHideSearchBar) {
                layoutSearchBar.setVisibility(View.GONE);
            } else {
                layoutSearchBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }

        String status = "1";
        if (isMyAnswer) status = "";
        QuestionUtil.search(url, getActivity(), token, keyword, tag, status
                , limit, offset, new VolleyUtils.OnRequestListenner() {
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
                        Log.e("LOG ANSWERED QUESTION", question.getTitle());
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

        adapter = new MyQuestionAdapter(getActivity(), data, isMyAnswer);

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

        listQuestion.setAdapter(adapter);
        listQuestion.setNestedScrollingEnabled(true);
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
        }
    }
}