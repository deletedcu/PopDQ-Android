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

import com.popdq.app.HidingScrollListener;
import com.popdq.app.MainActivity;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.QuestionFeedAdapter;
import com.popdq.app.connection.QuestionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ContentQuestionActivity;
import com.popdq.app.ui.EditProfileActivity;
import com.popdq.app.ui.LoginWithEmailActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dang Luu on 18/07/2016.
 */
public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";
    public int action;
    private RecyclerView listQuestion;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutBottom;
    private LinearLayout load;
    private QuestionFeedAdapter adapter;
    private List<Question> questions;
    private EditText edtSearch;
    //    private LinearLayout layoutSearch;
    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    public String token;
    private User user;
    String keyword = "";
    private View view;
    private TextViewNormal tvNodata;
    private RelativeLayout noticeIncompleteInfo;
    private BroadcastReceiver broadcastReceiver;
    private int positionClicked;
    private TextViewNormal btnCancel;
    private MainActivity mainActivity;
    //    private Button btnBottom;
//    private LinearLayout btnSearchByArea;
    private BroadcastReceiver changeInterestBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);

        }
    };

    private LinearLayout layout_header;

    private Timer timer = new Timer();
    private final long DELAY = 2000; // milliseconds


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.adapter = new QuestionFeedAdapter(getContext());
        user = User.getInstance(getActivity());
        token = PreferenceUtil.getInstancePreference(getActivity()).getString(Values.TOKEN, "");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    int currentTotalView = adapter.getQuestions().get(positionClicked).getTotal_view();
                    int newTotal = intent.getIntExtra("total_views", currentTotalView);
                    adapter.getQuestions().get(positionClicked).setTotal_view(newTotal);
                    adapter.getQuestions().get(positionClicked).setViewed(true);

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("update_views");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter changeInterest = new IntentFilter("update_interest");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(changeInterestBroadcast, changeInterest);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(changeInterestBroadcast);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
//        layoutSearch = (LinearLayout) view.findViewById(R.id.layoutSearch);

        listQuestion = (RecyclerView) view.findViewById(R.id.list);
        listQuestion.setLayoutManager(new LinearLayoutManager(getActivity()));
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(getContext()));
        layoutBottom = ((LinearLayout) view.findViewById(R.id.bottom));
        layout_header = ((LinearLayout) view.findViewById(R.id.layout_header));
        load = (LinearLayout) view.findViewById(R.id.load);
        tvNodata = (TextViewNormal) view.findViewById(R.id.tvNodata);
        noticeIncompleteInfo = (RelativeLayout) view.findViewById(R.id.noticeIncompleteInfo);
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            mainActivity.hideBtnAsk(false);
        }


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

//        Utils.setActionEnterSearch(getActivity(), edtSearch);
//        if (user.getCategories() == null || user.getCategories().length <= 0 || user.getAddress().length() <= 3 || user.getDescription().length() <= 3) {
//            noticeIncompleteInfo.setVisibility(View.VISIBLE);
//        } else {
//            noticeIncompleteInfo.setVisibility(View.GONE);
//        }

        try{
            if (user.getProfessional_field() == null||user.getAddress() == null ||user.getDescription() == null||
                    user.getProfessional_field().length() <= 1||
                    user.getAddress().length() <= 1 ||
                    user.getDescription().length() <= 1  ) {
                noticeIncompleteInfo.setVisibility(View.VISIBLE);
            } else {
                noticeIncompleteInfo.setVisibility(View.GONE);
            }

        } catch (NullPointerException e){
            e.printStackTrace();
        }


        ((RelativeLayout) view.findViewById(R.id.btnCloseNotice)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeIncompleteInfo.setVisibility(View.GONE);
                setHeightSpace();
            }
        });
        noticeIncompleteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeIncompleteInfo.setVisibility(View.GONE);
                setHeightSpace();
                startActivity(new Intent(getActivity(), EditProfileActivity.class
                ));
            }
        });

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

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                adapter.edit_search(keyword);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword = edtSearch.getText().toString();

                if(keyword == null || keyword.equals("")){
                    adapter.edit_search("");
                } else{
                    adapter.edit_search(keyword);
                }

                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
                            }
                        },
                        DELAY
                );
//
//                if (charSequence.length() >= 1) {
//                    keyword = edtSearch.getText().toString();
////                    btnBottom.setVisibility(View.GONE);
//                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
////                    adapter.visibleTitleWhenSearch(true);
//                } else if (charSequence.length() == 0) {
//                    keyword = "";
//                    getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
////                    btnBottom.setVisibility(View.GONE);
////                    adapter.visibleTitleWhenSearch(false);
////                    btnSearchByArea.setVisibility(View.VISIBLE);
//                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    btnCancel.setVisibility(View.VISIBLE);
                    if (mainActivity != null)
                        mainActivity.hideBtnAsk(true);

                } else {
                    if (mainActivity != null)
                        mainActivity.hideBtnAsk(false);
                    if (keyword.equals(""))
                        btnCancel.setVisibility(View.GONE);

//                    Utils.hideKeyBoard(getActivity());

                }

            }
        });
//        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    btnBottom.setVisibility(View.GONE);
////                    btnSearchByArea.setVisibility(View.VISIBLE);
////                } else if (view.getId() == btnSearchByArea.getId()) {
//
//                } else
//
//                    btnBottom.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            btnBottom.setVisibility(View.VISIBLE);
//                        }
//                    }, 300);
//            }
//        });


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
                }, 300);
            }
        });
        getQuestion(true, token, keyword, "", 0, LOAD_ITEM_EACH);
        return view;
    }

    public void getQuestion(final boolean newLoad, String token, final String keyword, String tag, int limit, int offset) {
        if (url == null) url = Values.URL_QUESTION_SEARCH;
        QuestionUtil.search(url, getActivity(), token, keyword, tag, "2", limit, offset, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                if (keyword.length() > 3)
                    MixPanelUtil.trackSearchKeyword(getActivity(), keyword);
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

    public void setEmptySearchBar() {

        if (edtSearch != null && edtSearch.getText().toString().length() > 0)
            edtSearch.setText("");

    }

    public void setData(final List<Question> data) {


        if (data == null || questions.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }

        adapter = new QuestionFeedAdapter(getActivity(), data, new ArrayList<User>());

        Log.e(TAG+"adapter", String.valueOf(adapter));
        Log.e(TAG+"keyword", String.valueOf(keyword));

        if (adapter != null) {
            adapter.searchUser(keyword);
        }


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                positionClicked = position;
                Intent intent = new Intent(getActivity(), ContentQuestionActivity.class);
                long id = data.get(position).getId();
                intent.putExtra(Values.question_id, id);
                startActivity(intent);
            }
        });
        listQuestion.clearOnScrollListeners();
        try {
            listQuestion.addOnScrollListener(new HidingScrollListener((int) Utils.pxFromDp(getActivity(), 16 + 100)) {
                @Override
                public void onMoved(int distance) {
                    Log.e(TAG, distance + "");
                    layout_header.setTranslationY(-distance);
                }
            });
        } catch (Exception e) {

        }
        listQuestion.setAdapter(adapter);
        setHeightSpace();

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
//        layout_header.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        setHeightSpace();
//        edtSearch = (EditText) view.findViewById(R.id.edtSearch);

       try{
           adapter.edit_search(edtSearch.getText().toString());
       } catch (NullPointerException e){
           e.printStackTrace();
       }
    }

//    public String edit_search(){
//        return edtSearch.getText().toString();
//    }

    public void setHeightSpace() {
        layout_header.post(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.setHeaderSpaceHeight(layout_header.getHeight());
                }
            }
        });
    }


//    public class BroadcastReceiverUpdateCount extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int currentTotalView = adapter.getUsers().get(positionClicked).getTotal_view();
//            adapter.getUsers().get(positionClicked).setTotal_view(intent.getIntExtra("total_view", currentTotalView));
//            adapter.notifyDataSetChanged();
//        }
//    }
}
