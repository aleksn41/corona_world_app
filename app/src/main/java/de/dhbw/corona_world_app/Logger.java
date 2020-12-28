package de.dhbw.corona_world_app;

import android.util.Log;

public class Logger {

    static boolean logOn = true;

    public static void logD(String tag, String msg){
        if(logOn==true){
            Log.d(tag, msg);
        }
    }

    public static void logE(String tag, String msg){
        if(logOn==true){
            Log.e(tag, msg);
        }
    }

    public static void logE(String tag, String msg, Throwable throwable){
        if(logOn==true){
            Log.e(tag, msg, throwable);
        }
    }

    public static void logV(String tag, String msg){
        if(logOn==true){
            Log.v(tag, msg);
        }
    }

    public static void disableLogging(){
        logOn = false;
    }

    public static void enableLogging(){
        logOn = true;
    }
}
