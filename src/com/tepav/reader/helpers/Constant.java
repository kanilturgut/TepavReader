package com.tepav.reader.helpers;

/**
 *
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 16:12
 */
public class Constant {

    public static final long SPLASH_TRANSITION_TIME = 1500;

    public static final int DRAWERS_PAGE_NUMBER = 4;

    //Cache Info
    public static final int CACHE_TYPE_NEWS = 0;
    public static final int CACHE_TYPE_BLOG = 1;
    public static final int CACHE_TYPE_PUBLICATION = 2;

    public static final String DISK_CACHE_FOR_NEWS = "newsCache.srl";
    public static final String DISK_CACHE_FOR_BLOG = "blogCache.srl";
    public static final String DISK_CACHE_FOR_PUBLICATION = "publicationCache.srl";

    //pdf location
    public static final String PDF_TARGET = "tepavReader/";

    //left menu settings
    public static final int LEFT_MENU_RIGHT_MARGIN = 20;

    //left menu items
    public static final int LEFT_MENU_ITEM_NEWS = 0;
    public static final int LEFT_MENU_ITEM_BLOGS = 1;
    public static final int LEFT_MENU_ITEM_RESEARCH_AND_PUBLICATIONS = 2;
    public static final int LEFT_MENU_ITEM_NOTES = 3;
    public static final int LEFT_MENU_ITEM_REPORTS_AND_PRINTED_PUBLICATIONS = 4;
    public static final int LEFT_MENU_ITEM_MY_READ_LIST = 5;
    public static final int LEFT_MENU_ITEM_FAVORITES = 6;
    public static final int LEFT_MENU_ITEM_ARCHIVE = 7;

    //menu items
    public static final String NEWS = "Haberler";
    public static final String BLOG = "Günlük";
    public static final String RESEARCH_AND_PUBLICATIONS = "Araştırma ve Yayınlar";
    public static final String NOTES = "Notlar";
    public static final String REPORTS = "Raporlar";
    public static final String PRINTED_PUBLICATIONS = "Basılı Yayınlar";
    public static final String MY_READ_LIST = "Okuma Listem";
    public static final String FAVORITES = "Favoriler";
    public static final String ARCHIVE = "Okuduklarım";


    //share url's
    public static final String SHARE_NEWS = "http://www.tepav.org.tr/tr/haberler/s/";
    public static final String SHARE_BLOG = "http://www.tepav.org.tr/tr/blog/s/";
    public static final String SHARE_PUBLICATION = "http://www.tepav.org.tr/tr/yayin/s/";

    //shared preferences
    public static final String SP_LOGIN = "login_preferences";

    // Details of post (Whether normal QuickAction or QuickActionForList)
    public static int DETAILS_FROM_POST = 0;
    public static int DETAILS_FROM_LIST = 1;

    // Default font size
    public static int DEFAULT_FONT_SIZE = 16;

}
