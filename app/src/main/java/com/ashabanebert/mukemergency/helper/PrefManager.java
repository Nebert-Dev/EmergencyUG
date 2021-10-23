package com.ashabanebert.mukemergency.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "PREF_MANAGER";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public int getUserid() {
        return pref.getInt("userid", 0);
    }

    public void setUserid(int userid) {
        editor.putInt("userid", userid);
        editor.commit();
    }

    public int getUid() {
        return pref.getInt("uid", 0);
    }

    public void setUid(int uid) {
        editor.putInt("uid", uid);
        editor.commit();
    }


}