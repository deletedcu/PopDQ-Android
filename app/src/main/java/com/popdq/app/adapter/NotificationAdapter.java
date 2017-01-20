package com.popdq.app.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.popdq.app.R;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Answer;
import com.popdq.app.model.Notification;
import com.popdq.app.model.Question;
import com.popdq.app.model.User;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewBold;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 9/6/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter {
    private OnItemClickListener onItemClickListener;
    private List<Notification> notifications;
    private Activity context;
    private Question currentQuestiion;
    private int type;
    private String askName;
    private int colorRead, colorNonRead;

    public NotificationAdapter(Activity context, List<Notification> notifications) {
        this.notifications = notifications;
        this.context = context;
        colorRead = Utils.getColor(context, R.color.button_cooking_static);
        colorNonRead = Utils.getColor(context, R.color.button_cooking_press);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        private TextViewBold tvUserName;
        private TextViewNormal tvAction;
        private ImageView imgIconMethod;
        private CircleImageView imgAvatar;
        private TextViewNormal tvDescription;
        private RelativeLayout root;
        private LinearLayout background;

        public NotificationHolder(View itemView) {
            super(itemView);
            tvUserName = (TextViewBold) itemView.findViewById(R.id.tvUserName);
            tvAction = (TextViewNormal) itemView.findViewById(R.id.tvAction);
            tvAction.setSelected(true);
            imgIconMethod = (ImageView) itemView.findViewById(R.id.imgIconMethod);
            tvDescription = (TextViewNormal) itemView.findViewById(R.id.tvDescription);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            background = (LinearLayout) itemView.findViewById(R.id.background);
        }

        public void setUserName(String name) {
            if (name != null && tvUserName != null) {
                if (name.equals("")) {
                    tvUserName.setVisibility(View.GONE);
                } else {
                    tvUserName.setText(name);
                    tvUserName.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_list_notification, null);
        NotificationHolder notificationHolder = new NotificationHolder(view);
        return notificationHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        NotificationHolder notificationHolder = (NotificationHolder) holder;
        final Notification notification = notifications.get(position);
        User myInfo = notification.getContent().getMyInfo();
        User userPartner = notification.getContent().getUser();
        final Question question = notification.getContent().getQuestion();
        Answer answer = notification.getContent().getAnswer();
        final int type = notification.getType();
        String avatarLink;
        if (userPartner == null) {
            avatarLink = Values.BASE_URL_AVATAR + notification.getContent().getMyInfo().getAvatar();
        } else {
            avatarLink = Values.BASE_URL_AVATAR + userPartner.getAvatar();
        }
        Glide.with(context).load(avatarLink).placeholder(R.drawable.list_avatar).dontAnimate().into(notificationHolder.imgAvatar);
        if (notification.getIs_read() == 0) {
            notificationHolder.background.setBackgroundColor(colorNonRead);
        } else {
            notificationHolder.background.setBackgroundColor(colorRead);
        }
        if (userPartner != null && notification.getContent().getUser().getDisplayName() != null)
            notificationHolder.setUserName(notification.getContent().getUser().getDisplayName());
        switch (type) {
            case 1:
                notificationHolder.tvAction.setText(context.getString(R.string.noti_item_has_question));
                Utils.setNotiIconShow(context, question.getMethod(), notificationHolder.imgIconMethod);
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 2:
                Utils.setNotiIconShow(context, question.getMethod(), notificationHolder.imgIconMethod);
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " + question.getTitle());
                notificationHolder.tvAction.setText(context.getString(R.string.noti_item_has_your_answer));
                break;
            case 3:
                notificationHolder.imgIconMethod.setVisibility(View.GONE);
                notificationHolder.tvAction.setText(context.getString(R.string.noti_item_view_your_question));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 4:
                notificationHolder.imgIconMethod.setVisibility(View.GONE);

                notificationHolder.tvAction.setText(String.format(context.getString(R.string.noti_item_has_been_rejected), ""));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 5:
                notificationHolder.setUserName("");
//                notificationHolder.tvAction.setText(context.getString(R.string.app_name));
//                notificationHolder.tvDescription.setText(context.getString(R.string.noti_wellcome_title));
                notificationHolder.imgIconMethod.setVisibility(View.GONE);

                notificationHolder.tvAction.setText(context.getString(R.string.noti_wellcome_title));
                notificationHolder.tvDescription.setText(context.getString(R.string.noti_wellcome_des));
                break;
            case 8:
                notificationHolder.setUserName("");
                notificationHolder.imgIconMethod.setVisibility(View.GONE);

//                notificationHolder.tvUserName.setText("");
                notificationHolder.tvAction.setText(context.getString(R.string.app_name));
                notificationHolder.tvDescription.setText(String.format(context.getString(R.string.noti_credited), ""));
                break;
            case 10:
                Utils.setNotiIconShow(context, question.getMethod(), notificationHolder.imgIconMethod);
                notificationHolder.setUserName(userPartner.getDisplayName());

//                notificationHolder.tvUserName.setText(userPartner.getDisplayName());
                notificationHolder.tvAction.setText(context.getString(R.string.noti_item_followed_has_question));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 12:
                Utils.setNotiIconShow(context, question.getMethod(), notificationHolder.imgIconMethod);
                notificationHolder.setUserName(userPartner.getDisplayName());
//                notificationHolder.tvUserName.setText(userPartner.getDisplayName());
                notificationHolder.tvAction.setText(context.getString(R.string.noti_item_has_answer));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - "+question.getTitle());
                break;
            case 13:
//                notificationHolder.tvUserName.setText("");
                notificationHolder.setUserName("");
                notificationHolder.imgIconMethod.setVisibility(View.GONE);

                notificationHolder.tvAction.setText(String.format(context.getString(R.string.noti_has_been_expried), userPartner.getDisplayName()));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 14:
                notificationHolder.setUserName("");
                notificationHolder.imgIconMethod.setVisibility(View.GONE);

//                notificationHolder.tvUserName.setText("");
                notificationHolder.tvAction.setText(String.format(context.getString(R.string.noti_has_been_expried_in_hour), userPartner.getDisplayName()));
                notificationHolder.tvDescription.setText("$" + question.getCredit_hold() + " - " +question.getTitle());
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                notificationHolder.imgIconMethod.setVisibility(View.GONE);


                notificationHolder.setUserName("Admin");
                notificationHolder.tvAction.setText("");
                notificationHolder.tvDescription.setText(notification.getContent().getContent());


                break;
        }


//        notificationHolder.tvUserName.setText(notification.getContent().getUser().getDisplayName());
        notificationHolder.root
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (onItemClickListener != null) {
                                                onItemClickListener.onClick(position);
                                            }
                                            if (notification.getContent().getUser() != null)
                                                askName = notification.getContent().getUser().getDisplayName();
                                            else {
                                                askName = notification.getContent().getMyInfo().getDisplayName();
                                            }
                                            currentQuestiion = question;
                                            NotificationAdapter.this.type = type;
                                        }
                                    }

                );
    }


    public List<Notification> getNotifications() {
        return notifications;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


}
