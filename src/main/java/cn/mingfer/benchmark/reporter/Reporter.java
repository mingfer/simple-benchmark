package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Args;
import cn.mingfer.benchmark.Benchmark;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 将测试数据形成报告
 */
public abstract class Reporter {

    public enum Type {
        CONSOLE, CSV
    }

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
    protected AtomicBoolean statistics = new AtomicBoolean(false);

    protected OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * @param file 设置记录报告结果的文件
     * @return 报告对象
     */
    public Reporter resultOutput(File file) {
        Args.notNull(file, "file");
        File parentFile = file.getAbsoluteFile().getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            boolean mkdirs = parentFile.mkdirs();
            Args.check(mkdirs, "Create directory " + parentFile + " failed");
        }
        try {
            this.out = Files.newOutputStream(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * @param stream 设置记录报告结果的输出流
     * @return 报告对象
     */
    public Reporter resultOutput(OutputStream stream) {
        this.out = Args.notNull(stream, "stream");
        return this;
    }

    /**
     * @param dateTimeFormatter 设置时间格式，默认是 HH:mm:ss
     * @return 报告对象
     */
    public Reporter datetimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = Objects.requireNonNull(dateTimeFormatter);
        return this;
    }

    /**
     * @param frequency 设置报告频率，默认为 1 秒钟
     * @return 报告对象
     */
    public Reporter frequency(Duration frequency) {
        this.frequency = Objects.requireNonNull(frequency);
        return this;
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


    public float cpu() {
        return (float) (bean.getProcessCpuLoad() * 100.0f);
    }

    public String memory() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;

        if (used < 1024) {
            return String.format("%.03dB", used);
        } else if (used / 1024 < 1024) {
            return String.format("%.03fKB", used / 1024.0);
        } else if (used / 1024 / 1024 < 1024) {
            return String.format("%.03fMB", used / 1024.0 / 1024.0);
        } else {
            return String.format("%.03fGB", used / 1024.0 / 1024.0 / 1024.0);
        }
    }
}
