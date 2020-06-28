package com.dsg.androidperformance;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * @author DSG
 * @Project AndroidPerformance
 * @date 2020/6/28
 * @describe
 */
public class PerformanceApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initAMap();
        initBugly();
        initGetDeviceId();
        initJPush();
        initUmengBug();
    }


    private void initGetDeviceId() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initJPush() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initUmengBug() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initBugly() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initAMap() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
