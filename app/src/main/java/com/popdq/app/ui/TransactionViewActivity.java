package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.popdq.app.R;
import com.popdq.app.adapter.TransactionAdapter;
import com.popdq.app.connection.TransactionUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.Transaction;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.view.TranferCreditPopup;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import java.util.List;

public class TransactionViewActivity extends BaseActivity {
    private RecyclerView listTransaction;
    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private static int ITEM_PER_LOAD = 10;
    private static int CURRENT_ITEM_LOADED = 0;
    private boolean isLoading = false;
    public int visibleThreshold = 1;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_view);
        Utils.checkStartActivityFromNotificationAndSendRead(this, null);

        Utils.setActionBar(this, getString(R.string.title_transaction), R.drawable.btn_back);
        listTransaction = (RecyclerView) findViewById(R.id.listTransaction);
        listTransaction.setLayoutManager(new LinearLayoutManager(TransactionViewActivity.this));

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listTransaction.getLayoutManager();
        listTransaction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = transactionAdapter.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    int sizeCurrent = transactionAdapter.getItemCount();
                    getData(false, sizeCurrent);
                    isLoading = true;
                }
            }
        });

        ((TextViewNormal) findViewById(R.id.tvTotalCreditAvaliable)).setText(getString(R.string.total_credits) + "  $" + String.format("%.2f", User.getInstance(this).getCredit()) + "");
        ((TextViewNormal) findViewById(R.id.tvTotalEarning)).setText(getString(R.string.total_earning) + "  $" + String.format("%.2f", User.getInstance(this).getCredit_earnings()) + "");
        ((TextViewThin) findViewById(R.id.btnWithDraw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionViewActivity.this, WithdrawActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnTransfer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranferCreditPopup.showDialogConvert(TransactionViewActivity.this, new TranferCreditPopup.TranferListener() {
                    @Override
                    public void onSuccess(float credit) {

                    }
                });
            }
        });
        getData(true,0);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getData(true,0);

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public void getData(final boolean isLoadNew, int limit) {
        TransactionUtil.getListTransaction(this, PreferenceUtil.getToken(this), limit, ITEM_PER_LOAD, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                if (isLoadNew) {
                    transactions = VolleyUtils.getListModelFromRespone(response, Transaction.class);
                    transactionAdapter = new TransactionAdapter(TransactionViewActivity.this, transactions);
//                    listTransaction.setLayoutManager(new LinearLayoutManager(TransactionViewActivity.this));
                    listTransaction.setAdapter(transactionAdapter);
                    isLoading = false;
                } else {
                    List<Transaction> list = VolleyUtils.getListModelFromRespone(response, Transaction.class);
                    for (Transaction transaction : list) {
                        transactions.add(transaction);
                    }
                    if (list.size() > 0) {
                        isLoading = false;
                        transactionAdapter.notifyDataSetChanged();
                    }
                }
                CURRENT_ITEM_LOADED = transactions.size();
            }

            @Override
            public void onError(String error) {

            }
        });
    }


}
