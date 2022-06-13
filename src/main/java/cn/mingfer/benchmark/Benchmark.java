package cn.mingfer.benchmark;

import cn.mingfer.benchmark.reporter.Reporter;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class Benchmark {
    protected final Set<Reporter> reporters = new HashSet<>();
    /**
     * 待执行的测试内容
     */
    protected final Executable executable;

    /**
     * 执行测试的线程池
     */
    protected final ExecutorService service;

    /**
     * 用于等待所有线程完成任务
     */
    protected final CountDownLatch countDownLatch;

    /**
     * 测试线程数
     */
    protected final int threads;
    private String name = "";
    private String desc = "";

    public static TimingBenchmark ofTiming(Duration duration, int threads, Executable executable) {
        return new TimingBenchmark(duration, threads, executable);
    }

    public abstract CountDownLatch prepare();


    public void warmUp() {
        try {
            executable.execute();
        } catch (Throwable ignored) {
        }
    }

    public void execute() {
        try {
            long start = System.nanoTime();
            executable.execute();
            reporters.forEach(reporter -> reporter.reportSuccess(System.nanoTime() - start));
        } catch (Throwable throwable) {
            reporters.forEach(reporter -> reporter.reportFailed(throwable));
        }
    }

    public void benchmark() {
        try {
            final CountDownLatch prepared = prepare();
            prepared.await();
            reporters.forEach(reporter -> reporter.reportStart(System.currentTimeMillis(), this));
            countDownLatch.await();
            service.shutdownNow();
            reporters.forEach(reporter -> reporter.statistics(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Benchmark(int threads, Executable executable) {
        this.threads = threads;
        this.executable = Objects.requireNonNull(executable);
        service = Executors.newFixedThreadPool(threads, new ThreadFactory() {
            int count = 1;

            public Thread newThread(Runnable r) {
                return new Thread(r, "simple-benchmark-thread-" + count++);
            }
        });
        countDownLatch = new CountDownLatch(threads);
    }

    public String getName() {
        return name;
    }

    public Benchmark setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public Benchmark setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public Benchmark addReporter(Reporter reporter) {
        reporters.add(reporter);
        return this;
    }

    public int getThreads() {
        return threads;
    }
}
