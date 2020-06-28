package com.dsg.androidperformance.tasks;

import com.dsg.androidperformance.launcherStarter.task.Task;
import com.tencent.bugly.crashreport.CrashReport;

public class InitBuglyTask extends Task {

    @Override
    public void run() {
        CrashReport.initCrashReport(mContext, "注册时申请的APPID", false);
    }
}
