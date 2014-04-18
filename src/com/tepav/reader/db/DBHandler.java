package com.tepav.reader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tepav.reader.model.DBData;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:05
 */
public class DBHandler extends SQLiteOpenHelper{

    static final String TAG = "DBHandler";

    public static DBHandler dbInstance;

    static final int VERSION = 1;
    static final String DATABASE = "tepavReader.db";

    static final String TABLE_DATA = "data";

    static final String COL_ID = "_id";
    static final String COL_CONTENT = "content";
    static final String COL_TYPE = "type";
    static final String COL_READ_LIST = "read_list";
    static final String COL_FAVORITE_LIST = "favorite_list";
    static final String COL_ARCHIVE = "archive";

    public static DBHandler getInstance(Context context) {

        if (dbInstance == null)
            dbInstance = new DBHandler(context.getApplicationContext());

        return dbInstance;
    }

    public DBHandler(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_DATA
                + " ("
                + COL_ID + " TEXT, "
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

    public boolean insert(DBData dbData) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, dbData.getId());
        contentValues.put(COL_CONTENT, dbData.getContent());
        contentValues.put(COL_TYPE, dbData.getType());
        contentValues.put(COL_READ_LIST, dbData.getReadList());
        contentValues.put(COL_FAVORITE_LIST, dbData.getFavoriteList());
        contentValues.put(COL_ARCHIVE, dbData.getArchive());

        try {
            db.insertOrThrow(TABLE_DATA, null, contentValues);
            db.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "ERROR on insert method", e);
            db.close();
            return false;
        }
    }

    //reads all
    public List<DBData> read() throws NullPointerException{

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_DATA;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                List<DBData> dbDataList = new LinkedList<DBData>();

                do {
                    DBData dbData = new DBData();
                    dbData.setId(cursor.getString(0));
                    dbData.setContent(cursor.getString(1));
                    dbData.setType(Integer.parseInt(cursor.getString(2)));
                    dbData.setReadList(Integer.parseInt(cursor.getString(3)));
                    dbData.setFavoriteList(Integer.parseInt(cursor.getString(4)));
                    dbData.setArchive(Integer.parseInt(cursor.getString(5)));

                    dbDataList.add(dbData);
                }while (cursor.moveToNext());

                cursor.close();
                return dbDataList;
            }
        }

        return null;
    }

    public DBData read(String id) throws NullPointerException{

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_DATA + " WHERE " + COL_ID + "='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
           if (cursor.moveToFirst()) {
               DBData dbData = new DBData();
               dbData.setId(cursor.getString(0));
               dbData.setContent(cursor.getString(1));
               dbData.setType(Integer.parseInt(cursor.getString(2)));
               dbData.setReadList(Integer.parseInt(cursor.getString(3)));
               dbData.setFavoriteList(Integer.parseInt(cursor.getString(4)));
               dbData.setArchive(Integer.parseInt(cursor.getString(5)));

               return dbData;
           }
        }

        return null;
    }


    public int update(DBData dbData) throws NullPointerException{

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, dbData.getId());
        contentValues.put(COL_CONTENT, dbData.getContent());
        contentValues.put(COL_TYPE, dbData.getType());
        contentValues.put(COL_READ_LIST, dbData.getReadList());
        contentValues.put(COL_FAVORITE_LIST, dbData.getFavoriteList());
        contentValues.put(COL_ARCHIVE, dbData.getArchive());

        return db.update(TABLE_DATA, contentValues, COL_ID + " = ?", new String[] {dbData.getId()});
    }

    public void delete(DBData dbData) throws NullPointerException{

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DATA, COL_ID + " = ?", new String[] {dbData.getId()});
        db.close();
    }
}
