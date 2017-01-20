package com.popdq.app.model;

/**
 * Created by Dang Luu on 9/8/2016.
 */
public class Category {
    private int id;
    private long user_id;
    private int category_id;
    private long created_timestamp;
    private String name;
    private String description;
    private int total_question;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotal_question() {
        return total_question;
    }

    public void setTotal_question(int total_question) {
        this.total_question = total_question;
    }
}
