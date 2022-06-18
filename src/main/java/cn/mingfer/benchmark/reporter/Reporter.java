package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 将测试数据形成报告
 */
public abstract class Reporter {

    protected final AtomicLong successCounts = new AtomicLong(0);

    /**
     * 记录失败请求的总数
     */
    protected final AtomicLong failedCounts = new AtomicLong(0);

    /**
     * 请求的时间之和，单位微秒
     */
    protected final AtomicLong timeConsuming = new AtomicLong(0);
    /**
     * 最小耗时，单位微秒
     */
    protected volatile long maxTimeConsuming = 0;
    /**
     * 最大耗时，单位微秒
     */
    protected volatile long minTimeConsuming = Integer.MAX_VALUE;
    protected volatile long startTimestamp = 0;

    /**
     * 获取一个打印信息到控制台的报告器
     *
     * @return {@link ConsoleReporter}
     */
    public static Reporter console() {
        return ConsoleReporter.CONSOLE;
    }

    public void reportSuccess(long timeout) {
        successCounts.incrementAndGet();
        timeConsuming.addAndGet(timeout / 1000L);
        if (timeout / 1000L < minTimeConsuming) {
            this.minTimeConsuming = timeout / 1000L;
        }
        if (timeout / 1000L > maxTimeConsuming) {
            this.maxTimeConsuming = timeout / 1000L;
        }
    }

    public void reportFailed(Throwable throwable) {
        failedCounts.incrementAndGet();
        throwable.printStackTrace();
    }

    public void reportStart(long timestamp, Benchmark<?> benchmark) {
        this.startTimestamp = timestamp;
        this.successCounts.set(0);
        this.failedCounts.set(0);
        this.timeConsuming.set(0);
        this.minTimeConsuming = Integer.MAX_VALUE;
        this.maxTimeConsuming = 0;
    }

    public abstract void statistics(Benchmark<?> benchmark);

}
