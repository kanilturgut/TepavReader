package com.tepav.reader.model;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static Blog fromJSON(JSONObject jsonObject) throws JSONException {

        Blog blog = new Blog();

        blog.setGunluk_id(jsonObject.getString("gunluk_id"));
        blog.setBtitle(jsonObject.getString("btitle"));
        blog.setBcontent(jsonObject.getString("bcontent"));
        blog.setBdate(jsonObject.getString("bdate"));
        blog.setPfullname(jsonObject.getString("pfullname"));
        blog.setPtype(jsonObject.getString("ptype"));
        blog.setPtitle(jsonObject.getString("ptitle"));
        blog.setPimage(jsonObject.getString("pimage"));
        blog.setId(jsonObject.getString("_id"));
        blog.setDate(jsonObject.getString("date"));

        return blog;
    }

    public static JSONObject toJSON(Blog blog) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("gunluk_id", blog.getGunluk_id());
        jsonObject.put("btitle", blog.getBtitle());
        jsonObject.put("bcontent", blog.getBcontent());
        jsonObject.put("bdate", blog.getBdate());
        jsonObject.put("pfullname", blog.getPfullname());
        jsonObject.put("ptype", blog.getPtype());
        jsonObject.put("ptitle", blog.getPtitle());
        jsonObject.put("pimage", blog.getPimage());
        jsonObject.put("_id", blog.getId());
        jsonObject.put("date", blog.getDate());

        return jsonObject;
    }
}
