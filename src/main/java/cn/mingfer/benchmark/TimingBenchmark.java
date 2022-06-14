package cn.mingfer.benchmark;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 计时压测器，用于压测一段时间内方法的性能
 */
public class TimingBenchmark extends Benchmark<TimingBenchmark> {
    private final Duration duration;
    private Duration warmUp = Duration.ZERO;

    /**
     * 设置预热的时间段
     *
     * @param warmUp 预热的时间段
     * @return 设置预热时间后的 {@link TimingBenchmark} 对象
     */
    public TimingBenchmark warmUp(Duration warmUp) {
        this.warmUp = Objects.requireNonNull(warmUp);
        return this;
    }

    protected TimingBenchmark(Duration duration, int threads, Executable executable) {
        super(threads, executable);
        this.duration = Objects.requireNonNull(duration);
    }

    @Override
    protected CountDownLatch prepare(CountDownLatch latch) {
        final CountDownLatch wait = new CountDownLatch(threads);
        final CountDownLatch warmUpWait = new CountDownLatch(threads);
        final long ms = warmUp.toMillis();
        for (int i = 0; i < threads; i++) {
            warmUpWait.countDown();
            service.execute(() -> {
                try {
                    warmUpWait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                long start = System.currentTimeMillis();
                while ((System.currentTimeMillis() - start) < ms) {
                    execute();
                }

                try {
                    wait.countDown();
                    wait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                start = System.currentTimeMillis();
                while ((System.currentTimeMillis() - start) < duration.toMillis()) {
                    execute();
                }
                latch.countDown();
            });
        }
        return wait;
    }

}
