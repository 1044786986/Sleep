package com.example.ljh.sleep.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.contract.MainContract;
import com.example.ljh.sleep.utils.GetDateUtils;
import com.example.ljh.sleep.utils.SQLiteUtils;

public class MainModel implements MainContract.MainModel{

    @Override
    public void insertDownLoadInfo(Context context, DownLoadBean bean) {
        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("story_id",bean.getId());
//        contentValues.put("name",bean.getName());
//        contentValues.put("author",bean.getAuthor());
//        contentValues.put("url",bean.getUrl());
//        contentValues.put("progress",0);
//        contentValues.put("date", GetDateUtils.getDate());
//        contentValues.put("status",0);
//        sqLiteDatabase.insert("download",null,contentValues);
//        sqLiteDatabase.close();
        sqLiteUtils.insertDownLoadInfo(bean);
    }

    @Override
    public void updateDownLoadInfo(Context context, DownLoadBean bean) {
        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteUtils.updateDownLoadStatus(bean);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("progress",bean.getProgress());
//        sqLiteDatabase.update("download",contentValues,"story_id=",new String[]{bean.getId()+""});
//        sqLiteDatabase.close();
    }

    @Override
    public void checkDownLoadInfo(Context context, DownLoadBean bean, MainContract.CheckDownLoadCallback callback) {
        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
        //正在下载
        Cursor cursor =
         sqLiteDatabase.query("download",null,"story_id="+bean.getId()+" and status!=3",
                null,null,null,null);
        if(cursor.moveToNext()){
            callback.onDownLoadingExist();
            return;
        }
        //下载完成
         cursor = sqLiteDatabase.query("download",null,"story_id="+bean.getId()+" and status=3",
                        null,null,null,null);
        if(cursor.moveToNext()){
            callback.onDownLoadedExist();
            return;
        }
        callback.onNoExist();
    }

    @Override
    public void insertMusicInfo(Context context, DownLoadBean bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("story_id",bean.getId());
        contentValues.put("name",bean.getName());
        contentValues.put("author",bean.getAuthor());
        contentValues.put("url",bean.getUrl());
        contentValues.put("type",bean.getType());
        contentValues.put("count",0);
        contentValues.put("progress",0);
        contentValues.put("date",GetDateUtils.getDate());
    }

//    @Override
//    public List<DownLoadBean> getDownLoadInfo(Context context) {
//        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
//        SQLiteDatabase sqLiteDatabase = sqLiteUtils.getReadableDatabase();
//        String sql = "SELECT * FROM download ORDER BY _id ASC";
//        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
//        while (cursor.moveToNext()){
//            DownLoadBean downLoadBean = new DownLoadBean();
//            downLoadBean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
//            downLoadBean.setName(cursor.getString(cursor.getColumnIndex("name")));
//            downLoadBean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
//            downLoadBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//            downLoadBean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
//            list.add(downLoadBean);
//        }
//        sqLiteDatabase.close();
//        return list;
//    }
}
