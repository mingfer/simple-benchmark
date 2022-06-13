package cn.mingfer.benchmark;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class TimingBenchmark extends Benchmark {
    private final Duration duration;
    private Duration warmUp = Duration.ZERO;

    public TimingBenchmark(Duration duration, int threads, Executable executable) {
        super(threads, executable);
        this.duration = Objects.requireNonNull(duration);
    }

    public TimingBenchmark setWarmUp(Duration warmUp) {
        this.warmUp = Objects.requireNonNull(warmUp);
        return this;
    }

    @Override
    public CountDownLatch prepare() {
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
                    warmUp();
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
                countDownLatch.countDown();
            });
        }
        return wait;
    }
}
