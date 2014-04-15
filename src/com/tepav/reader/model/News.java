package com.tepav.reader.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 13:42
 */
public class News implements Serializable{

    String haber_id;
    String htitle;
    String hcontent;
    String hdate;
    String dname;
    String himage;
    String id;
    String date;
    List<File> files;

    public String getHaber_id() {
        return haber_id;
    }

    public void setHaber_id(String haber_id) {
        this.haber_id = haber_id;
    }

    public String getHtitle() {
        return htitle;
    }

    public void setHtitle(String htitle) {
        this.htitle = htitle;
    }

    public String getHcontent() {
        return hcontent;
    }

    public void setHcontent(String hcontent) {
        this.hcontent = hcontent;
    }

    public String getHdate() {
        return hdate;
    }

    public void setHdate(String hdate) {
        this.hdate = hdate;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getHimage() {
        return himage;
    }

    public void setHimage(String himage) {
        this.himage = himage;
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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
