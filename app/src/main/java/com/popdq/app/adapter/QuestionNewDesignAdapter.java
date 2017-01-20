package com.popdq.app.adapter;

import android.app.Activity;
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
import com.popdq.app.util.Utils;
import com.popdq.app.util.ViewAnswerCheckCredits;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 9/7/2016.
 */
public class QuestionNewDesignAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private Activity context;
    private List<Question> questions;
    private List<User> users;
    //    private String textReply;
    private String help, solved, reject;
    private String textView, textViews;
    private boolean isPopby;
    private int colorTextWhite, colorTextSolved;

//    private int colorTextHelp, colorTextSolved;

    public List<Question> getQuestions() {
        return questions;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public QuestionNewDesignAdapter(Activity context, List<Question> questions, List<User> users, boolean isPopby) {
        this.context = context;
        this.questions = questions;
        this.users = users;
        if (context == null || questions == null) return;
//        textReply = context.getString(R.string.views);
        help = context.getString(R.string.help);
        solved = context.getString(R.string.solved);
        reject = context.getString(R.string.reject);
        this.isPopby = isPopby;
        colorTextWhite = Utils.getColor(context, R.color.white);
        colorTextSolved = Utils.getColor(context, R.color.text_solved_static);
        textViews = context.getString(R.string.views);
        textView = context.getString(R.string.view_low);

    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        CardView root;
        TextViewNormal tvCountViews;
        TextViewBold tvTitle;
        CircleImageView imgAvatar;
        ImageView icMethod;
        TextViewNormal tvNameUserAnswer, tvStatus;
        LinearLayout layoutView;


        public ItemHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.root);
            tvTitle = (TextViewBold) itemView.findViewById(R.id.tvTitle);
            tvCountViews = (TextViewNormal) itemView.findViewById(R.id.tvCountViews);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            icMethod = (ImageView) itemView.findViewById(R.id.icMethod);
            tvNameUserAnswer = (TextViewNormal) itemView.findViewById(R.id.tvNameUserAnswer);
            tvStatus = (TextViewNormal) itemView.findViewById(R.id.tvStatus);
            layoutView = (LinearLayout) itemView.findViewById(R.id.layoutView);
            if (isPopby)
                ((TextViewNormal) itemView.findViewById(R.id.popBy)).setText(context.getString(R.string.pop_by));

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_question_new_design, null);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder holderItem = (ItemHolder) holder;
        final Question question = questions.get(position);
        holderItem.tvTitle.setText(question.getTitle());
        if (question.getTotal_view() <= 1) {
            holderItem.tvCountViews.setText(question.getTotal_view() + " " + textView);
        } else {
            holderItem.tvCountViews.setText(question.getTotal_view() + " " + textViews);
        }
//        holderItem.tvCountViews.setText(question.getTotal_view() + " " + textReply);
        if (isPopby) {
            holderItem.tvNameUserAnswer.setText(question.getUser().getDisplayName());
            holderItem.tvNameUserAnswer.setSelected(true);
            String a = Values.BASE_URL_AVATAR + question.getUser().getAvatar();
            Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holderItem.imgAvatar);
        } else {
            holderItem.tvNameUserAnswer.setText(question.getUser_answer().getDisplayName());
            holderItem.tvNameUserAnswer.setSelected(true);
            String a = Values.BASE_URL_AVATAR + question.getUser_answer().getAvatar();
            Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holderItem.imgAvatar);
        }
        if (question.isViewed()) {
            holderItem.layoutView.setBackgroundResource(R.drawable.bg_status_viewed);
            holderItem.tvStatus.setTextColor(colorTextSolved);
            holderItem.tvStatus.setText(textView);
            if (question.getMethod() == 1) {
                holderItem.icMethod.setImageResource(R.drawable.ic_text_white);
                holderItem.tvStatus.setText(textView);
            } else if (question.getMethod() == 2) {
                holderItem.icMethod.setImageResource(R.drawable.ic_audio_white);
            } else if (question.getMethod() == 3) {
                holderItem.icMethod.setImageResource(R.drawable.ic_video_white);
            }
        } else {
            holderItem.layoutView.setBackgroundResource(R.drawable.bg_status_question_green);
            holderItem.tvStatus.setTextColor(colorTextWhite);

            if (question.getMethod() == 1) {
                holderItem.icMethod.setImageResource(R.drawable.ic_text_white);
                holderItem.tvStatus.setText("$0.10");
//                if (question.getFree_preview() == 1) {
////                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_text);
//                } else {
//                    holderItem.imgMethod.setImageResource(R.drawable.ic_text);
//                }
            } else if (question.getMethod() == 2) {
                holderItem.icMethod.setImageResource(R.drawable.ic_audio_white);
                holderItem.tvStatus.setText("$0.20");

//                if (question.getFree_preview() == 1) {
//                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_audio);
//                } else {
//                    holderItem.imgMethod.setImageResource(R.drawable.ic_audio);
//                }
            } else if (question.getMethod() == 3) {
                holderItem.icMethod.setImageResource(R.drawable.ic_video_white);
                holderItem.tvStatus.setText("$0.30");

//                if (question.getFree_preview() == 1) {
//                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_video);
//                } else {
//                    holderItem.imgMethod.setImageResource(R.drawable.ic_video);
//                }
            }
            if (question.getFree_preview() == 1) {
                holderItem.tvStatus.setText("FREE");
            }
        }
        ((ItemHolder) holder).imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                if (isPopby) {
                    intent.putExtra(Values.experts_id, question.getUser().getId());
                } else {
                    intent.putExtra(Values.experts_id, question.getUser_id_answer());
                }


                context.startActivity(intent);
            }
        });
        ((ItemHolder) holder).layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAnswerCheckCredits.checkViewAnswer(context, question, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!question.isViewed()) {
                            question.setViewed(true);
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holderItem.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return questions.size();
    }
}
