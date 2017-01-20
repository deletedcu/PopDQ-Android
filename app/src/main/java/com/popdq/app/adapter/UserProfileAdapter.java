package com.popdq.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.connection.ExpertUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.ui.ViewImageActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.ViewAnswerCheckCredits;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private OnItemClickListener onItemClickListener;
    public static boolean isLoadHeader = true;
    private Activity context;
    private List<Question> questions;
    private String textReply;
    private HeaderHolder headerHolder;
    long expert_id;
    private User user;
    private boolean areYourAcc;
    TextViewNormal tvTitleListQuestion;

    public List<Question> getQuestions() {
        return questions;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public UserProfileAdapter(Activity context, List<Question> questions, long expert_id) {
        this.context = context;
        isLoadHeader = true;
        this.questions = questions;
        if (context == null || questions == null) return;
        textReply = context.getString(R.string.views);
        this.expert_id = expert_id;
        user = User.getInstance(context);
        if (expert_id == user.getId()) {
            areYourAcc = true;
        } else areYourAcc = false;


    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);

        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        CardView root;
        TextViewNormal tvCountViews;
        TextViewBold tvTitle;
        CircleImageView imgAvatar;
        ImageView imgMethod;
        TextViewNormal tvNameUserAnswer;

        public ItemHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.root);
            tvTitle = (TextViewBold) itemView.findViewById(R.id.tvTitle);
            tvCountViews = (TextViewNormal) itemView.findViewById(R.id.tvCountViews);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            imgMethod = (ImageView) itemView.findViewById(R.id.imgMethod);
            tvNameUserAnswer = (TextViewNormal) itemView.findViewById(R.id.tvNameUserAnswer);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = View.inflate(context, R.layout.item_header_profile, null);
            HeaderHolder headerHolder = new HeaderHolder(view);

            return headerHolder;
        } else if (viewType == TYPE_ITEM) {
            View view = View.inflate(context, R.layout.item_question_new_design, null);
            ItemHolder itemHolder = new ItemHolder(view);
            return itemHolder;
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else
            return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderHolder) {
            headerHolder = (HeaderHolder) holder;
            if (isLoadHeader) {
                isLoadHeader = false;
                getDataHeader(holder.itemView);
            }

        }
        if (holder instanceof ItemHolder) {
            ItemHolder holderItem = (ItemHolder) holder;
            final Question question = questions.get(position - 1);
            holderItem.tvTitle.setText(question.getTitle());
            holderItem.tvCountViews.setText(question.getTotal_view() + " " + textReply);
            holderItem.tvNameUserAnswer.setText(question.getUser_answer().getDisplayName());
            if (question.getMethod() == 1) {
                if (question.getFree_preview() == 1) {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_text);
                } else {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_text);
                }
            } else if (question.getMethod() == 2) {
                if (question.getFree_preview() == 1) {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_audio);
                } else {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_audio);
                }
            } else if (question.getMethod() == 3) {
                if (question.getFree_preview() == 1) {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_free_video);
                } else {
                    holderItem.imgMethod.setImageResource(R.drawable.ic_video);
                }
            }
            ((ItemHolder) holder).imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Values.experts_id, question.getUser_id_answer());
                    context.startActivity(intent);
                }
            });
            ((ItemHolder) holder).imgMethod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewAnswerCheckCredits.checkViewAnswer(context, question, null);
                }
            });
            String a = Values.BASE_URL_AVATAR + question.getUser_answer().getAvatar();
            Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holderItem.imgAvatar);

            holderItem.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(position - 1);
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return questions.size() + 1;
    }

    public void setCountDisplay() {
        if (tvTitleListQuestion != null)
            tvTitleListQuestion.setText(context.getString(R.string.my_answerd_question) + " (" + questions.size() + ")");
    }

    public void getDataHeader(final View header) {
        ExpertUtils.detail(context, PreferenceUtil.getToken(context), expert_id, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                user = VolleyUtils.getUserInfo(response);
                if (user == null) {
                    Toast.makeText(context, context.getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = user.getDisplayName();
                ((TextViewBold) header.findViewById(R.id.tvNameAnswer)).setText(name.toUpperCase());
                if (user.getStatus_anonymous() == 0 && !areYourAcc) {
                } else {
                    final TextViewNormal tvDescription = ((TextViewNormal) header.findViewById(R.id.tvDescription));
                    String professtion = user.getCategoriesString();
                    ((TextViewNormal) header.findViewById(R.id.tvProfesstion)).setSelected(true);

                    if (professtion == null || professtion.equals("")) {
                        ((TextViewNormal) header.findViewById(R.id.tvProfesstion)).setText("No information");
                    } else {
                        ((TextViewNormal) header.findViewById(R.id.tvProfesstion)).setText(user.getCategoriesString() + "");
                    }
                    String location = user.getAddress();
                    if (location == null || location.equals("")) {
                        ((TextViewNormal) header.findViewById(R.id.tvLocation)).setText("No information");
                    } else {
                        ((TextViewNormal) header.findViewById(R.id.tvLocation)).setText(user.getAddress() + "");
                    }
                    ((TextViewNormal) header.findViewById(R.id.tvLocation)).setSelected(true);

                    ((TextViewThin) header.findViewById(R.id.tvFollowers)).setText(context.getString(R.string.follower) + " " + user.getTotal_favorite());
                    ((TextViewThin) header.findViewById(R.id.tvFollowing)).setText(context.getString(R.string.following) + " " + user.getTotal_follow());
                    try {
                        if (user.getConfig_charge()[0].price == 0) {
                            ((TextViewThin) header.findViewById(R.id.tvTextPrice)).setText("FREE");
                        } else
                            ((TextViewThin) header.findViewById(R.id.tvTextPrice)).setText("$ " + user.getConfig_charge()[0].price + "");

                    } catch (Exception e) {
                        ((TextViewThin) header.findViewById(R.id.tvTextPrice)).setText("");

                    }
                    try {
                        if (user.getConfig_charge()[1].price == 0) {
                            ((TextViewThin) header.findViewById(R.id.tvVoicePrice)).setText("FREE");
                        } else
                            ((TextViewThin) header.findViewById(R.id.tvVoicePrice)).setText("$ " + user.getConfig_charge()[1].price + "");


                    } catch (Exception e) {
                        ((TextViewThin) header.findViewById(R.id.tvVoicePrice)).setText("");

                    }
                    try {
                        if (user.getConfig_charge()[2].price == 0) {
                            ((TextViewThin) header.findViewById(R.id.tvVideoPrice)).setText("FREE");
                        } else
                            ((TextViewThin) header.findViewById(R.id.tvVideoPrice)).setText("$ " + user.getConfig_charge()[2].price + "");


                    } catch (Exception e) {
                        ((TextViewThin) header.findViewById(R.id.tvVideoPrice)).setText("");

                    }
                    if (user.getDescription().equals("")) {
                        tvDescription.setText("No information");

                    } else tvDescription.setText(user.getDescription() + "");

                    tvDescription.setMovementMethod(new ScrollingMovementMethod());
//                    ScrollView rootScroll = (ScrollView) header.findViewById(R.id.rootScroll);
//                    final ScrollView SCROLLER_ID = (ScrollView) findViewById(R.id.SCROLLER_ID);
//                    rootScroll.setOnTouchListener(new View.OnTouchListener() {
//
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            tvDescription.getParent().requestDisallowInterceptTouchEvent(false);
//                            return false;
//                        }
//                    });
//                    tvDescription.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            v.getParent().requestDisallowInterceptTouchEvent(true);
//                            return false;
//                        }
//                    });
                }
                try {
                    Glide.with(context).load(Values.BASE_URL_AVATAR + user.getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into((CircleImageView) header.findViewById(R.id.imgAvatar));
                } catch (Exception e) {

                }

                tvTitleListQuestion = (TextViewNormal) header.findViewById(R.id.tvTitleListQuestion);
                ((ImageView) header.findViewById(R.id.imgFollow)).setImageResource(user.isFavorite() ? R.drawable.ic_following : R.drawable.ic_follow);
                ((ImageView) header.findViewById(R.id.imgVerified)).setImageResource(user.getVerified() == 1 ? R.drawable.ic_verified : android.R.color.transparent);
                ((ImageView) header.findViewById(R.id.imgAvatar)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user.getAvatar() == null || user.getAvatar().equals("")) {
                            Toast.makeText(context, "No avatar!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(context, ViewImageActivity.class);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(user.getAvatar());
                        intent.putStringArrayListExtra("listImages", strings);
                        intent.putExtra(Values.index, strings);
                        context.startActivity(intent);
                    }
                });
setCountDisplay();

            }

            @Override
            public void onError(String error) {

            }
        });
    }

}
