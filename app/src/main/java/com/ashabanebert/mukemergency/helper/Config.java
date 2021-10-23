package com.ashabanebert.mukemergency.helper;

public class Config {
    public static String SERVER_ROOT = "http://168.119.185.239";
    public static int MY_SOCKET_TIMEOUT_MS = 5000;
    public static final String SQLITE_DATABASE_NAME = "contacts_db";
    public static final String TOKEN = SERVER_ROOT+"/user";
    public static final String UPDATE = SERVER_ROOT+"/location";
    public static final String LAST_SEEN = SERVER_ROOT+"/location/lastseen/";
    public static final String SYNC_USER = SERVER_ROOT+"/user/sync";
}