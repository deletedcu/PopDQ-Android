package com.popdq.app.model;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class FavoriteModel {
//    private long id;
    private long user_id;
    private User favorite;
    private User user;
    private int total_favorite;

    public int getTotal_favorite() {
        return total_favorite;
    }

    public void setTotal_favorite(int total_favorite) {
        this.total_favorite = total_favorite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
//    @Override
//    public long getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(long id) {
//        this.id = id;
//    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public User getFavorite() {
        return favorite;
    }

    public void setFavorite(User favorite) {
        this.favorite = favorite;
    }
}
