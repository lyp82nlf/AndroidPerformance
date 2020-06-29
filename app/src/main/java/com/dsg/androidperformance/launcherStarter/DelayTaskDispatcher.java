package com.dsg.androidperformance.launcherStarter;

import android.os.Looper;
import android.os.MessageQueue;

import com.dsg.androidperformance.launcherStarter.task.DispatchRunnable;
import com.dsg.androidperformance.launcherStarter.task.Task;

import java.util.LinkedList;

/**
 * @author DSG
 * @Project AndroidPerformance
 * @date 2020/6/28
 * @describe
 */
public class DelayTaskDispatcher {
    private LinkedList<Task> delayTasks = new LinkedList<>();

    //空闲时间启动
    private MessageQueue.IdleHandler idleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if (!delayTasks.isEmpty()) {
                Task task = delayTasks.poll();
                new DispatchRunnable(task).run();
            }
            return !delayTasks.isEmpty();
        }
    };

    public DelayTaskDispatcher addTask(Task task) {
        delayTasks.add(task);
        return this;
    }

    public void start() {
        Looper.myQueue().addIdleHandler(idleHandler);
    }
}
