package com.dsg.androidperformance.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dsg.androidperformance.PerformanceApp;
import com.dsg.androidperformance.launcherStarter.task.Task;


public class GetDeviceIdTask extends Task {
    private String mDeviceId;

    @Override
    public void run() {
        // 真正自己的代码
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        mDeviceId = "2121312312321312312312312";
        PerformanceApp app = (PerformanceApp) mContext;
        app.setDeviceId(mDeviceId);
    }
}
