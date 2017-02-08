package com.popdq.app.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.popdq.app.MyApplication;
import com.popdq.app.R;
import com.popdq.app.ui.InsertProfileActivity;
import com.popdq.app.ui.LoginActivity;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class User {

    public static void userNull(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.overridePendingTransition(0, 0);
            activity.finish();
        }

        return;
    }

    public static void userNameNull(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return;
    }

    public static User getInstance(Context context) {
        if (MyApplication.user == null) {
            try {
                MyApplication.user = Utils.getUser(context);
                if (MyApplication.user == null) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return null;

                }
            } catch (Exception e) {
                context.startActivity(new Intent(context, LoginActivity.class));
                e.printStackTrace();
                return null;
            }
        }

        if (MyApplication.user.username == null || MyApplication.user.username == null || MyApplication.user.username.equals("")) {
            Intent intent = new Intent(context, InsertProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        return MyApplication.user;
    }

    public boolean isDisableAllMethod() {
        if (config_charge == null || config_charge.length < 3) {
            return false;
        } else {
            for (int i = 0; i < config_charge.length; i++) {
                if (config_charge[i].price != -1) {
                    return false;
                }
            }
            return true;
        }
    }

    public String getDisplayName() {
//        String name = firstname + " " + lastname;
//        if (name == null || name.trim().equals("")) {
//            name = username;
////            return username;
//        }
////        name = username;
//        int i = 0;
//        i++;
//        if (status_anonymous == 0) {
//            return Utils.getAnonymousName(name);
//        } else
        return username;
    }


    public static void changeUser(Activity activity, User user) {
        String content = new Gson().toJson(user);
        PreferenceUtil.getInstanceEditor(activity).putString(Values.user, content);
        PreferenceUtil.getInstanceEditor(activity).commit();
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("change_user"));
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("change_user_more_tab"));
    }

    public static User changeCreditAndPutPrefernce(Context context, float credit_earnings, float credit_diposit) {
        User user = getInstance(context);
        user.setCredit(credit_diposit);
        user.setCredit_earnings(credit_earnings);
        String content = new Gson().toJson(user);
        PreferenceUtil.getInstanceEditor(context).putString(Values.user, content);
        PreferenceUtil.getInstanceEditor(context).commit();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("change_user"));
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("change_user_more_tab"));
        return user;
    }

    public String getConfigchareString(){
        if (config_charge == null || config_charge.length <= 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < config_charge.length; i++) {
            String price = "";
            if(config_charge[i].price<0){
                price = "DISABLE";
            }else {
                price = "$"+config_charge[i].price+"";
            }
            stringBuilder.append(config_charge[i].type + " - " +price +" | ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
    public String getCategoriesString() {
        if (categories == null || categories.size() <= 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(categories.get(i).getName() + " | ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        return stringBuilder.toString();
    }

    public String getRealCategory() {
        if (categories == null || categories.size() <= 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(categories.get(i).getName() + "|");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public String getIdCategoriesArr() {
        if (categories == null || categories.size() <= 0) return "[]";
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(categories.get(i).getCategory_id() + ",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }



    private CreditAnswer[] config_charge;
    private boolean isFavorite;
    private long id;
    private String username;
    private String email;

    private String firstname;
    private String lastname;
    private String facebook_id;
    private String google_id;
    private String password;
    private String avatar;
    private String position;
    private String description;
    private String address;
    private int gender;
    private long birthday;
    private String experience;
    private String professional_field;
    private int activate;
    private long registered_timestamp;
    private long login_timestamp;
    private String current_token;
    private int status_anonymous;
    private String language_answer;

    private int verified;
    private int email_verified;

    private int total_favorite;
    private int total_follow;
    private float credit;
    private float credit_earnings;
    private int status;
    private int type;
//    private Category[] categories;
    private List<Category> categories = new ArrayList<>();


    public int getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(int email_verified) {
        this.email_verified = email_verified;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getTotal_follow() {
        return total_follow;
    }

    public void setTotal_follow(int total_follow) {
        this.total_follow = total_follow;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getLanguage_answer() {

//        if (language_answer.equals("")) {
//            return " ";
//        }
        return language_answer;
    }

    public void setLanguage_answer(String language_answer) {
        this.language_answer = language_answer;
    }


    public float getCredit_earnings() {
        return credit_earnings;
    }

    public void setCredit_earnings(float credit_earnings) {
        this.credit_earnings = credit_earnings;
    }

    public String getUsername() {
        try {
            if (username != null && (username.contains("null") || username.contains("NULL"))) {
                return firstname + " " + lastname;
            }
            return username;
        } catch (Exception e) {
            return "";
        }
    }

    public String getName() {
        String name = firstname + " " + lastname;
//        if (name.equals(" ")) {
//            return " ";
//        }
        return name;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
//        if (description.equals("")) {
//            return " ";
//        }
        return description;
    }

    public String getRealDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAddress() {
//        if (address.equals("")) {
//            return " ";
//        }

        return address;
    }

    public String getRealAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProfessional_field() {

//        if (professional_field.equals("")) {
//            return " ";
//        }
        return professional_field;
    }

    public void setProfessional_field(String professional_field) {

        this.professional_field = professional_field;
    }

    public int getActivate() {
        return activate;
    }

    public void setActivate(int activate) {
        this.activate = activate;
    }

    public long getRegistered_timestamp() {
        return registered_timestamp;
    }

    public void setRegistered_timestamp(long registered_timestamp) {
        this.registered_timestamp = registered_timestamp;
    }

    public long getLogin_timestamp() {
        return login_timestamp;
    }

    public void setLogin_timestamp(long login_timestamp) {
        this.login_timestamp = login_timestamp;
    }

    public String getCurrent_token() {
        return current_token;
    }

    public void setCurrent_token(String current_token) {
        this.current_token = current_token;
    }

    public int getStatus_anonymous() {
        return 1;
    }

    public void setStatus_anonymous(int status_anonymous) {
        this.status_anonymous = status_anonymous;
    }

    public int getTotal_favorite() {
        return total_favorite;
    }

    public void setTotal_favorite(int total_favorite) {
        this.total_favorite = total_favorite;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public CreditAnswer[] getConfig_charge() {
        return config_charge;
    }

    public void setConfig_charge(CreditAnswer[] config_charge) {
        this.config_charge = config_charge;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAvatar(Context context, CircleImageView avatar, ImageView imgVerify) {
        try {
            Glide.with(context).load(Values.BASE_URL_AVATAR + getAvatar()).placeholder(R.drawable.profile_information_avatar).dontAnimate().into(avatar);
        } catch (Exception e) {

        }
        imgVerify.setImageResource(getVerified() == 1 ? R.drawable.ic_verified : android.R.color.transparent);
    }

    public static class CreditAnswer {
        public int type;
        public float price;
        public String summary;

        public CreditAnswer(int type, float price, String summary) {
            this.type = type;
            this.price = price;
            this.summary = summary;
        }
    }

//    public static List<User> getUsersDemo(){
//        List<User> users = new
//    }
}
