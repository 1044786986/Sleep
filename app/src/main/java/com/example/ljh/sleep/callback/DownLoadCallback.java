package com.example.ljh.sleep.callback;

import com.example.ljh.sleep.bean.DownLoadBean;

public interface DownLoadCallback {
    void onDownStart(DownLoadBean downLoadBean);
    void onDownWait(DownLoadBean downLoadBean);
    void DownPause(DownLoadBean downLoadBean);
    void DownReStart(DownLoadBean downLoadBean);
    void onDowning(DownLoadBean downLoadBean);
    void onDownFinish(DownLoadBean downLoadBean);
    void onFailed(DownLoadBean downLoadBean,String error);
}

