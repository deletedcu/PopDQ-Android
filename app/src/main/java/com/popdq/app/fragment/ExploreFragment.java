package com.popdq.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.popdq.app.HidingScrollListener;
import com.popdq.app.MainActivity;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.adapter.ExploreAdapter;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.AskExpertActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dang Luu on 9/15/2016.
 */
public class ExploreFragment extends Fragment {
    private static final int LOAD_ITEM_EACH = 10;
    private static final String TAG = "ExploreFragment";
    private RecyclerView listExpert;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<User> users;
    private EditText edtSearch;
    private String token;
    private String keyword = "";
    boolean isLoading;
    private ExploreAdapter adapter;
    public int visibleThreshold = 1;
    private TextViewNormal tvNodata;
    private String verifed = "1";
    private TextViewNormal layoutTitle;
    private LinearLayout layout_header;

    CallbackManager callbackManager;
    private TextViewNormal btnCancel;
    private static int PAGE_LOAD;
    public static boolean isHideHeader;
    private Timer timer = new Timer();
    private final long DELAY = 2000; // milliseconds
    private MainActivity mainActivity;
    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        PAGE_LOAD = 0;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_explore_fragment, container, false);
        this.view=view;
        listExpert = (RecyclerView) view.findViewById(R.id.listExpert);

        listExpert.setLayoutManager(new LinearLayoutManager(getActivity()));
        listExpert = (RecyclerView) view.findViewById(R.id.listExpert);
        edtSearch = ((EditText) view.findViewById(R.id.edtSearch));
        edtSearch.setTypeface(MyApplication.getInstanceTypeNormal(getActivity()));
        tvNodata = (TextViewNormal) view.findViewById(R.id.tvNodata);
        layout_header = (LinearLayout) view.findViewById(R.id.layout_header);
        edtSearch.setHint(getString(R.string.hint_search_users));
        token = PreferenceUtil.getToken(getActivity());
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword = edtSearch.getText().toString();
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);

                            }
                        },
                        DELAY
                );
                if (charSequence.length() > 0) {
//                    if (adapter != null) {
                    isHideHeader = true;
//                        adapter.hideLayoutHeader(true);
//                    }
                } else {
                    isHideHeader = false;

//                    if (adapter != null) {
//                        adapter.hideLayoutHeader(false);
//                    }
                }

//                if (charSequence.length() >= 1) {
//                    keyword = edtSearch.getText().toString();
//                    getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);
//                } else if (charSequence.length() == 0) {
//                    keyword = "";
//                    getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);
//                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

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

//        Utils.setActionEnterSearch(getActivity(), edtSearch);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
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
                }, 300);
            }
        });
        getExpert(true, token, keyword, 0, LOAD_ITEM_EACH);


        return view;
    }

    public void getExpert(final boolean newLoad, String token, final String keyword, int limit, int offset) {
        PAGE_LOAD++;
        if (keyword.equals("")) {
            verifed = "1";
        } else verifed = "";
        UserUtil.search(getActivity(), token, keyword, "", limit, offset, verifed, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                if (keyword.length() > 3)
                    MixPanelUtil.trackSearchKeyword(getActivity(), keyword);
//                MixpanelAPI.getInstance(getActivity(), Values.PROJECT_TOKEN_MIXPANEL).track(Values.TRACK_SEARCH_KEYWORD);

                if (newLoad) {
//                    listExpert.setLayoutManager(new LinearLayoutManager(getActivity()));
                    if (keyword.equals("")) {
                        users = VolleyUtils.getListUserFromJson(reponse);
                    } else
                        users = VolleyUtils.getListUserFromJsonNotMe(getActivity(), reponse);

//                    users = VolleyUtils.getListUserFromJsonNotMe(getActivity(), reponse);
                    isLoading = false;
                    setData(users);

                } else {
                    List<User> newUser;
                    if (keyword.equals("")) {
                        newUser = VolleyUtils.getListUserFromJson(reponse);
                    } else
                        newUser = VolleyUtils.getListUserFromJsonNotMe(getActivity(), reponse);

//                    = VolleyUtils.getListUserFromJsonNotMe(getActivity(), reponse);
                    for (User user : newUser) {
                        adapter.getUsers().add(user);
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

    public void setEmptySearchBar() {
        if (edtSearch != null && edtSearch.getText().toString().length() > 0)
            edtSearch.setText("");
    }

    public void setData(final List<User> data) {
        if (data == null || data.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }

//        for (User user : data) {
//            Log.e("LOG DESC", user.getDescription());
//        }

        adapter = new ExploreAdapter(getActivity(), data);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFbFriend();
            }
        });
        listExpert.clearOnScrollListeners();

        listExpert.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listExpert.getLayoutManager();
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);

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
                }
            }
        });
        try {
            listExpert.addOnScrollListener(new HidingScrollListener((int) Utils.pxFromDp(getActivity(), 16 + 100)) {
                @Override
                public void onMoved(int distance) {
                    Log.e(TAG, distance + "");
                    layout_header.setTranslationY(-distance);
                }
            });
        } catch (Exception e) {

        }
    }

    public void clickFbFriend() {

        String tokenfb = PreferenceUtil.getTokenFb(getActivity());
        if (tokenfb == null || tokenfb.equals("")) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.e(TAG, "onSuccess");
                    String tokenFb = AccessToken.getCurrentAccessToken().getToken();
                    PreferenceUtil.getInstanceEditor(getActivity()).putString(Values.fbToken, tokenFb);
                    PreferenceUtil.getInstanceEditor(getActivity()).commit();
                    Intent intent = new Intent(getActivity(), AskExpertActivity.class);
                    intent.putExtra(Values.fbToken, tokenFb);
                    startActivity(intent);
                    LoginManager.getInstance().logOut();
                }

                @Override
                public void onCancel() {
                    LoginManager.getInstance().logOut();
                    Log.e(TAG, "onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, "onError: " + error.toString());
                }
            });
            List<String> permissionNeeds = Arrays.asList(new String[]{"public_profile", "user_friends", "email"});
            LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                    permissionNeeds);
        } else {
            Intent intent = new Intent(getActivity(), AskExpertActivity.class);
            intent.putExtra(Values.fbToken, tokenfb);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }catch (Exception e){

        }
    }

}
