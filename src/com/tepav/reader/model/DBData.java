package com.tepav.reader.model;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:21
 */
public class DBData {

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
