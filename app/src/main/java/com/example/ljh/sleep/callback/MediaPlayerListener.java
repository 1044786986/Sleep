package com.example.ljh.sleep.callback;

import com.example.ljh.sleep.bean.MusicInfoBean;

public interface MediaPlayerListener {
    void play(MusicInfoBean musicInfoBean);
    void pause();
    void start();
    void next();
    void prev();
}
