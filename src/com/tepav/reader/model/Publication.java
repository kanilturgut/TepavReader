package com.tepav.reader.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 21.04.2014
 * Time : 21:45
 */
public class Publication implements Serializable{

    public static String TYPE_NOTES = "Notlar";
    public static String TYPE_PRINTED_PUBLICATIONS = "Basılı Yayınlar";
    public static String TYPE_REPORTS = "Raporlar";

    String yayin_id;
    String ytitle;
    String ydate;
    String yauthors;
    String ycontent;
    String ytype;
    String ytype_id;
    String id;
    List<String> likes;
    List<File> files;
    String date;

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

    public String getYtype() {
        return ytype;
    }

    public void setYtype(String ytype) {
        this.ytype = ytype;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Publication fromJSON(JSONObject jsonObject) throws JSONException {

        Publication publication = new Publication();
        publication.setYayin_id(jsonObject.getString("yayin_id"));
        publication.setYtitle(jsonObject.getString("ytitle"));
        publication.setYdate(jsonObject.getString("ydate"));
        publication.setYauthors(jsonObject.getString("yauthors"));
        publication.setYcontent(jsonObject.getString("ycontent"));
        publication.setYtype(jsonObject.getString("ytype"));
        publication.setYtype_id(jsonObject.getString("ytype_id"));
        publication.setId(jsonObject.getString("_id"));

        JSONArray likersArray = jsonObject.getJSONArray("likes");
        List<String> likers = new LinkedList<String>();
        for (int i = 0; i < likersArray.length(); i++) {
            likers.add(likersArray.getJSONObject(i).toString());
        }
        publication.setLikes(likers);

        JSONArray filesArray = jsonObject.getJSONArray("files");
        List<File> fileList = new LinkedList<File>();
        for (int i = 0 ; i < filesArray.length() ; i++){
            fileList.add(File.fromJSON(filesArray.getJSONObject(i)));
        }
        publication.setFiles(fileList);


        return publication;

    }

    public static JSONObject toJSON(Publication publication) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("yayin_id", publication.getYayin_id());
        jsonObject.put("ytitle", publication.getYtitle());
        jsonObject.put("ydate", publication.getYdate());
        jsonObject.put("yauthors", publication.getYauthors());
        jsonObject.put("ycontent", publication.getYcontent());
        jsonObject.put("ytype", publication.getYtype());
        jsonObject.put("ytype_id", publication.getYtype_id());
        jsonObject.put("_id", publication.getId());

        JSONArray likeArray = new JSONArray();
        for (String like: publication.getLikes()) {
            likeArray.put(like);
        }
        jsonObject.put("likes", likeArray);

        JSONArray filesArray = new JSONArray();
        for (File file: publication.getFiles()) {
            filesArray.put(File.toJSON(file));
        }
        jsonObject.put("files", filesArray);

        return jsonObject;
    }

    public static DBData toDBData(Publication publication) throws JSONException {

        DBData dbData = new DBData();
        dbData.setId(publication.getId());
        dbData.setContent(Publication.toJSON(publication).toString());
        dbData.setType(DBData.TYPE_PUBLICATION);


        return dbData;
    }

    public static Publication fromDBData(DBData dbData) throws JSONException {

        return Publication.fromJSON(new JSONObject(dbData.getContent()));
    }
}
