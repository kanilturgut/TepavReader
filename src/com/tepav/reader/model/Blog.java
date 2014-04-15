package com.tepav.reader.model;

import java.io.Serializable;

/**
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 14:21
 */
public class Blog implements Serializable{

    String gunluk_id;
    String btitle;
    String bcontent;
    String bdate;
    String pfullname;
    String ptype;
    String ptitle;
    String pimage;
    String id;
    String date;

    public String getGunluk_id() {
        return gunluk_id;
    }

    public void setGunluk_id(String gunluk_id) {
        this.gunluk_id = gunluk_id;
    }

    public String getBtitle() {
        return btitle;
    }

    public void setBtitle(String btitle) {
        this.btitle = btitle;
    }

    public String getBcontent() {
        return bcontent;
    }

    public void setBcontent(String bcontent) {
        this.bcontent = bcontent;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getPfullname() {
        return pfullname;
    }

    public void setPfullname(String pfullname) {
        this.pfullname = pfullname;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPtitle() {
        return ptitle;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
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
}
