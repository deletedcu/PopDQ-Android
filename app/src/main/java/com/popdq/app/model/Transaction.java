package com.popdq.app.model;

/**
 * Created by Dang Luu on 9/6/2016.
 */
public class Transaction {
    //type:
    //1: ios, 2: android, 3: view answer,
    // 4: received credit if user view answer,
    // 5: received if myself create answer,
    // 6: myself create question, 7: question reject,
    // 8: with draw, 9: convert credit
    private long id;
    private long user_id;
    private int type;
    private String receipt_data;
    private String transaction_id;
    private int quantity;
    private String credit;
    private String credit_received;
    private String data;
    private long updated_timestamp;
    private User user_from;
    private long question_id;
    private long created_timestamp;


    public long getCreated_timestamp() {
        return created_timestamp*1000;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public User getUser_from() {
        return user_from;
    }

    public void setUser_from(User user_from) {
        this.user_from = user_from;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceipt_data() {
        return receipt_data;
    }

    public void setReceipt_data(String receipt_data) {
        this.receipt_data = receipt_data;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getUpdated_timestamp() {
        return updated_timestamp;
    }

    public void setUpdated_timestamp(long updated_timestamp) {
        this.updated_timestamp = updated_timestamp;
    }
}
