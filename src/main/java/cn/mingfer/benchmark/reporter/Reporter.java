package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Args;
import cn.mingfer.benchmark.Benchmark;

import java.io.OutputStream;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
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

    protected OutputStream out = System.out;
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    protected Duration frequency = Duration.ofSeconds(1);
    protected ScheduledFuture<?> future;
    protected boolean statistics = false;

    public Reporter resultOutput(OutputStream stream) {
        this.out = Args.notNull(stream, "stream");
        return this;
    }

    public Reporter datetimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = Objects.requireNonNull(dateTimeFormatter);
        return this;
    }

    public Reporter frequency(Duration frequency) {
        this.frequency = Objects.requireNonNull(frequency);
        return this;
    }

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
        final Thread thread = new Thread(() -> statistics(benchmark));
        Runtime.getRuntime().addShutdownHook(thread);
    }

    public abstract void statistics(Benchmark<?> benchmark);



}
