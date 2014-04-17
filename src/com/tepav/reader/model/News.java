package com.tepav.reader.model;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.tepav.reader.helpers.HttpURL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
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

    public static News fromJSON(JSONObject jsonObject) throws JSONException {

        News news = new News();

        news.setHaber_id(jsonObject.getString("haber_id"));
        news.setHtitle(jsonObject.getString("htitle"));
        news.setHcontent(jsonObject.getString("hcontent"));
        news.setHdate(jsonObject.getString("hdate"));
        news.setDname(jsonObject.getString("dname"));
        news.setHimage(jsonObject.getString("himage"));
        news.setId(jsonObject.getString("_id"));
        news.setDate(jsonObject.getString("date"));

        JSONArray filesArray = jsonObject.getJSONArray("files");
        List<File> fileList = new LinkedList<File>();

        for (int i = 0 ; i < filesArray.length() ; i++){
            fileList.add((File) filesArray.get(i));
        }

        news.setFiles(fileList);

        return news;
    }

}
