package com.popdq.app.model;

/**
 * Created by Dang Luu on 06/07/2016.
 */
public class Question {

    private long id;
    private long category_id;
    private long user_id;
    private long user_id_answer;
    private String title;
    private String description;
    private Attachments[] attachments;
    private String[] tag;
    private int total_view;
    private int total_answer;
    private int total_report;
    private long solved_timestamp;

    private long created_timestamp;
    private String language_spoken;
    private String language_written;
    private int status_anonymous;
    private int type;
    private int method;
    private int free_preview;
    private float credit_hold;
    private User user;
    private boolean isViewed;
    private User user_answer = null;
    private int count_rating;
    private int total_rating;


    public long getCreated_timestamp() {
        return created_timestamp*1000;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public int getCount_rating() {
        return count_rating;
    }

    public void setCount_rating(int count_rating) {
        this.count_rating = count_rating;
    }

    public int getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(int total_rating) {
        this.total_rating = total_rating;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public User getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(User user_answer) {
        this.user_answer = user_answer;
    }

    private int status;

    public String getLanguage_spoken() {
        return language_spoken;
    }

    public void setLanguage_spoken(String language_spoken) {
        this.language_spoken = language_spoken;
    }

    public String getLanguage_written() {
        return language_written;
    }

    public void setLanguage_written(String language_written) {
        this.language_written = language_written;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getFree_preview() {
        return free_preview;
    }

    public void setFree_preview(int free_preview) {
        this.free_preview = free_preview;
    }

    public float getCredit_hold() {
        return credit_hold;
    }

    public void setCredit_hold(float credit_hold) {
        this.credit_hold = credit_hold;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getUser_id_answer() {
        return user_id_answer;
    }

    public void setUser_id_answer(long user_id_answer) {
        this.user_id_answer = user_id_answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Attachments[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments[] attachments) {
        this.attachments = attachments;
    }

    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
    }

    public int getTotal_view() {
        return total_view;
    }

    public void setTotal_view(int total_view) {
        this.total_view = total_view;
    }

    public int getTotal_answer() {
        return total_answer;
    }

    public void setTotal_answer(int total_answer) {
        this.total_answer = total_answer;
    }

    public int getTotal_report() {
        return total_report;
    }

    public void setTotal_report(int total_report) {
        this.total_report = total_report;
    }

    public long getSolved_timestamp() {
        return solved_timestamp;
    }

    public void setSolved_timestamp(long solved_timestamp) {
        this.solved_timestamp = solved_timestamp;
    }

    public int getStatus_anonymous() {
        return status_anonymous;
    }

    public void setStatus_anonymous(int status_anonymous) {
        this.status_anonymous = status_anonymous;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class Attachments {
        public String link;
        public FileModel info;
    }
}
