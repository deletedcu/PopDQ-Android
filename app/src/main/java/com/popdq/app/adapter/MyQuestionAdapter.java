package com.popdq.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.User;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.util.DateUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class MyQuestionAdapter extends RecyclerView.Adapter<MyQuestionAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;


    private Context context;
    private List<Question> questions;
    private String textPending, textExpired, textViews, textAsnwer, textRejected;
    private String help, solved, expired;
    private int colorTextHelp, colorTextSolved;

    private boolean isMyAnswer = false;

    public List<Question> getQuestions() {
        return questions;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MyQuestionAdapter(Context context, List<Question> questions, boolean isMyAnswer) {
        this.context = context;
        this.questions = questions;
        if (context == null || questions == null) return;
        textViews = context.getString(R.string.views);
        textPending = context.getString(R.string.pending);
        textExpired = context.getString(R.string.expired);
        textAsnwer = context.getString(R.string.answertext);
        help = context.getString(R.string.help);
        solved = context.getString(R.string.solved);
        expired = context.getString(R.string.expired);
        textRejected = context.getString(R.string.reject);
        colorTextHelp = Utils.getColor(context, R.color.white);
        colorTextSolved = Utils.getColor(context, R.color.text_solved_static);

        this.isMyAnswer = isMyAnswer;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextViewBold tvTitle;
        CardView root;
        LinearLayout layoutStatus;
        TextViewNormal tvNameUserAnswer, tvStatus;
        TextViewThin tvDate;
        ImageView imgMethod;
        ImageView imgVerified;
        TextViewNormal popBy;
        CircleImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.root);
            layoutStatus = (LinearLayout) itemView.findViewById(R.id.layoutStatus);
            tvTitle = (TextViewBold) itemView.findViewById(R.id.tvTitle);
            tvNameUserAnswer = (TextViewNormal) itemView.findViewById(R.id.tvNameUserAnswer);
            tvStatus = (TextViewNormal) itemView.findViewById(R.id.tvStatus);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            imgMethod = (ImageView) itemView.findViewById(R.id.imgMethod);
            imgVerified = (ImageView) itemView.findViewById(R.id.imgVerified);
            tvDate = (TextViewThin) itemView.findViewById(R.id.tvDate);
            popBy = (TextViewNormal) itemView.findViewById(R.id.popBy);
            if (isMyAnswer) {
                popBy.setText(context.getString(R.string.pop_by));
            } else popBy.setText(context.getString(R.string.pop_to));


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_question__my_question, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Question question = questions.get(position);
        holder.tvTitle.setText(question.getTitle());
        holder.imgMethod.setImageResource(Utils.getMethodIconWhite(question.getMethod()));
        if (question.getStatus() == 2) {
            holder.tvDate.setVisibility(View.GONE);
        } else {
            String date = context.getString(R.string.timeleft) + " " + DateUtil.getLeftTime(context, Locale.getDefault(), question.getCreated_timestamp());
            if (question.getStatus() == 3 || question.getStatus() == 5) {
                date = context.getString(R.string.timeleft) + " " + "00:00";
            }
            holder.tvDate.setText(date);
            holder.tvDate.setVisibility(View.VISIBLE);
        }
        int status = question.getStatus();
        if (status == 2) {
            holder.tvStatus.setText(question.getTotal_view() + " " + textViews);
            holder.layoutStatus.setBackgroundResource(R.drawable.bg_status_question_solved);
//            holder.layoutStatus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (question.getTotal_view() > 0) {
//                        Intent intent1 = new Intent(context, ListViewerActivity.class);
//                        intent1.putExtra(question_id, question.getId());
//                        context.startActivity(intent1);
//                    }
//                }
//            });

        } else if (status == 3) {
            holder.tvStatus.setText(textExpired);
            holder.layoutStatus.setBackgroundResource(R.drawable.bg_status_question_solved);
        } else if (status == 1 || status ==4) {
            if (isMyAnswer) {
                holder.tvStatus.setText(textAsnwer);
                holder.layoutStatus.setBackgroundResource(R.drawable.bg_status_question_green);
            } else {
                holder.tvStatus.setText(textPending);
                holder.layoutStatus.setBackgroundResource(R.drawable.bg_status_question_solved);
            }
        } else if (status == 5) {
            holder.tvStatus.setText(textRejected);
            holder.layoutStatus.setBackgroundResource(R.drawable.bg_status_question_solved);
        }
        String displayName;
        String avatar;
        User user = null;
        if (isMyAnswer) {
            user = question.getUser();
            displayName = question.getUser().getDisplayName();
            avatar = Values.BASE_URL_AVATAR + question.getUser().getAvatar();
        } else {
            user = question.getUser_answer();
            displayName = question.getUser_answer().getDisplayName();
            avatar = Values.BASE_URL_AVATAR + question.getUser_answer().getAvatar();
        }
        user.setAvatar(context, holder.imgAvatar, holder.imgVerified);
        holder.tvNameUserAnswer.setText(displayName);
        final User finalUser = user;
        holder.tvNameUserAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);

//                if (isMyAnswer)
                intent.putExtra(Values.experts_id, finalUser.getId());
                context.startActivity(intent);

//                else intent.putExtra(Values.experts_id, question.getUser_answer().getId());
            }
        });

//        Glide.with(context).load(avatar).placeholder(R.drawable.list_avatar).dontAnimate().into(holder.imgAvatar);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                }
            }
        });
        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, finalUser.getId());

//                if (isMyAnswer)
//                    intent.putExtra(Values.experts_id, question.getUser().getId());
//                else intent.putExtra(Values.experts_id, question.getUser_answer().getId());

                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return questions.size();
    }
}
