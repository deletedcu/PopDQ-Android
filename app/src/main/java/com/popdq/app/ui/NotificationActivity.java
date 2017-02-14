package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.adapter.NotificationAdapter;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Notification;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

import java.util.List;

public class NotificationActivity extends BaseActivity implements OnItemClickListener {
    private RecyclerView listNotification;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notifications;
    private LinearLayout load;
    public int LOAD_ITEM_EACH = 10;
    private String url;
    public boolean isLoading;
    public int visibleThreshold = 1;
    public String token;
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        token = PreferenceUtil.getToken(this);
        user = User.getInstance(this);
        listNotification = (RecyclerView) findViewById(R.id.listNotification);
        load = (LinearLayout) findViewById(R.id.load);
        Utils.setActionBar(this, getString(R.string.notifications), R.drawable.btn_back);
        getData(true, 0, LOAD_ITEM_EACH);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getData(true, 0, LOAD_ITEM_EACH);

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        NotificationUtil.readAllNotification(this, token, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (result.getCode() == 0) {
                    Intent intent1 = new Intent("update_unread");
                    LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent1);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getData(final boolean newLoad, int limit, int offset) {
        NotificationUtil.getNotifications(this, token, limit, offset, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        load.setVisibility(View.GONE);

                        Log.e("NOTIF ERROR", String.valueOf(notifications));

                        if (newLoad) {
                            notifications = VolleyUtils.getListModelFromRespone(response, Notification.class);
                            setData();
                            isLoading = false;

                        } else {
                            List<Notification> newNotifications = VolleyUtils.getListModelFromRespone(response, Notification.class);

                            for (Notification notification : newNotifications) {
                                notificationAdapter.getNotifications().add(notification);
                            }
                            if (newNotifications.size() > 0) {
                                isLoading = false;
                                notificationAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                }
        );


    }

    public void setData() {
        notificationAdapter = new NotificationAdapter(this, notifications);
        listNotification.clearOnScrollListeners();
        listNotification.setLayoutManager(new LinearLayoutManager(this));
        listNotification.setAdapter(notificationAdapter);
        notificationAdapter.setOnItemClickListener(this);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listNotification.getLayoutManager();
        listNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    int sizeCurrent = notificationAdapter.getItemCount();
                    getData(false, sizeCurrent, LOAD_ITEM_EACH);
                    isLoading = true;
                    load.setVisibility(View.VISIBLE);
                }
            }
        });
//        listNotification.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                notificationAdapter.notifyDataSetChanged();
//            }
//        },200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == ContentQuestionActivity.RECORD_REQUEST_CODE_VISUAL) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                notificationAdapter.viewQuestion();
//            }
//        }
    }


    @Override
    public void onClick(int position) {
        Question question = notificationAdapter.getNotifications().get(position).getContent().getQuestion();
        int type = notificationAdapter.getNotifications().get(position).getType();
//        Toast.makeText(this,String.valueOf(notificationAdapter.getNotifications().get(position).getId()),Toast.LENGTH_LONG).show();

//        if (question.getMethod() == 2) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        ContentQuestionActivity.RECORD_REQUEST_CODE_VISUAL);
//                return;
//            }
//        }
//        String name;
        User user;
        if (notificationAdapter.getNotifications().get(position).getContent().getUser() != null) {
            user = notificationAdapter.getNotifications().get(position).getContent().getUser();
//                    name = notificationAdapter.getNotifications().get(position).getContent().getUser().getDisplayName();
        }
        else {
            user = notificationAdapter.getNotifications().get(position).getContent().getMyInfo();
//            name = notificationAdapter.getNotifications().get(position).getContent().getMyInfo().getDisplayName();
        }

        Intent intent;
        if(type == 5){
             intent = null;
        } else {

             intent = NotificationUtil.getIntentNotificationBar(this, type, question, user);
        }



        if (intent != null) {

            if (notificationAdapter.getNotifications().get(position).getIs_read() == 0) {
                intent.putExtra(Values.FROM_NOTIFICATION_BAR, true);
                intent.putExtra(Values.NOTIFICATION_ID, notificationAdapter.getNotifications().get(position).getId());
                notificationAdapter.getNotifications().get(position).setIs_read(1);
                notificationAdapter.notifyDataSetChanged();


            }
            startActivity(intent);
        } else {
            Toast.makeText(this, "Welcome to PopDQ !",Toast.LENGTH_LONG).show();
        }

    }
}
