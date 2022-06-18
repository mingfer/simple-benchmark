package cn.mingfer.benchmark;

import cn.mingfer.benchmark.reporter.ConsoleReporter;
import cn.mingfer.benchmark.reporter.Reporter;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class Benchmark<T> {
    protected final Set<Reporter> reporters = new HashSet<>();
    protected final Executable executable;
    protected final ExecutorService service;
    protected final int threads;
    private String name = "";

    /**
     * 创建一个计时的性能测试实例
     *
     * @param duration   测试的时间段，不可为 null
     * @param threads    测试线程数，必须为正整数
     * @param executable 测试内容，不可为 null
     * @return {@link TimingBenchmark} 对象
     */
    public static TimingBenchmark ofTiming(Duration duration, int threads, Executable executable) {
        return new TimingBenchmark(duration, threads, executable);
    }

    /**
     * 创建一个计数的性能测试实例
     *
     * @param count      测试次数
     * @param threads    测试线程数
     * @param executable 测试内容，不可为 null
     * @return {@link CountBenchmark} 对象
     */
    public static CountBenchmark ofCount(int count, int threads, Executable executable) {
        return new CountBenchmark(count, threads, executable);
    }

    /**
     * 设置报告生成器
     * <ul>
     *     <li>{@link Reporter#console()} - 将压测信息打印到终端</li>
     * </ul>
     *
     * @param reporter 报告生成器，不可为 null
     * @return 设置了报告生成器的压测对象
     */
    @SuppressWarnings("unchecked")
    public T addReporter(Reporter reporter) {
        reporters.add(Objects.requireNonNull(reporter));
        return (T) this;
    }

    /**
     * 开始压测
     */
    public void benchmark() {
        try {
            final CountDownLatch downLatch = new CountDownLatch(threads);
            if (reporters.isEmpty()) {
                reporters.add(new ConsoleReporter());
            }
            final CountDownLatch prepared = prepare(downLatch);
            prepared.await();
            reporters.forEach(reporter -> reporter.reportStart(System.currentTimeMillis(), this));
            downLatch.await();
            service.shutdownNow();
            reporters.forEach(reporter -> reporter.statistics(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 获取压测内容的命名
     */
    public String name() {
        return name;
    }

    /**
     * 命名压测对象
     *
     * @param name 压测对象名称
     * @return 命名的压测对象
     */
    @SuppressWarnings("unchecked")
    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * @return 获取压测线程数
     */
    public int threads() {
        return threads;
    }

    protected Benchmark(int threads, Executable executable) {
        this.threads = Args.positive(threads, "threads");
        this.executable = Objects.requireNonNull(executable);
        service = Executors.newFixedThreadPool(threads, new ThreadFactory() {
            int count = 1;

            public Thread newThread(Runnable r) {
                return new Thread(r, "simple-benchmark-thread-" + count++);
            }
        });
    }

    protected abstract CountDownLatch prepare(CountDownLatch latch);

    protected void execute() {
        try {
            long start = System.nanoTime();
            executable.execute();
            reporters.forEach(reporter -> reporter.reportSuccess(System.nanoTime() - start));
        } catch (Throwable throwable) {
            reporters.forEach(reporter -> reporter.reportFailed(throwable));
        }
    }
}
