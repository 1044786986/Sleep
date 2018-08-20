package com.example.ljh.sleep.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.contract.DownLoadContract;

import java.util.ArrayList;
import java.util.List;

public class SQLiteUtils extends SQLiteOpenHelper{
    public static final String SQL_NAME = "ljh";
    private static final int VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;

    public SQLiteUtils(Context context,SQLiteDatabase.CursorFactory factory) {
        super(context, SQL_NAME, factory, VERSION);
//        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE download(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "story_id INTEGER,name VARCHAR,author VARCHAR,url VARCHAR,filepath VARCHAR,length INTEGER," +
                "filelength INTEGER,progress INTEGER,status INTEGER,date VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE music(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "story_id INTEGER,name VARCHAR,author VARCHAR,url VARCHAR,duration VARCHAR," +
                "type VARCHAR,progress INTEGER,count INTEGER,date VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i == 1 && i1 == 2){
            Log.i("aaa","----------数据库升级啦");
        }
    }

    /**
     *
     *
     *
     *
     *
     *
     */

    /**
     * 获取已下载列表信息
     * @param callback
     */
    public void getDownLoaded(DownLoadContract.GetDownLoadCallback callback) {
        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("download",null,"status=3",
                null, null,null,"date");
        while (cursor.moveToNext()){
            DownLoadBean bean = new DownLoadBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//            bean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
            bean.setFilepath(cursor.getString(cursor.getColumnIndex("filepath")));
            list.add(bean);
        }
        sqLiteDatabase.close();
        callback.onSuccess(list);
    }

    /**
     * 获取正在下载列表信息
     * @param callback
     */
    public void getDownLoading(DownLoadContract.GetDownLoadCallback callback) {
        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("download",null,"status!=3",
                null, null,null,"date");
        while (cursor.moveToNext()){
            DownLoadBean bean = new DownLoadBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            bean.setFilepath(cursor.getString(cursor.getColumnIndex("filepath")));
            bean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
            bean.setFilelength(cursor.getInt(cursor.getColumnIndex("filelength")));
            bean.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            list.add(bean);
        }
        sqLiteDatabase.close();
        callback.onSuccess(list);
    }

    /**
     * 更新下载列表的某个任务状态
     * @param downLoadBean
     */
    public void updateDownLoadStatus(DownLoadBean downLoadBean) {
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",downLoadBean.getStatus());
        sqLiteDatabase.update("download",contentValues,"story_id=" + downLoadBean.getId(),null);
        sqLiteDatabase.close();
    }

    /**
     * 插入下载列表
     * @param bean
     */
    public void insertDownLoadInfo(DownLoadBean bean) {
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("story_id",bean.getId());
        contentValues.put("name",bean.getName());
        contentValues.put("author",bean.getAuthor());
        contentValues.put("url",bean.getUrl());
        contentValues.put("filepath",bean.getFilepath());
        contentValues.put("progress",0);
        contentValues.put("length",bean.getFilelength());
        contentValues.put("filelength",bean.getFilelength());
        contentValues.put("date", GetDateUtils.getDate());
        contentValues.put("status",DownLoadBean.DOWNLOAD_WIAT);
        sqLiteDatabase.insert("download",null,contentValues);
        sqLiteDatabase.close();
    }

    public void deleteDownLoadInfo(DownLoadBean bean){
        sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM download WHERE story_id=" + bean.getId());
        sqLiteDatabase.close();
    }

    /**
     * 更新下载进度信息
     * @param bean
     */
    public void updateDownLoadProgress(DownLoadBean bean) {
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("progress",bean.getProgress());
        contentValues.put("length",bean.getLength());
        contentValues.put("filelength",bean.getFilelength());
        sqLiteDatabase.update("download",contentValues,"story_id="+bean.getId(),null);
        sqLiteDatabase.close();
    }

    /**
     * 插入音频信息
     * @param bean
     */
    public void insertMusicInfo(DownLoadBean bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("story_id",bean.getId());
        contentValues.put("name",bean.getName());
        contentValues.put("author",bean.getAuthor());
        contentValues.put("url",bean.getUrl());
        contentValues.put("type",bean.getType());
        contentValues.put("count",0);
        contentValues.put("progress",0);
        contentValues.put("date",GetDateUtils.getDate());
        sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.insert("music",null,contentValues);
        sqLiteDatabase.close();
    }

    /**
     * 移除音频信息
     * @param bean
     */
    public void removeMusicInfo(MusicInfoBean bean){
        sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.delete("music","story_id="+bean.getId(),null);
        sqLiteDatabase.close();
    }

    public List<DownLoadBean> getDownLoadInfo(Context context) {
        List<DownLoadBean> list = new ArrayList<>();
//        SQLiteUtils sqLiteUtils = new SQLiteUtils(context,null);
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "SELECT * FROM download ORDER BY _id ASC";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        while (cursor.moveToNext()){
            DownLoadBean downLoadBean = new DownLoadBean();
            downLoadBean.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
            downLoadBean.setName(cursor.getString(cursor.getColumnIndex("name")));
            downLoadBean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            downLoadBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downLoadBean.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
            list.add(downLoadBean);
        }
        sqLiteDatabase.close();
        return list;
    }
}
