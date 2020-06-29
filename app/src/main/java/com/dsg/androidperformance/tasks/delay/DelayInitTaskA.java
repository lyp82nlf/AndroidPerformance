package com.dsg.androidperformance.tasks.delay;


import com.dsg.androidperformance.launcherStarter.task.MainTask;
import com.dsg.androidperformance.utils.LogUtils;

public class DelayInitTaskA extends MainTask {

    @Override
    public void run() {
        // 模拟一些操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.i("DelayInitTaskA finished");
    }
}
