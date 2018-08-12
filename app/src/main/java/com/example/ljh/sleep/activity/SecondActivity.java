package com.example.ljh.sleep.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.ljh.sleep.utils.SQLiteUtils;

public class SecondActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(SQLiteUtils.SQL_NAME,MODE_PRIVATE,null);
        sqLiteDatabase.delete("download","story_id != 0",null);
//        sqLiteDatabase.execSQL("DROP TABLE download");
//        sqLiteDatabase.execSQL("CREATE TABLE download(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "story_id INTEGER,name VARCHAR,author VARCHAR,url VARCHAR,filepath VARCHAR,length INTEGER," +
//                "filelength INTEGER,progress INTEGER,status INTEGER,date VARCHAR)");
//        sqLiteDatabase.execSQL("CREATE TABLE music(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "story_id INTEGER,name VARCHAR,author VARCHAR,url VARCHAR,duration VARCHAR," +
//                "type VARCHAR,progress INTEGER,count INTEGER,date VARCHAR)");
        sqLiteDatabase.close();
    }
}
