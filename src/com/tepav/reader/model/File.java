package com.tepav.reader.model;

import java.io.Serializable;

/**
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 14:17
 */
public class File implements Serializable{

    String file_id;
    String name;
    String url;
    String id;

    public File() {}

    public File(String file_id, String name, String url, String id) {
        this.file_id = file_id;
        this.name = name;
        this.url = url;
        this.id = id;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
