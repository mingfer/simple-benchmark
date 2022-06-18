package cn.mingfer.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数压测器，压测指定次数后停止
 */
public class CountBenchmark extends Benchmark<CountBenchmark> {
    private final int count;

    private int warmUp = 0;

    /**
     * 设置预热的次数
     *
     * @param warmUp 预热次数，必须为正整数
     * @return 设置预热次数后的 {@link CountDownLatch}
     */
    public CountBenchmark warmUp(int warmUp) {
        this.warmUp = Args.notNegative(warmUp, "warmUp");
        return this;
    }

    protected CountBenchmark(int count, int threads, Executable executable) {
        super(threads, executable);
        this.count = count;
    }

    @Override
    protected CountDownLatch prepare(CountDownLatch latch) {
        final CountDownLatch wait = new CountDownLatch(threads);
        final CountDownLatch warmUpWait = new CountDownLatch(threads);
        final int warmUpCount = warmUp;
        final AtomicInteger warmUpExecutedCount = new AtomicInteger();
        final AtomicInteger executedCount = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            warmUpWait.countDown();
            service.execute(() -> {
                try {
                    warmUpWait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                while (warmUpExecutedCount.incrementAndGet() <= warmUpCount) {
                    execute();
                }

                try {
                    wait.countDown();
                    wait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                while (executedCount.incrementAndGet() <= count) {
                    execute();
                }
                latch.countDown();
            });
        }
        return wait;
    }
}
