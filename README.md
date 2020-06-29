## 深入启动优化

#### 前言
	启动过程是面向用户的第一体验 如果启动过慢或者崩溃 那么卸载概率就会大大增加 
	有一个八秒定律是说 如果应用在8秒内没有启动完成 那么大概率会被卸载
	所以启动优化还是非常有必要的步骤
	
#### 老规矩(Show me the code)
[Talk is cheap](https://github.com/lyp82nlf/AndroidPerformance)
	
我们做启动优化的过程主要要思考三个问题:

- 如何测量启动时间
- 有哪些方法可以降低启动时间
- 线上监测如何实现

### 如何测量启动时间?
测量启动时间有很多种 最简单的一种就是通过打日志实现

#### 1.日志类实现
```
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
```

我们可以通过打日志的方式获取启动时间 

优点是可以带入线上使用,但是也有几个缺点:

- 代码侵入太强 不够优雅
- 可维护性变差

#### 2.AOP方式实现
我们可以通过AOP方式实现无侵入式的日志打印 

```
@Aspect
public class PerformanceAop {

    // 第一个*表示任意返回值
    @Around("call(* com.dsg.androidperformance.PerformanceApp.**(..))")
    public void getTime(ProceedingJoinPoint joinPoint) {
        // 签名
        Signature signature = joinPoint.getSignature();
        String name = signature.toShortString();
        long time = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LogUtils.i(name + " cost " + (System.currentTimeMillis() - time));
    }
}
```

#### 3.使用traceView
在需要监测的代码前后分别加入` Debug.startMethodTracing("App");` 和`Debug.stopMethodTracing();` 然后通过Profile可以查看TraceView

![WechatIMG8.png](https://upload-images.jianshu.io/upload_images/11006838-8b9579ed8501adf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以通过火焰图或者Top Down等等查看耗时

当然也可以使用AS提供的Profile工具 差不多

缺点:
	traceView使用开销比较严重 可能会待偏优化方向
	
#### 4.adb命令方式
```
adb shell am start -W 包名/包名.MainActivity
```
可以通过adb获取启动到指定Activity的耗时

#### 5.systrace
个人比较喜欢用这种方式 轻量级 开销小 显示信息也非常的全
通过命令

通过`python systrace.py --list-cate-categories`查看手机支持的systrace类型

1. `TraceCompat.beginSection("ApponCreate")`
2. `TraceCompat.endSection()`
3. 运行python脚本

```
python systrace.py -t 5 -o ~/Downloads/mytrace.html -a com.dsg.androidperformance sched gfx view wm am res sync
```
systrace.py 存放在sdk下Platform-tools/systrace下

-o 为指定目录

gfx view wm am res sync 为指定tag

cputime和walltime区别

   1. cputime是代码消耗cpu的时间（重点指标）
   2. walltime是代码执行时间
   3. cputime为什么和walltime不一样，举例：锁冲突

### 如何启动优化?
主要思路是异步优化和延迟优化 主要优化方向是CPU Time

#### 异步优化

##### 1.线程池
```
ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
threadPoolExecutor.execute(initAMap());
threadPoolExecutor.execute(initBugly());
threadPoolExecutor.execute(initGetDeviceId());
threadPoolExecutor.execute(initJPush());
threadPoolExecutor.execute(initUmeng());
try {
   countDownLatch.await();
} catch (InterruptedException e) {
   e.printStackTrace();
}
```

通过线程池的方式有几个缺点:

1. 代码不够优雅 可维护性比较差
2. 如果各任务之间有依赖性 比如B依赖A 那么没办法实现
3. 如果A需要在App onCreate生命周期内完成 需要使用countDownLatch来实现 不够优雅 每次改变需要计算 可维护性比较差

##### 2.启动器
为什么我们要设计启动器?

主要就是因为上面几点原因 不可维护 依赖性没法解决

所以实现启动器的时候 我们主要考虑几个点

1. 使用算法排序 解决依赖性
2. 需要在某阶段完成
3. 区分CPU密集型和IO密集型

启动器流程

1. 代码Task化，启动逻辑抽象为Task
2. 根据所有任务依赖关系排序生成一个有向无环图
3. 多线程按照排序后的优先级依次执行

抽象Task设计

```
	public interface ITask {
    /**
     * 优先级的范围，可根据Task重要程度及工作量指定；之后根据实际情况决定是否有必要放更大
     *
     * @return
     */
    @IntRange(from = Process.THREAD_PRIORITY_FOREGROUND, to = Process.THREAD_PRIORITY_LOWEST)
    int priority();

    void run();

    /**
     * Task执行所在的线程池，可指定，一般默认
     *
     * @return
     */
    Executor runOn();

    /**
     * 依赖关系
     *
     * @return
     */
    List<Class<? extends Task>> dependsOn();

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     *
     * @return
     */
    boolean needWait();

    /**
     * 是否在主线程执行
     *
     * @return
     */
    boolean runOnMainThread();

    /**
     * 只是在主进程执行
     *
     * @return
     */
    boolean onlyInMainProcess();

    /**
     * Task主任务执行完成之后需要执行的任务
     *
     * @return
     */
    Runnable getTailRunnable();

    void setTaskCallBack(TaskCallBack callBack);

    boolean needCall();
}

```

##### 启动器UML图
![WechatIMG9.png](https://upload-images.jianshu.io/upload_images/11006838-00fa64c2d40f9a28.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
 TaskDispatcher dispatcher = TaskDispatcher.createInstance();

        dispatcher.addTask(new InitAMapTask())
                .addTask(new InitStethoTask())
                .addTask(new InitWeexTask())
                .addTask(new InitBuglyTask())
                .addTask(new InitFrescoTask())
                .addTask(new InitJPushTask())
                .addTask(new InitUmengTask())
                .addTask(new GetDeviceIdTask())
                .start();

        dispatcher.await();
```
具体启动器代码请看源码 没啥逻辑

#### 延迟加载
我们常规的延迟加载 可能使用Handle+postDelay实现 

缺点:

1. 代码不够优雅 可维护性变差
2. 可能会阻塞主线程 时机不方便控制

##### 优化方案
使用`IdleHandler`的特性 在空闲时执行延迟任务

```
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

```


### 线上如何检测启动时间?
- 可以自建APM
- 使用听云等第三方平台
- Android Vitals


### 还有哪些优化点?
- IO优化
	- 建议不出现网络IO
	- 了解启动过程中的所有IO操作
- 数据重排(减少真正磁盘IO)
- 类重排(可以通过ReDex实现类重排 提高加载速度)
- 资源文件重排
- 厂商通道(HardCoder,Hyper Boost等等)

### 其他黑科技

1. 我们可以在启动页先换成一个有背景图的theme,然后启动完成之后oncreate 换成真正的theme
虽然对启动速度没有提高 但是用户体验提高很多,但是如果在低机型机器上 反而会使更加卡顿 所以建议在Android7.0机器以上开启 优化跟手体验
2. 尽量不要在启动过程创建子进程 
3. 提前初始化SharePrefence
4. 类加载优化：提前异步类加载
5. 启动阶段抑制GC
6. CPU锁频


