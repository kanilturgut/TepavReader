package com.tepav.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.model.DBData;

import java.util.LinkedList;
import java.util.List;

/**
 * Author   : kanilturgut
 * Date     : 06/05/14
 * Time     : 09:57
 */
public class TepavService extends Service {

    public static List<DBData> favoriteList = new LinkedList<DBData>();
    public static List<DBData> readingList = new LinkedList<DBData>();
    public static List<DBData> archiveList = new LinkedList<DBData>();

    DBHandler dbHandler = null;
    public static TepavService tepavService = null;

    public static TepavService getInstance() {
        if (tepavService == null)
            tepavService = new TepavService();

        return tepavService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHandler = DBHandler.getInstance(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        readFavoriteListFromDatabase();
        readReadingListFromDatabase();
        readArchiveListFromDatabase();

        return START_STICKY;
    }

    private void readFavoriteListFromDatabase() {

        // Favorite List
        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_FAVORITE);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {
                if (dbDatas != null)
                    favoriteList.addAll(dbDatas);
            }
        }.execute();
    }

    private void readReadingListFromDatabase() {

        // Reading List
        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_READ_LIST);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {
                if (dbDatas != null)
                    readingList.addAll(dbDatas);
            }
        }.execute();
    }

    private void readArchiveListFromDatabase() {

        // Archive List
        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_ARCHIVE);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {
                if (dbDatas != null)
                    archiveList.addAll(dbDatas);
            }
        }.execute();
    }

    public List<DBData> getFavoriteListFromTepavService() {
        return favoriteList;
    }

    public List<DBData> getReadingListFromTepavService() {
        return readingList;
    }

    public List<DBData> getArchiveListFromTepavService() {
        return archiveList;
    }

    public void addItemToFavoriteListOfTepavService(DBData dbData) {
        favoriteList.add(dbData);
    }

    public void addItemToReadingListOfTepavService(DBData dbData) {
        readingList.add(dbData);
    }

    public void addItemToArchiveListOfTepavService(DBData dbData) {
        archiveList.add(dbData);
    }

    public void removeItemFromFavoriteListOfTepavService(DBData dbData) {
        favoriteList.remove(dbData);
    }

    public void removeItemToReadingListOfTepavService(DBData dbData) {
        readingList.remove(dbData);
    }

    public void removeItemToArchiveListOfTepavService(DBData dbData) {
        archiveList.remove(dbData);
    }

    public boolean checkIfContains(String tableName, String id) {

        if (tableName.equals(DBHandler.TABLE_FAVORITE)) {
            for (DBData dbData: getFavoriteListFromTepavService()) {
                if (dbData.getId().equals(id))
                    return true;
            }
        } else if (tableName.equals(DBHandler.TABLE_READ_LIST)) {
            for (DBData dbData: getReadingListFromTepavService()) {
                if (dbData.getId().equals(id))
                    return true;
            }
        } else if (tableName.equals(DBHandler.TABLE_ARCHIVE)) {
            for (DBData dbData: getArchiveListFromTepavService()) {
                if (dbData.getId().equals(id))
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}
