package com.dsg.androidperformance.utils;

import android.util.Log;

/**
 * @author DSG
 * @Project AndroidPerformance
 * @date 2020/6/28
 * @describe
 */
public class LaunchTimer {
    private static long startTime;

    public static void startRecord() {
        startTime = System.currentTimeMillis();
    }

    public static void endRecord() {
        endRecord("");
    }

    public static void endRecord(String msg) {
        long cost = System.currentTimeMillis() - startTime;
        Log.i("main1", msg + " cost time : " + cost);
    }

}
