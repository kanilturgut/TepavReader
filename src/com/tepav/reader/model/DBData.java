package com.tepav.reader.model;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:21
 */
public class DBData {

    //types
    public static int TYPE_NEWS = 0;
    public static int TYPE_BLOG = 1;
    public static int TYPE_PUBLICATION = 2;

    //read list
    public static int READ_LIST_TRUE = 1;
    public static int READ_LIST_FALSE = 0;

    //favorite list
    public static int FAVORITE_LIST_TRUE = 1;
    public static int FAVORITE_LIST_FALSE = 0;

    //archive
    public static int ARCHIVE_TRUE = 1;
    public static int ARCHIVE_FALSE = 0;

    String id;
    String content;
    int type;
    int readList;
    int favoriteList;
    int archive;

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public int getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(int favoriteList) {
        this.favoriteList = favoriteList;
    }

    public int getReadList() {
        return readList;
    }

    public void setReadList(int readList) {
        this.readList = readList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
