package com.popdq.app.model;

/**
 * Created by Dang Luu on 9/6/2016.
 */
public class Credit {
    private long id;
    private String name;
    private String credit;
    private String credit_received;
    private long created_timestamp;
    private long updated_timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCredit_received() {
        return credit_received;
    }

    public void setCredit_received(String credit_received) {
        this.credit_received = credit_received;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }
}
