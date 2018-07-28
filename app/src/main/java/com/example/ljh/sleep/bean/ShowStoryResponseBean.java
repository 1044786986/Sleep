package com.example.ljh.sleep.bean;

import java.util.List;

public class ShowStoryResponseBean {
    private boolean status;
    private int responseCode;
    private List<MusicInfoBean> data;

    public List<MusicInfoBean> getData() {
        return data;
    }

    public void setData(List<MusicInfoBean> data) {
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
