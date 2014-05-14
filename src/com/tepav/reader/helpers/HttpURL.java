package com.tepav.reader.helpers;

/**
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 14:54
 */
public class HttpURL {

    public static String createURL(String url) {
        return HttpURL.domain + url;
    }

    public static final String domain = "http://server.umutozan.com:3000";

    public static final String news = "/news";
    public static final String blog = "/blog";
    public static final String publication = "/publication";

    //like
    public static final String likeNews = "/news/like";
    public static final String likeBLog = "/blog/like";
    public static final String likePublication = "/publication/like";

    //register
    public static final String tepavRegister = "/user/register";

    //login
    public static final String tepavLogin = "/auth";
    public static final String facebookLogin = "/auth/facebook/token";
    public static final String twitterLogin = "/auth/twitter/token";

    // comment
    public static final String addComment = "/comment/add";
    public static final String getComment = "/comment/getComments";

    // share
    public static final String shareNews = "/news/share";
    public static final String shareBLog = "/blog/share";
    public static final String sharePublication = "/publication/share";



}
