package com.popdq.app.model;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class Interest {
    private int id;
    private int total_question;
    private int status;
    private String name;
    private long created_timestamp;
    private String description;
    private boolean myInterest;

    public Interest(int _id, String name) {
        this.id = _id;
        this.name = name;
    }

    public int get_id() {
        return id;
    }

    public void set_id(int _id) {
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal_question() {
        return total_question;
    }

    public void setTotal_question(int total_question) {
        this.total_question = total_question;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMyInterest() {
        return myInterest;
    }

    public void setMyInterest(boolean myInterest) {
        this.myInterest = myInterest;
    }
}
