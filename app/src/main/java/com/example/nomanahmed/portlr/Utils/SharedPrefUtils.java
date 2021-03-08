package com.example.nomanahmed.portlr.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedPrefUtils {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPrefUtils(Context context) {
        preferences = context.getSharedPreferences("AutoTimeSettings", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void addValue(String value, String key) {
        editor.putString(key,value);
        editor.commit();
        editor.apply();
    }

    public void addValue(boolean value, String key) {
        editor.putBoolean(key,value);
        editor.commit();
        editor.apply();
    }

    public void addValue(Date value, String key) {
        editor.putLong(key,value.getTime());
        editor.commit();
        editor.apply();
    }

    public void addValue(Float value, String key) {
        editor.putFloat(key,value);
        editor.commit();
        editor.apply();
    }

    public void addValue(boolean[] value, String key) {
        for(int i = 0 ; i < value.length ;i++)
            editor.putBoolean(key + "_" + i,value[i]);
        editor.commit();
        editor.apply();
    }

    public String getValue(String key) { return preferences.getString(key,""); }
    public boolean getBoolean(String key) { return preferences.getBoolean(key,false); }
    public Float getFloat(String key) { return preferences.getFloat(key, 0); }
    public Date getDate(String key) {
        long v = preferences.getLong(key, 0);
        if( v == 0) return new Date();
        return new Date(v);
    }
    public boolean[] getBooleanArray(String key) {
        boolean[] array = new boolean[7];
        for(int i=0;i<7;i++)
            array[i] = preferences.getBoolean(key + "_" + i, false);
        return array;
    }

}
