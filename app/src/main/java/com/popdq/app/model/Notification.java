package com.popdq.app.model;

/**
 * Created by Dang Luu on 9/9/2016.
 */
public class Notification {

    private long id;
    private long receiver_id;
    private Content content;
    private int is_read;
    private long read_timestamp;
    private int is_pushed;
    private long push_timestamp;
    private long created_timestamp;
    private int type;
    private int total_unread;

    public int getTotal_unread() {
        return total_unread;
    }

    public void setTotal_unread(int total_unread) {
        this.total_unread = total_unread;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(long receiver_id) {
        this.receiver_id = receiver_id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }



    public int getIs_pushed() {
        return is_pushed;
    }

    public void setIs_pushed(int is_pushed) {
        this.is_pushed = is_pushed;
    }

    public long getRead_timestamp() {
        return read_timestamp;
    }

    public void setRead_timestamp(long read_timestamp) {
        this.read_timestamp = read_timestamp;
    }

    public long getPush_timestamp() {
        return push_timestamp;
    }

    public void setPush_timestamp(long push_timestamp) {
        this.push_timestamp = push_timestamp;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public class Content {
        private User myInfo;
        private User user;
        private Question question;
        private Answer answer;
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public User getMyInfo() {
            return myInfo;
        }

        public void setMyInfo(User myInfo) {
            this.myInfo = myInfo;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public Answer getAnswer() {
            return answer;
        }

        public void setAnswer(Answer answer) {
            this.answer = answer;
        }
    }
}
