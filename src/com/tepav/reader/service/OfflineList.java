package com.tepav.reader.service;

import android.content.Context;
import android.os.AsyncTask;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.model.DBData;

import java.util.LinkedList;
import java.util.List;

/**
 * Author   : kanilturgut
 * Date     : 10/05/14
 * Time     : 21:15
 */
public class OfflineList {

    Context context;
    DBHandler dbHandler;
    public static OfflineList offlineList = null;
    public static List<DBData> favoriteList, readingList, archiveList, likeList;


    public static OfflineList getInstance(Context context) {
        if (offlineList == null)
            offlineList = new OfflineList(context);

        return offlineList;
    }

    private OfflineList(Context context) {

        this.context = context;
        this.dbHandler = DBHandler.getInstance(context);

        favoriteList = new LinkedList<DBData>();
        readingList = new LinkedList<DBData>();
        archiveList = new LinkedList<DBData>();
        likeList = new LinkedList<DBData>();
    }

    public void startReadingFromDatabase() {
        readFavoriteListFromDatabase();
        readReadingListFromDatabase();
        readArchiveListFromDatabase();
        readLikeListFromDatabase();
    }

    public void updateList() {

        destroyList();
        startReadingFromDatabase();
    }

    public void destroyList() {
        favoriteList.clear();
        readingList.clear();
        archiveList.clear();
        likeList.clear();
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

    private void readLikeListFromDatabase() {

        // Archive List
        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_LIKE);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {
                if (dbDatas != null)
                    likeList.addAll(dbDatas);
            }
        }.execute();
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

    public void addItemToLikeListOfTepavService(DBData dbData) {
        likeList.add(dbData);

    }

    public void removeItemFromFavoriteListOfTepavService(DBData dbData) {
        favoriteList.remove(dbData);

    }

    public void removeItemFromReadingListOfTepavService(DBData dbData) {
        readingList.remove(dbData);

    }

    public void removeItemFromArchiveListOfTepavService(DBData dbData) {
        archiveList.remove(dbData);

    }

    public void removeItemFromLikeListOfTepavService(DBData dbData) {
        likeList.remove(dbData);

    }

    public boolean checkIfContains(String tableName, String id) {

        if (tableName.equals(DBHandler.TABLE_FAVORITE)) {
            for (DBData dbData : favoriteList) {
                if (dbData.getId().equals(id))
                    return true;
            }
        } else if (tableName.equals(DBHandler.TABLE_READ_LIST)) {
            for (DBData dbData : readingList) {
                if (dbData.getId().equals(id))
                    return true;
            }
        } else if (tableName.equals(DBHandler.TABLE_ARCHIVE)) {
            for (DBData dbData : archiveList) {
                if (dbData.getId().equals(id))
                    return true;
            }
        } else if (tableName.equals(DBHandler.TABLE_LIKE)) {
            for (DBData dbData : likeList) {
                if (dbData.getId().equals(id))
                    return true;
            }
        }

        return false;
    }

}
