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

    public static void setUser(String fullname, String email) {

        if (user == null)
            user = new User(fullname, email);
    }

    User(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }

    public static User getInstance() {
        if (user != null)
            return user;

        return null;
    }
}
