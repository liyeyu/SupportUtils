package liyeyu.support.utils.utils;

import android.content.Context;
import android.content.SharedPreferences;

import liyeyu.support.utils.manager.BaseManager;

/**
 * Created by Liyeyu on 2016/7/29.
 */
public class SPUtils extends BaseManager {
    public static SharedPreferences sp;
    public static String CONFIG_NAME = "_config";
    static {
        sp =  app.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
    }

    public static void setConfigName(String name){
        CONFIG_NAME = name;
    }

    public static <T> void save(String key,T t){
        if(t==null){
            return;
        }
        if(t instanceof String){
            sp.edit().putString(key,t.toString()).commit();
        }
        if(t instanceof Integer || t instanceof Long
                || t instanceof Double || t instanceof Float){
            sp.edit().putString(key,String.valueOf(t)).commit();
        }
        if(t instanceof Boolean){
            sp.edit().putBoolean(key,(Boolean)t).commit();
        }
    }

    public static <R> R get(String key,R defVaule){
        Object obj = null;
        if(defVaule instanceof String){
            obj = sp.getString(key,defVaule.toString());
        }
        if(defVaule instanceof Integer){
            obj = sp.getInt(key,(Integer)defVaule);
        }
        if(defVaule instanceof Long){
            obj = sp.getLong(key,(Long)defVaule);
        }
        if(defVaule instanceof Double || defVaule instanceof Float){
            obj = sp.getFloat(key,(Float)defVaule);
        }
        if(defVaule instanceof Boolean){
            obj = sp.getBoolean(key,(Boolean)defVaule);
        }
        return (R)obj;
    }
}
