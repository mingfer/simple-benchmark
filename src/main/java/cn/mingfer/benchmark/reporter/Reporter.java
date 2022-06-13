package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;
import cn.mingfer.benchmark.recorder.Recorder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 将测试数据形成报告
 */
public abstract class Reporter {

    protected final Set<Recorder> recorders = new HashSet<>();
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

    public static Reporter console() {
        return ConsoleReporter.CONSOLE;
    }

    public void addRecorder(Recorder recorder) {
        recorders.add(Objects.requireNonNull(recorder));
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
    }

    public void reportStart(long timestamp, Benchmark benchmark) {
        this.startTimestamp = timestamp;
    }

    public abstract void statistics(Benchmark benchmark);

}
