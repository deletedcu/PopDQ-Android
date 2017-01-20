package com.popdq.app.model;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class ExpertProfile extends User {

    private String professinalField;
    private int experienceYear;
    private String location;
    private String description;
    private String avatar;

    public String getProfessinalField() {
        return professinalField;
    }

    public void setProfessinalField(String professinalField) {
        this.professinalField = professinalField;
    }

    public int getExperienceYear() {
        return experienceYear;
    }

    public void setExperienceYear(int experienceYear) {
        this.experienceYear = experienceYear;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
