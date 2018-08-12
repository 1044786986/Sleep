package com.example.ljh.sleep.model;

import android.content.Context;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.contract.DownLoadContract;
import com.example.ljh.sleep.utils.SQLiteUtils;

public class DownLoadModel implements DownLoadContract.DownLoadModel{

    @Override
    public void getDownLoaded(Context context,DownLoadContract.GetDownLoadCallback callback) {
//        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        Cursor cursor = sqLiteDatabase.query("download",null,"status=3",
//                null, null,null,"date");
//        while (cursor.moveToNext()){
//            DownLoadBean bean = new DownLoadBean();
//            bean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
//            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
//            bean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
//            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//            bean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
//            list.add(bean);
//        }
//        callback.onSuccess(list);
        SQLiteUtils utils = new SQLiteUtils(context,null);
        utils.getDownLoaded(callback);
    }

    @Override
    public void getDownLoading(Context context,DownLoadContract.GetDownLoadCallback callback) {
//        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        Cursor cursor = sqLiteDatabase.query("download",null,"status!=3",
//                null, null,null,"date");
//        while (cursor.moveToNext()){
//            DownLoadBean bean = new DownLoadBean();
//            bean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
//            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
//            bean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
//            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//            bean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
//            list.add(bean);
//        }
//        callback.onSuccess(list);
        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteUtils.getDownLoading(callback);
    }

    @Override
    public void updateDownLoad(Context context, DownLoadBean downLoadBean) {
        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("status",downLoadBean.getStatus());
//        sqLiteDatabase.update("download",contentValues,"story_id=" + downLoadBean.getId(),null);
//        sqLiteDatabase.close();
        sqLiteUtils.updateDownLoadStatus(downLoadBean);
    }
}
