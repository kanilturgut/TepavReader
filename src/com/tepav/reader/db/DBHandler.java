package com.tepav.reader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.model.DBData;
import com.tepav.reader.service.OfflineList;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:05
 */
public class DBHandler extends SQLiteOpenHelper {

    static final String TAG = "DBHandler";

    public static DBHandler dbInstance;
    static Context ctx;

    static final int VERSION = 2;
    static final String DATABASE = "tepavReader.db";

    public static final String TABLE_READ_LIST = "read_list";
    public static final String TABLE_FAVORITE = "favorite";
    public static final String TABLE_ARCHIVE = "archive";
    public static final String TABLE_LIKE = "like";

    static final String COL_ID = "_id";
    static final String COL_CONTENT = "content";
    static final String COL_TYPE = "type";

    public static DBHandler getInstance(Context context) {

        if (dbInstance == null) {
            dbInstance = new DBHandler(context.getApplicationContext());
            ctx = context;
        }


        return dbInstance;
    }

    public DBHandler(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_READ_LIST
                + " ("
                + COL_ID + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_TYPE + " INTEGER "
                + ");");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITE
                + " ("
                + COL_ID + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_TYPE + " INTEGER "
                + ");");

        db.execSQL("CREATE TABLE " + TABLE_ARCHIVE
                + " ("
                + COL_ID + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_TYPE + " INTEGER "
                + ");");

        db.execSQL("CREATE TABLE " + TABLE_LIKE
                + " ("
                + COL_ID + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_TYPE + " INTEGER "
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READ_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKE);
        onCreate(db);
    }

    public boolean insert(DBData dbData, String table) {

        Logs.i(TAG, "insert operation started");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, dbData.getId());
        contentValues.put(COL_CONTENT, dbData.getContent());
        contentValues.put(COL_TYPE, dbData.getType());

        if (isContain(table, dbData.getId(), db)) {
            Logs.i(TAG, "item already in the db, it will update");
            update(dbData, table);
            return true;
        } else {
            try {
                db.insertOrThrow(table, null, contentValues);
                db.close();
                Logs.i(TAG, "SUCCESS on insert operation");

                OfflineList offlineList = OfflineList.getInstance(ctx);
                if (table.equals(DBHandler.TABLE_READ_LIST)) {
                    offlineList.addItemToReadingListOfTepavService(dbData);
                } else if (table.equals(DBHandler.TABLE_FAVORITE)) {
                    offlineList.addItemToFavoriteListOfTepavService(dbData);
                } else if (table.equals(DBHandler.TABLE_ARCHIVE)) {
                    offlineList.addItemToArchiveListOfTepavService(dbData);
                } else if (table.equals(DBHandler.TABLE_LIKE)) {
                    offlineList.addItemToLikeListOfTepavService(dbData);
                }

                return true;
            } catch (Exception e) {
                Logs.e(TAG, "ERROR on insert method", e);
                db.close();
                return false;
            }
        }
    }

    //reads all
    public List<DBData> read(String table) throws NullPointerException {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + table;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                List<DBData> dbDataList = new LinkedList<DBData>();

                do {
                    DBData dbData = new DBData();
                    dbData.setId(cursor.getString(0));
                    dbData.setContent(cursor.getString(1));
                    dbData.setType(Integer.parseInt(cursor.getString(2)));

                    dbDataList.add(dbData);
                } while (cursor.moveToNext());

                cursor.close();
                return dbDataList;
            }
        }

        return null;
    }

    public int size(String table) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + table;
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }

    public boolean isContain(String table, String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return isContain(table, id, db);
    }

    public boolean isContain(String table, String id, SQLiteDatabase db) {

        String query = "SELECT * FROM " + table + " WHERE " + COL_ID + "='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null)
            return false;
        else if (cursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public DBData read(String table, String id) throws NullPointerException {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + table + " WHERE " + COL_ID + "='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                DBData dbData = new DBData();
                dbData.setId(cursor.getString(0));
                dbData.setContent(cursor.getString(1));
                dbData.setType(Integer.parseInt(cursor.getString(2)));

                return dbData;
            }
        }

        return null;
    }

    public int update(DBData dbData, String table) throws NullPointerException {

        Logs.i(TAG, "update operation started");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, dbData.getId());
        contentValues.put(COL_CONTENT, dbData.getContent());
        contentValues.put(COL_TYPE, dbData.getType());

        return db.update(table, contentValues, COL_ID + " = ?", new String[]{dbData.getId()});
    }

    public void delete(DBData dbData, String table) throws NullPointerException {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table, COL_ID + " = ?", new String[]{dbData.getId()});
        db.close();

        OfflineList offlineList = OfflineList.getInstance(ctx);
        if (table.equals(DBHandler.TABLE_READ_LIST)) {
            offlineList.removeItemFromReadingListOfTepavService(dbData);
        } else if (table.equals(DBHandler.TABLE_FAVORITE)) {
            offlineList.removeItemFromFavoriteListOfTepavService(dbData);
        } else if (table.equals(DBHandler.TABLE_ARCHIVE)) {
            offlineList.removeItemFromArchiveListOfTepavService(dbData);
        } else if (table.equals(DBHandler.TABLE_LIKE)) {
            offlineList.removeItemFromLikeListOfTepavService(dbData);
        }
    }
}
