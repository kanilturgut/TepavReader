package com.tepav.reader.model;

/**
 * Author   : kanilturgut
 * Date     : 13/05/14
 * Time     : 17:09
 */
public class User {

    public static User user = null;

    public String fullname;
    public String email;

    public void setNotificationNews(boolean notificationNews) {
        this.notificationNews = notificationNews;
    }

    public void setNotificationBlog(boolean notificationBlog) {
        this.notificationBlog = notificationBlog;
    }

    public void setNotificationPublication(boolean notificationPublication) {
        this.notificationPublication = notificationPublication;
    }

    public boolean notificationNews, notificationBlog, notificationPublication;

    public static void setUser(String fullname, String email, boolean notificationNews, boolean notificationBlog, boolean notificationPublication) {

        if (user == null)
            user = new User(fullname, email, notificationNews, notificationBlog, notificationPublication);
    }

    User(String fullname, String email, boolean notificationNews, boolean notificationBlog, boolean notificationPublication) {
        this.fullname = fullname;
        this.email = email;
        this.notificationNews = notificationNews;
        this.notificationBlog = notificationBlog;
        this.notificationPublication= notificationPublication;
    }

    public static User getInstance() {
        if (user != null)
            return user;

        return null;
    }
}
