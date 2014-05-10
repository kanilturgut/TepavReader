package com.tepav.reader.model;

import java.io.Serializable;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 15:21
 */
public class DBData implements Serializable{

    //types
    public static int TYPE_NEWS = 0;
    public static int TYPE_BLOG = 1;
    public static int TYPE_PUBLICATION = 2;

    String id;
    String content;
    int type;


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
