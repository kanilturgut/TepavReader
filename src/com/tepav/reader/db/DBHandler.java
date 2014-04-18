package com.tepav.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:05
 */
public class DBHandler extends SQLiteOpenHelper{

    static final int VERSION = 1;
    static final String DATABASE = "tepavReader.db";

    static final String TABLE_DATA = "data";

    static final String COL_ID = "_id";
    static final String COL_CONTENT = "content";
    static final String COL_TYPE = "type";
    static final String COL_READ_LIST = "read_list";
    static final String COL_FAVORITE_LIST = "favorite_list";
    static final String COL_ARCHIVE = "archive";

    public DBHandler(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_DATA
                + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CONTENT + " TEXT, "
                + COL_TYPE + " INTEGER, "
                + COL_READ_LIST + " INTEGER, "
                + COL_FAVORITE_LIST + " INTEGER, "
                + COL_ARCHIVE + " INTEGER"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXIST " + TABLE_DATA);
        onCreate(db);
    }

    public static boolean insert() {



        return false;
    }
}
