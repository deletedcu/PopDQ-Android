package com.popdq.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.popdq.app.R;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.fragment.ExploreFragment;
import com.popdq.app.interfaces.OnItemClickListener;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ProvideQuestionActivity;
import com.popdq.app.ui.FavoriteListActivity;
import com.popdq.app.ui.MyPreviewActivity;
import com.popdq.app.ui.SelectCategoryActivity;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;
import com.popdq.app.view.textview.TextViewThin;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class ExploreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final String TAG = "ExploreAdapter";
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickFbFriendListener;


    private Activity context;
    private List<User> users;
    private String textView;
    private String textViews;
    private String help, solved, reject;
    private HeaderHolder headerHolder;
    private String name = "";


//    private int colorTextHelp, colorTextSolved;

    public List<User> getUsers() {
        return users;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickFbFriendListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickFbFriendListener = onClickListener;
    }

    public ExploreAdapter(Activity context, List<User> users) {
        this.context = context;
        this.users = users;
        if (context == null || users == null) return;
        textViews = context.getString(R.string.views);
        textView = context.getString(R.string.view_low);
        help = context.getString(R.string.help);
        solved = context.getString(R.string.solved);
        reject = context.getString(R.string.reject);

    }

    public class HeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout btnFriendFB, btnBrowseCategory, btnMyView, btnFollow;
        private LinearLayout layout_header;
        private TextViewNormal titleUser;

        public HeaderHolder(View itemView) {
            super(itemView);
            btnFriendFB = (RelativeLayout) itemView.findViewById(R.id.btnFriendFB);
            btnBrowseCategory = (RelativeLayout) itemView.findViewById(R.id.btnBrowseCategory);
            btnMyView = (RelativeLayout) itemView.findViewById(R.id.btnMyView);
            btnFollow = (RelativeLayout) itemView.findViewById(R.id.btnFollow);
            layout_header = (LinearLayout) itemView.findViewById(R.id.layout_header);
            titleUser = (TextViewNormal) itemView.findViewById(R.id.titleUser);

            btnFriendFB.setOnClickListener(this);
            btnBrowseCategory.setOnClickListener(this);
            btnMyView.setOnClickListener(this);
            btnFollow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFriendFB:
                    if (onClickFbFriendListener != null)
                        onClickFbFriendListener.onClick(view);

                    break;
                case R.id.btnBrowseCategory:
                    Intent intent1 = new Intent(context, SelectCategoryActivity.class);
//                    intent1.putExtra(Values.find_user_category, true);
                    context.startActivity(intent1);
                    break;
                case R.id.btnMyView:
                    context.startActivity(new Intent(context, MyPreviewActivity.class));
                    break;
                case R.id.btnFollow:
                    Intent intent = new Intent(context, FavoriteListActivity.class);
                    intent.putExtra(Values.user_id, PreferenceUtil.getUserId(context) + "");
                    context.startActivity(intent);
                    break;

            }
        }
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        TextViewNormal tvName;
        TextViewThin tvField;
        CircleImageView imgAvatar;
        Button btnAsk;
        RelativeLayout root;
        ImageView imgVerified, imgFollow;
        TextViewThin tvCreditText, tvCreditVoice, tvCreditVideo, tvDescription;
        LinearLayout layoutTextCredit, layoutVoiceCredit, layoutVideoCredit;
        TextViewNormal tvUserDisableAll;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextViewNormal) itemView.findViewById(R.id.tvNameAnswer);
            tvField = (TextViewThin) itemView.findViewById(R.id.tvField);
            tvField.setSelected(true);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            btnAsk = (Button) itemView.findViewById(R.id.btnAsk);
            imgVerified = (ImageView) itemView.findViewById(R.id.imgVerified);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
            tvCreditText = (TextViewThin) itemView.findViewById(R.id.tvCreditText);
            tvCreditVideo = (TextViewThin) itemView.findViewById(R.id.tvCreditVideo);
            tvCreditVoice = (TextViewThin) itemView.findViewById(R.id.tvCreditVoice);
            tvDescription = (TextViewThin) itemView.findViewById(R.id.tvDescription);
            tvUserDisableAll = (TextViewNormal) itemView.findViewById(R.id.tvUserDisableAll);

            imgFollow = (ImageView) itemView.findViewById(R.id.imgFollow);
            layoutTextCredit = (LinearLayout) itemView.findViewById(R.id.layoutTextCredit);
            layoutVoiceCredit = (LinearLayout) itemView.findViewById(R.id.layoutVoiceCredit);
            layoutVideoCredit = (LinearLayout) itemView.findViewById(R.id.layoutVideoCredit);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = View.inflate(context, R.layout.layout_header_explore, null);
            HeaderHolder headerHolder = new HeaderHolder(view);

            return headerHolder;
        } else if (viewType == TYPE_ITEM) {
            View view = View.inflate(context, R.layout.item_expert, null);
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

//    public void hideLayoutHeader(boolean hide) {
//        isHideHeader = hide;
////        if (headerHolder != null) {
////            if (hide)
////                headerHolder.layout_header.setVisibility(View.GONE);
////            else {
////                headerHolder.layout_header.setVisibility(View.VISIBLE);
////            }
////        }
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderHolder) {
            headerHolder = (HeaderHolder) holder;
            if (headerHolder != null) {
                if (ExploreFragment.isHideHeader) {
                    headerHolder.layout_header.setVisibility(View.GONE);
                    headerHolder.titleUser.setText(context.getString(R.string.users).toUpperCase());
                } else {
                    headerHolder.layout_header.setVisibility(View.VISIBLE);
                    headerHolder.titleUser.setText(context.getString(R.string.featured_users).toUpperCase());
                }
            }
        }
        if (holder instanceof ItemHolder) {
            ItemHolder holderItem = (ItemHolder) holder;
            final User user = users.get(position - 1);
            String name = user.getDisplayName();
            if (name == null || name.equals("")) {
                name = user.getFirstname() + " " + user.getLastname();
            }
            holderItem.tvName.setText(name);
            holderItem.tvField.setText(user.getRealCategory());
//            holderItem.tvField.setText(user.getDescription());
            if (user.getVerified() == 1) {
                holderItem.imgVerified.setVisibility(View.VISIBLE);
            } else {
                holderItem.imgVerified.setVisibility(View.GONE);
            }

            if (user.getDescription() == null || user.getDescription().equals("") ) {
                holderItem.tvDescription.setText("No information");
            } else {
                holderItem.tvDescription.setText(user.getDescription());
            }

            try {
                Utils.setLayoutCredit(user.getConfig_charge()[0].price, holderItem.layoutTextCredit, holderItem.tvCreditText);
            } catch (Exception e) {
                Utils.setLayoutCredit(0, holderItem.layoutTextCredit, holderItem.tvCreditText);
            }
            try {
                Utils.setLayoutCredit(user.getConfig_charge()[1].price, holderItem.layoutVoiceCredit, holderItem.tvCreditVoice);
            } catch (Exception e) {
                Utils.setLayoutCredit(0, holderItem.layoutVoiceCredit, holderItem.tvCreditVoice);
            }
            try {
                Utils.setLayoutCredit(user.getConfig_charge()[2].price, holderItem.layoutVideoCredit, holderItem.tvCreditVideo);
            } catch (Exception e) {
                Utils.setLayoutCredit(0, holderItem.layoutVideoCredit, holderItem.tvCreditVideo);
            }
            if (user.isFavorite()) {
                holderItem.imgFollow.setImageResource(R.drawable.ic_following_small);
            } else {
                holderItem.imgFollow.setImageResource(R.drawable.ic_follow_small);
            }
            if (user.getId() == PreferenceUtil.getUserId(context)) {
                holderItem.btnAsk.setVisibility(View.GONE);
            } else holderItem.btnAsk.setVisibility(View.VISIBLE);

            holderItem.imgFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.isFavorite()) {
                        FavoriteUtils.remove(context, PreferenceUtil.getToken(context), user.getId(), new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String response, Result result) {
                                if (result.getCode() == 0) {
                                    user.setFavorite(false);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    } else {
                        FavoriteUtils.add(context, PreferenceUtil.getToken(context), user.getId(), new VolleyUtils.OnRequestListenner() {
                            @Override
                            public void onSussces(String response, Result result) {
                                if (result.getCode() == 0) {
                                    user.setFavorite(true);
                                    notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                }
            });
            if (user.isDisableAllMethod()) {
                holderItem.btnAsk.setVisibility(View.GONE);
                holderItem.tvUserDisableAll.setVisibility(View.VISIBLE);
            } else {
                holderItem.tvUserDisableAll.setVisibility(View.GONE);
                holderItem.btnAsk.setVisibility(View.VISIBLE);
                holderItem.btnAsk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ProvideQuestionActivity.class);
                        intent.putExtra(Values.user_id_answer, user.getId());
                        intent.putExtra(Values.language_written, user.getLanguage_answer());
                        intent.putExtra(Values.name, user.getDisplayName());
                        intent.putExtra(Values.avatar, user.getAvatar());
                        intent.putExtra(Values.professional_field, user.getCategoriesString());
                        try {
                            intent.putExtra(Values.text_credit, user.getConfig_charge()[0].price);
                        } catch (Exception e) {
                            intent.putExtra(Values.text_credit, 0);

                        }
                        try {
                            intent.putExtra(Values.voice_credit, user.getConfig_charge()[1].price);
                        } catch (Exception e) {
                            intent.putExtra(Values.text_credit, 0);

                        }
                        try {
                            intent.putExtra(Values.video_credit, user.getConfig_charge()[2].price);
                        } catch (Exception e) {
                            intent.putExtra(Values.text_credit, 0);

                        }


                        context.startActivity(intent);

//                Toast.makeText(context, expertProfile.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            String a = Values.BASE_URL_AVATAR + user.getAvatar();
            Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holderItem.imgAvatar);
            holderItem.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Values.experts_id, user.getId());
                    context.startActivity(intent);
                }
            });
            ((ItemHolder) holder).tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Values.experts_id, user.getId());
                    context.startActivity(intent);
                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return users.size() + 1;
    }
}
