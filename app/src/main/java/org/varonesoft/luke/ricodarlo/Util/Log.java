package org.varonesoft.luke.ricodarlo.Util;

/**
 * Created by luke on 27/10/17.
 * <p>
 * Wrapper for Log
 */

public class Log {

    // Default TAG
    private final static String TAG = "Ricordalo";

    // Debug
    private final static boolean DEBUG = true;

    public static void d(String msg) {
        if (DEBUG) d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG) android.util.Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG) android.util.Log.w(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG) android.util.Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }
}

