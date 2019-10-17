package com.project.android_kidstories.sharePref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePref {

    private static final String LAST_LOGGED_IN ="LAST_LOGGED_IN";
    private static final String ID_KEY="com.project.android_kidstories_ID_KEY";

    private static SharePref INSTANCE;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCr(savedInstanceState);
//
//        INSTANCE=SharePref.getINSTANCE(this);
//    }
    private SharedPreferences sharedPreferences;

    private SharePref(SharedPreferences sharedPreferences) {
        this.sharedPreferences=sharedPreferences;

    }

    public static synchronized SharePref getINSTANCE(Context context) {
        if(INSTANCE==null){
            //noinspection deprecation
            INSTANCE = new SharePref(PreferenceManager.getDefaultSharedPreferences(context));
        }
        return INSTANCE;
    }

    public void setString(String key, String data) {
        sharedPreferences.edit().putString(key, data).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public SharePref getSharePref() {
        return INSTANCE;
    }

    public void setLastSunAccess(int hour){
        sharedPreferences.edit().putInt(LAST_LOGGED_IN,hour).apply();
    }

    public int getLastLoginInHour(){
        return sharedPreferences.getInt(LAST_LOGGED_IN,1);
    }

    public Long getLoggedUserId(){
        return sharedPreferences.getLong(ID_KEY,-1);
    }

    public void setLoggedUserId(Long id){
        sharedPreferences.edit().putLong(ID_KEY,id).apply();
    }
}
