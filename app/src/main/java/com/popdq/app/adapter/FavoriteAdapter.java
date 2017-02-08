package com.popdq.app.adapter;

import android.content.Context;
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
import com.popdq.app.model.FavoriteModel;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.ProvideQuestionActivity;
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
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private Context context;
    private List<FavoriteModel> favoriteModels;
    private String textFavorite;
    private boolean isMyFavorite;

    public FavoriteAdapter(Context context, List<FavoriteModel> favoriteModels, boolean isMyFavorite) {
        this.context = context;
        this.favoriteModels = favoriteModels;
        textFavorite = context.getString(R.string.favorites);
        this.isMyFavorite = isMyFavorite;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextViewNormal tvName;
        TextViewThin tvField;
        CircleImageView imgAvatar;
        Button btnAsk;
        RelativeLayout root;
        ImageView imgVerified, imgFollow;
        TextViewThin tvCreditText, tvCreditVoice, tvCreditVideo, tvDescription;
        LinearLayout layoutTextCredit, layoutVoiceCredit, layoutVideoCredit;
        TextViewNormal tvUserDisableAll;

        public ViewHolder(View itemView) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_expert, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User userTemp = null;
        if (isMyFavorite) {
            userTemp = favoriteModels.get(position).getFavorite();
        } else {
            userTemp = favoriteModels.get(position).getUser();
        }
        final User user = userTemp;
        String name = user.getDisplayName();
        holder.tvName.setText(name);
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, user.getId());
                context.startActivity(intent);
            }
        });
        holder.tvField.setText(user.getCategoriesString());
        if (user.getVerified() == 1) {
            holder.imgVerified.setVisibility(View.VISIBLE);
        } else {
            holder.imgVerified.setVisibility(View.GONE);
        }
//        if (user.getDescription().equals("")) {
//            holder.tvDescription.setText(" ");
//        } else
        holder.tvDescription.setText(user.getDescription());
//        try {
//            if (user.getConfig_charge()[0].price == 0) {
//                holder.tvCreditText.setText("FREE");
//            } else
//                holder.tvCreditText.setText("$ " + user.getConfig_charge()[0].price + "");
//
//        } catch (Exception e) {
//            holder.tvCreditText.setText("");
//
//        }
//        try {
//            if (user.getConfig_charge()[1].price == 0) {
//                holder.tvCreditVoice.setText("FREE");
//            } else
//                holder.tvCreditVoice.setText("$ " + user.getConfig_charge()[1].price + "");
//
//
//        } catch (Exception e) {
//            holder.tvCreditVoice.setText("");
//
//        }
//        try {
//            if (user.getConfig_charge()[2].price == 0) {
//                holder.tvCreditVideo.setText("FREE");
//            } else
//                holder.tvCreditVideo.setText("$ " + user.getConfig_charge()[2].price + "");
//
//
//        } catch (Exception e) {
//            holder.tvCreditVideo.setText("");
//        }

        try {
            Utils.setLayoutCredit(user.getConfig_charge()[0].price, holder.layoutTextCredit, holder.tvCreditText);
        } catch (Exception e) {
            Utils.setLayoutCredit(0, holder.layoutTextCredit, holder.tvCreditText);
        }
        try {
            Utils.setLayoutCredit(user.getConfig_charge()[1].price, holder.layoutVoiceCredit, holder.tvCreditVoice);
        } catch (Exception e) {
            Utils.setLayoutCredit(0, holder.layoutVoiceCredit, holder.tvCreditVoice);
        }
        try {
            Utils.setLayoutCredit(user.getConfig_charge()[2].price, holder.layoutVideoCredit, holder.tvCreditVideo);
        } catch (Exception e) {
            Utils.setLayoutCredit(0, holder.layoutVideoCredit, holder.tvCreditVideo);
        }

        if (user.isFavorite()) {
            holder.imgFollow.setImageResource(R.drawable.ic_following);
        } else {
            holder.imgFollow.setImageResource(R.drawable.ic_follow);
        }


        if (user.isDisableAllMethod()) {
            holder.btnAsk.setVisibility(View.GONE);
            holder.tvUserDisableAll.setVisibility(View.VISIBLE);
        } else {
            holder.tvUserDisableAll.setVisibility(View.GONE);
            holder.btnAsk.setVisibility(View.VISIBLE);
            holder.btnAsk.setOnClickListener(new View.OnClickListener() {
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
        if (User.getInstance(context).getId() == user.getId()) {
            holder.imgFollow.setVisibility(View.GONE);
            holder.btnAsk.setVisibility(View.GONE);

        } else {
            holder.imgFollow.setOnClickListener(new View.OnClickListener() {
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
        }
        String a = Values.BASE_URL_AVATAR + user.getAvatar();
        Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holder.imgAvatar);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Values.experts_id, user.getId());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return favoriteModels.size();
    }

    public List<FavoriteModel> getFavoriteModels() {
        return favoriteModels;
    }
}
