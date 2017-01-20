package com.popdq.app.model;

/**
 * Created by Dang Luu on 21/07/2016.
 */
public class FileModel {
    private String path;
    private long fileSize;
    private double duration;

    public FileModel(String path, long fileSize, long duration) {
        this.path = path;
        this.fileSize = fileSize;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
