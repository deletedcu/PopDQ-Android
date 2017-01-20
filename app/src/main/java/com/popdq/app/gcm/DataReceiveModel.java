package com.popdq.app.gcm;

import com.popdq.app.model.Question;
import com.popdq.app.model.User;

/**
 * Created by Dang Luu on 8/29/2016.
 */
public class DataReceiveModel {

    public Data data;
    public Aps aps;


    public class Data {
        public Question question;
        public User from;
        public int type;
        public float credit;
        public float credit_earnings;
        public long notification_id;

    }

    public class Aps {
        public String alert;
        public String sound;
    }
}
