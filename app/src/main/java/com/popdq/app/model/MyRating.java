package com.popdq.app.model;

/**
 * Created by Dang Luu on 15/08/2016.
 */
public class MyRating {
    private long id;
    private long answer_id;
    private long user_id;
    private int rating;
    private String content;
    private long created_timestamp;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(long answer_id) {
        this.answer_id = answer_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }
}
