package de.dhbw.corona_world_app;

import android.util.Log;

public class Logger {

    static boolean logOn = true;

    public static void logD(String tag, String msg){
        if(logOn){
            Log.d(tag, msg);
        }
    }

    public static void logV(String tag, String msg){
        if(logOn){
            Log.v(tag, msg);
        }
    }

    public static void logE(String tag, String msg){
        if(logOn){
            Log.e(tag, msg);
        }
    }

    public static void logE(String tag, String msg, Throwable throwable){
        if(logOn){
            Log.e(tag, msg, throwable);
        }
    }

    public static boolean getDebugging(){return !logOn; }

    public static void disableLogging() {
        logOn = false;
    }
}
