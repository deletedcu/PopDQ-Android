package com.popdq.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.connection.FavoriteUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.ui.UserProfileActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewThin;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class UserHoziroltalAdapter extends RecyclerView.Adapter<UserHoziroltalAdapter.ViewHolder> {
    private Context context;
    private List<User> expertProfiles;


    public UserHoziroltalAdapter(Context context, List<User> expertProfiles) {
        this.context = context;
        this.expertProfiles = expertProfiles;
    }

    public List<User> getExpertProfiles() {
        return expertProfiles;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextViewThin tvName;
        CircleImageView imgAvatar;
        LinearLayout root;
        ImageView imgFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextViewThin) itemView.findViewById(R.id.tvNameAnswer);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            imgFollow = (ImageView) itemView.findViewById(R.id.imgFollow);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_user_horizoltal, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User user = expertProfiles.get(position);
        String name = user.getDisplayName();
        if (name == null || name.equals("")) {
            name = user.getFirstname() + " " + user.getLastname();
        }
        holder.tvName.setText(name);
        if (user.isFavorite()) {
            holder.imgFollow.setImageResource(R.drawable.ic_following);
        } else {
            holder.imgFollow.setImageResource(R.drawable.ic_follow);
        }
        holder.imgFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!user.isFavorite())
                    FavoriteUtils.add(context, PreferenceUtil.getToken(context), user.getId(), new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (result.getCode() == 0) {
                                expertProfiles.get(position).setFavorite(!user.isFavorite());
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                else {
                    FavoriteUtils.remove(context, PreferenceUtil.getToken(context), user.getId(), new VolleyUtils.OnRequestListenner() {
                        @Override
                        public void onSussces(String response, Result result) {
                            if (result.getCode() == 0) {
                                expertProfiles.get(position).setFavorite(!user.isFavorite());
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
        String a = Values.BASE_URL_AVATAR + user.getAvatar();
        Glide.with(context).load(a).placeholder(R.drawable.list_avatar).dontAnimate().into(holder.imgAvatar);
        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
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
        return expertProfiles.size();
    }
}
