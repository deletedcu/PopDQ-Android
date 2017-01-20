package com.popdq.app.model;

/**
 * Created by Dang Luu on 15/07/2016.
 */
public class Answer {
    private long id;
    private long question_id;
    private long user_id;
    private String content;
    private Question.Attachments[] attachments;
    private int total_rating;
    private int count_rating;
    private int total_report;
    private int status_anonymous;
    private int status;
    private String tag;
    private User user;
    private MyRating myRating;
    private int totalView;
    private int method;

    private long created_timestamp;


    public long getCreated_timestamp() {
        return created_timestamp*1000;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }


    public MyRating getMyRating() {
        return myRating;
    }

    public void setMyRating(MyRating myRating) {
        this.myRating = myRating;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Question.Attachments[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Question.Attachments[] attachments) {
        this.attachments = attachments;
    }

    public int getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(int total_rating) {
        this.total_rating = total_rating;
    }

    public int getCount_rating() {
        return count_rating;
    }

    public void setCount_rating(int count_rating) {
        this.count_rating = count_rating;
    }

    public int getTotal_report() {
        return total_report;
    }

    public void setTotal_report(int total_report) {
        this.total_report = total_report;
    }

    public int getStatus_anonymous() {
        return status_anonymous;
    }

    public void setStatus_anonymous(int status_anonymous) {
        this.status_anonymous = status_anonymous;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
