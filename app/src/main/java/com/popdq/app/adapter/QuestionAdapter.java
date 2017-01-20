package com.popdq.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.popdq.app.R;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.interfaces.OnLoadToBottomRecycleViewListenner;
import com.popdq.app.model.Question;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnLoadToBottomRecycleViewListenner onLoadToBottomRecycleViewListenner;


    private Context context;
    private List<Question> questions;
    private String textReply;
    private String help, solved, reject;
    private int colorTextHelp, colorTextSolved;

    public List<Question> getQuestions() {
        return questions;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }


    public void setOnLoadToBottomRecycleViewListenner(OnLoadToBottomRecycleViewListenner onLoadToBottomRecycleViewListenner) {
        this.onLoadToBottomRecycleViewListenner = onLoadToBottomRecycleViewListenner;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public QuestionAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        if (context == null || questions == null) return;
        textReply = context.getString(R.string.views);
        help = context.getString(R.string.help);
        solved = context.getString(R.string.solved);
        reject = context.getString(R.string.reject);
        colorTextHelp= Utils.getColor(context, R.color.white);
        colorTextSolved= Utils.getColor(context, R.color.text_solved_static);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        TextViewNormal tvTitle, tvCountReply;
        CircleImageView imgAvatar;
        TextViewNormal status;

        public ViewHolder(View itemView) {
            super(itemView);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
            tvTitle = (TextViewNormal) itemView.findViewById(R.id.tvTitle);
            tvCountReply = (TextViewNormal) itemView.findViewById(R.id.tvCountReply);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            status = (TextViewNormal) itemView.findViewById(R.id.status);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_question, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Question question = questions.get(position);
        holder.tvTitle.setText(question.getTitle());
        holder.tvCountReply.setText(question.getTotal_view() + " " + textReply);
        if (question.getStatus() == 2) {
            holder.status.setBackgroundResource(R.drawable.bg_solved);
            holder.status.setText(solved);
            holder.status.setTextColor(colorTextSolved);

//            holder.status.setTextColor(R.drawable.text_color_solved);
        } else if (question.getStatus() == 3) {
            holder.status.setBackgroundResource(R.drawable.bg_solved);
            holder.status.setText(reject);
            holder.status.setTextColor(colorTextSolved);
        } else {
            holder.status.setBackgroundResource(R.drawable.bg_help);
            holder.status.setText(help);
            holder.status.setTextColor(colorTextHelp);

        }
        String a = Values.BASE_URL_AVATAR + question.getUser().getAvatar();
        if (question.getUser_answer() == null) {
            a = Values.BASE_URL_AVATAR + question.getUser().getAvatar();

        } else {
            a = Values.BASE_URL_AVATAR + question.getUser_answer().getAvatar();
        }
        Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holder.imgAvatar);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
//                    question.setTotal_view(question.getTotal_view() + 1);
//                    notifyDataSetChanged();
                }
            }
        });
//        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, ExpertProfileActivity.class);
//                intent.putExtra(Values.experts_id, question.getUser_id());
//                context.startActivity(intent);
//            }
//        });

        if (position == questions.size() - 1) {
            if (onLoadToBottomRecycleViewListenner != null) {
                onLoadToBottomRecycleViewListenner.onLoadToBottom();
            }
        }

    }


    @Override
    public int getItemCount() {
        return questions.size();
    }
}
