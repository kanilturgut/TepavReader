package com.tepav.reader.model;

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
public class News extends DBData implements Serializable{

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
            fileList.add(File.fromJSON(filesArray.getJSONObject(i)));
        }

        news.setFiles(fileList);

        return news;
    }

    public static JSONObject toJSON(News news) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("haber_id", news.getHaber_id());
        jsonObject.put("htitle", news.getHtitle());
        jsonObject.put("hcontent", news.getHcontent());
        jsonObject.put("hdate", news.getHdate());
        jsonObject.put("dname", news.getDname());
        jsonObject.put("himage", news.getHimage());
        jsonObject.put("_id", news.getId());
        jsonObject.put("date", news.getDate());

        JSONArray jsonArray = new JSONArray();
        for (File file: news.getFiles()) {
            jsonArray.put(File.toJSON(file));
        }
        jsonObject.put("files", jsonArray);

        return jsonObject;
    }

    public static DBData toDBData(News news) throws JSONException {

        DBData dbData = new DBData();
        dbData.setId(news.getId());
        dbData.setContent(News.toJSON(news).toString());
        dbData.setType(DBData.TYPE_NEWS);

        return dbData;
    }

    public static News fromDBData(DBData dbData) throws JSONException {

        return News.fromJSON(new JSONObject(dbData.getContent()));
    }
}
