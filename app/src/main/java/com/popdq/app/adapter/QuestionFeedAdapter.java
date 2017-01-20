package com.popdq.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popdq.app.R;
import com.popdq.app.connection.UserUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Question;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.AskExpertActivity;
import com.popdq.app.ui.ListViewerActivity;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.util.ViewAnswerCheckCredits;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;
import com.popdq.mixpanelutil.MixPanelUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.popdq.app.values.Values.question_id;


/**
 * Created by Dang Luu on 06/07/2016.
 */
public class QuestionFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private OnItemClickListener onItemClickListener;
    public static boolean isLoadUser = true;
    private Activity context;
    private List<Question> questions;
    private List<User> users;
    private String textView;
    private String textViews;
    private String help, solved, reject;
    private HeaderHolder headerHolder;
    private String feeView;
    private int colorTextWhite, colorTextSolved;
    private String textResult;
    private String keyword;
    private String verify;
    private long myId;

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

    public QuestionFeedAdapter(Activity context, List<Question> questions, List<User> users) {
        this.context = context;
        isLoadUser = true;
        this.questions = questions;
        this.users = users;
        if (context == null || questions == null) return;
        textViews = context.getString(R.string.views);
        textView = context.getString(R.string.view_low);
        help = context.getString(R.string.help);
        solved = context.getString(R.string.solved);
        reject = context.getString(R.string.reject);
        colorTextWhite = Utils.getColor(context, R.color.white);
        colorTextSolved = Utils.getColor(context, R.color.text_solved_static);
        if (User.getInstance(context) == null) {
            return;
        }

        myId = User.getInstance(context).getId();
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        LinearLayout btnViewAll;
        RecyclerView listUsers;
        LinearLayout textQuetion, textUser;
        LinearLayout textTitleUser;
        TextViewThin resultQuestion, resultUser;
        View view_space;


        public HeaderHolder(View itemView) {
            super(itemView);
            btnViewAll = (LinearLayout) itemView.findViewById(R.id.btnViewAll);
            listUsers = (RecyclerView) itemView.findViewById(R.id.listUsers);
            textQuetion = (LinearLayout) itemView.findViewById(R.id.textQuetion);
            textUser = (LinearLayout) itemView.findViewById(R.id.textUser);
            resultQuestion = (TextViewThin) itemView.findViewById(R.id.resultQuestion);
            resultUser = (TextViewThin) itemView.findViewById(R.id.resultUser);
            textTitleUser = (LinearLayout) itemView.findViewById(R.id.textTitleUser);
            view_space = (View) itemView.findViewById(R.id.view_space);
//
//            if(textUser.getVisibility() == View.VISIBLE){
//                verify = Values.nonverified;
//            } else {
//                verify = Values.verified;
//            }
//
//            final String finalVerified = verify;
            btnViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixPanelUtil.trackViewAll(context);
                    Intent intent = new Intent(context, AskExpertActivity.class);
                    if(textUser.getVisibility() == View.GONE) {
                        intent.putExtra(Values.verified, true);
                    } else {
                        intent.putExtra(Values.verified, false);
                    }
                    context.startActivity(intent);
                }
            });
        }
    }

    public void setHeaderSpaceHeight(int height) {
        if (headerHolder != null && headerHolder.view_space != null) {
            headerHolder.view_space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
    }


    public void visibleTitleWhenSearch(boolean visibility) {
        try {
            if (visibility) {
                headerHolder.textQuetion.setVisibility(View.VISIBLE);
                headerHolder.textUser.setVisibility(View.VISIBLE);
                headerHolder.textTitleUser.setVisibility(View.GONE);
            } else {
                headerHolder.textQuetion.setVisibility(View.GONE);
                headerHolder.textUser.setVisibility(View.GONE);
                headerHolder.textTitleUser.setVisibility(View.VISIBLE);
            }
            if (questions.size() <= 0) {
                String result = String.format(context.getString(R.string.no_result_for), keyword);
                headerHolder.resultQuestion.setText(result);
                headerHolder.resultQuestion.setVisibility(View.VISIBLE);
            } else headerHolder.resultQuestion.setVisibility(View.GONE);

        } catch (Exception e) {

        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        CardView root;
        TextViewNormal tvCountViews;
        TextViewBold tvTitle;
        CircleImageView imgAvatar;
        ImageView icMethod;
        ImageView imgVerified;
        TextViewNormal tvNameUserAnswer, tvStatus;
        LinearLayout layoutView;
        TextViewNormal popBy;

        public ItemHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.root);
            tvTitle = (TextViewBold) itemView.findViewById(R.id.tvTitle);
            tvCountViews = (TextViewNormal) itemView.findViewById(R.id.tvCountViews);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
//            imgMethod = (ImageView) itemView.findViewById(R.id.imgMethod);
            icMethod = (ImageView) itemView.findViewById(R.id.icMethod);
            imgVerified = (ImageView) itemView.findViewById(R.id.imgVerified);
            tvNameUserAnswer = (TextViewNormal) itemView.findViewById(R.id.tvNameUserAnswer);
            tvStatus = (TextViewNormal) itemView.findViewById(R.id.tvStatus);
            popBy = (TextViewNormal) itemView.findViewById(R.id.popBy);
            layoutView = (LinearLayout) itemView.findViewById(R.id.layoutView);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = View.inflate(context, R.layout.layout_header_feed, null);
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
            if (isLoadUser) {
                isLoadUser = false;
            }

        }
        if (holder instanceof ItemHolder) {
            ItemHolder holderItem = (ItemHolder) holder;
            final Question question = questions.get(position - 1);
            holderItem.tvTitle.setText(question.getTitle());
            if (question.getTotal_view() <= 1) {
                holderItem.tvCountViews.setText(question.getTotal_view() + " " + textView);
            } else {
                holderItem.tvCountViews.setText(question.getTotal_view() + " " + textViews);
            }
            holderItem.tvNameUserAnswer.setText(question.getUser_answer().getDisplayName());
            if (question.isViewed() || question.getUser().getId() == myId || question.getUser_answer().getId() == myId) {
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
//            if (question.isView() | question.isViewed()) {
//                holderItem.imgMethod.setVisibility(View.GONE);
//                holderItem.layoutView.setVisibility(View.VISIBLE);
//            } else {
//                holderItem.imgMethod.setVisibility(View.VISIBLE);
//                holderItem.layoutView.setVisibility(View.GONE);
//            }
            ((ItemHolder) holder).imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Values.experts_id, question.getUser_id_answer());
                    context.startActivity(intent);
                }
            });

            holderItem.tvCountViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (question.getTotal_view() > 0) {
                        Intent intent1 = new Intent(context, ListViewerActivity.class);
                        intent1.putExtra(question_id, question.getId());
                        context.startActivity(intent1);
                    }
                }
            });
            holderItem.tvNameUserAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Values.experts_id, question.getUser_id_answer());
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
            User user = question.getUser_answer();
//            holderItem.popBy.setText(user.getUsername());
            user.setAvatar(context, holderItem.imgAvatar, holderItem.imgVerified);
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

    public void searchUser(final String name) {
        keyword = name;
        String verified = "";
        if (name.equals("")) {
            verified = "1";
        }
        UserUtil.search(context, PreferenceUtil.getToken(context), name, "", 0, 30, verified, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                List<User> users = null;
                if (keyword.equals("")) {
                    users = VolleyUtils.getListUserFromJson(response);
                } else {
                    users = VolleyUtils.getListUserFromJsonNotMe(context, response);
                }

                if (result.getCode() == 0) {

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//                    layoutManager.setReverseLayout(true);
//                    layoutManager.setStackFromEnd(true);
                    if (headerHolder != null && headerHolder.listUsers != null) {
                        headerHolder.listUsers.setLayoutManager(layoutManager);
                        headerHolder.listUsers.setAdapter(new UserHoziroltalAdapter(context, users));

                    }
                } else {
//                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (name.length() > 0) {
                    visibleTitleWhenSearch(true);
                } else {
                    visibleTitleWhenSearch(false);
                }
                try {
                    if (users.size() <= 0) {
                        headerHolder.resultUser.setVisibility(View.VISIBLE);
                        String result2 = String.format(context.getString(R.string.no_result_for), keyword);
                        headerHolder.resultUser.setText(result2);
                    } else headerHolder.resultUser.setVisibility(View.GONE);
                } catch (Exception e) {

                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return questions.size() + 1;
    }
}
