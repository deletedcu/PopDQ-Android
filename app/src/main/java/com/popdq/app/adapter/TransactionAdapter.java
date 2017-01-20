package com.popdq.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.model.Transaction;
import com.popdq.app.ui.ContentQuestionActivity;
import com.popdq.app.util.DateUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewThin;

import java.util.List;

/**
 * Created by Dang Luu on 9/6/2016.
 */
public class TransactionAdapter extends RecyclerView.Adapter {
    private List<Transaction> transactions;
    private Context context;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        this.context = context;
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        public TextViewThin tvNameTransaction, tvDate, tvTransaction;
        public LinearLayout root;

        public TransactionHolder(View itemView) {
            super(itemView);
            tvNameTransaction = (TextViewThin) itemView.findViewById(R.id.tvNameTransaction);
            tvDate = (TextViewThin) itemView.findViewById(R.id.tvDate);
            tvTransaction = (TextViewThin) itemView.findViewById(R.id.tvTransaction);
            root = (LinearLayout) itemView.findViewById(R.id.root);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_transaction, null);
        TransactionHolder transactionHolder = new TransactionHolder(view);
        return transactionHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TransactionHolder transactionHolder = (TransactionHolder) holder;
        final Transaction transaction = transactions.get(position);
        int type = transaction.getType();
        StringBuilder stringBuilderCredit = new StringBuilder(transaction.getCredit_received()+"");
        if (stringBuilderCredit.charAt(0) == '-') {
            stringBuilderCredit.insert(1, "$ ");
        } else {
            stringBuilderCredit.insert(0, "+$ ");

        }
        if (type == 3 || type == 4 || type == 5 || type == 6 || type == 7) {
            transactionHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ContentQuestionActivity.class);
                    long id = transaction.getQuestion_id();
                    intent.putExtra(Values.question_id, id);
                    context.startActivity(intent);
                }
            });
        }

        transactionHolder.tvTransaction.setText(stringBuilderCredit.toString() + "");
//        String date = DateUtil.getBriefRelativeTimeSpanString(context, Locale.getDefault(), transaction.getCreated_timestamp());
        String date = DateUtil.getTimeAgo(transaction.getCreated_timestamp(),context);
        transactionHolder.tvDate.setText(date);
        String name = "";

        if (transaction.getType() == 2) {
            name = String.format(context.getString(R.string.noti_credited), "$" + transaction.getCredit());
        } else if (transaction.getType() == 3) {
            name = String.format(context.getString(R.string.transaction_type_view_answer), transaction.getUser_from().getDisplayName());
        } else if (transaction.getType() == 4) {
            try {
                name = String.format(context.getString(R.string.transaction_type_answer_has_viewer), transaction.getUser_from().getDisplayName());
            } catch (Exception e) {

            }
        } else if (transaction.getType() == 5) {
            name = String.format(context.getString(R.string.transaction_type_answer_question), transaction.getUser_from().getDisplayName());
        }
        if (transaction.getType() == 6) {
            name = String.format(context.getString(R.string.transaction_type_ask), transaction.getUser_from().getDisplayName());
        }
        if (transaction.getType() == 7) {
            name = context.getString(R.string.transaction_type_reject);
        }
        if (transaction.getType() == 8) {
            name = context.getString(R.string.transaction_type_withdraw);
        }
        if (transaction.getType() == 9) {
            name = context.getString(R.string.transaction_type_convert);
        }
        transactionHolder.tvNameTransaction.setText(name);

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
