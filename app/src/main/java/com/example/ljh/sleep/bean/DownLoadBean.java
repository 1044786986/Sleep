package com.example.ljh.sleep.bean;

public class DownLoadBean {
    public static final int DOWNLOAD_WIAT = 0;
    public static final int DOWNLOAD_ING = 1;
    public static final int DOWNLOAD_PAUSE = 2;
    public static final int DOWNLOAD_FINISH = 3;
    public static final int DOWNLOAD_ERROR = 4;

    private String name;
    private String author;
    private String url;
    private String filepath;
    private String type;
    private int speed;
    private int progress;
    private int length;
    private int fileLength;
    private String duration;
    private int position;
    private int id;
    private int status; //0-等待下载  1-正在下载 2-暂停下载 3-已完成 4-网络错误

    public int getFilelength() {
        return fileLength;
    }

    public void setFilelength(int filelength) {
        this.fileLength = filelength;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public String getDuration(){
        return duration;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
