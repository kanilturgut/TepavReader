package com.tepav.reader.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 14:24
 */
public class Publications implements Serializable{

    String yayin_id;
    String ytitle;
    String ydate;
    String yauthors;
    String ycontent;
    String ytpe;
    String ytype_id;
    String id;
    String date;

    //user_id
    List<String> likes;

    List<File> files;

    public String getYayin_id() {
        return yayin_id;
    }

    public void setYayin_id(String yayin_id) {
        this.yayin_id = yayin_id;
    }

    public String getYtitle() {
        return ytitle;
    }

    public void setYtitle(String ytitle) {
        this.ytitle = ytitle;
    }

    public String getYdate() {
        return ydate;
    }

    public void setYdate(String ydate) {
        this.ydate = ydate;
    }

    public String getYauthors() {
        return yauthors;
    }

    public void setYauthors(String yauthors) {
        this.yauthors = yauthors;
    }

    public String getYcontent() {
        return ycontent;
    }

    public void setYcontent(String ycontent) {
        this.ycontent = ycontent;
    }

    public String getYtpe() {
        return ytpe;
    }

    public void setYtpe(String ytpe) {
        this.ytpe = ytpe;
    }

    public String getYtype_id() {
        return ytype_id;
    }

    public void setYtype_id(String ytype_id) {
        this.ytype_id = ytype_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
