package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CSVReporter extends Reporter {
    protected final static Reporter CSV = new CSVReporter();

    @Override
    public void reportStart(long timestamp, Benchmark<?> benchmark) {
        super.reportStart(timestamp, benchmark);
        try {
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // UTF-BOM
            out.write("Time, Success, failed, TPS, RTT\n".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "simple-benchmark-report-thread");
            t.setDaemon(true);
            return t;
        });

        future = executor.scheduleWithFixedDelay(new Runnable() {
            private long lastCounts = 0;
            private long lastTimeConsuming = 0;

            public void run() {
                try {
                    long success = successCounts.get();
                    long failed = failedCounts.get();
                    long timeout = timeConsuming.get();
                    long tps = success - lastCounts;
                    float rtt = (float) (tps == 0 ? 0.00 : (timeout - lastTimeConsuming) / 1000.00 / tps);
                    lastCounts = success;
                    lastTimeConsuming = timeout;
                    out.write((dateTimeFormatter.format(LocalDateTime.now()) + ", " +
                            success + ", " + failed + ", " + tps + ", " + rtt).getBytes());
                    out.write('\n');
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, frequency.getSeconds(), frequency.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void statistics(Benchmark<?> benchmark) {
        if (future != null) {
            future.cancel(true);
        }
        if (!statistics) {
            final long duration = System.currentTimeMillis() - startTimestamp;
            try {
                Thread.sleep(frequency.toMillis() + 10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            final long success = successCounts.get();
            final long failed = failedCounts.get();
            final double rtt = success == 0 ? 0 : timeConsuming.get() / 1000.00 / success;
            String result = "Name, " + benchmark.name() + "\n" +
                    "Threads, " + benchmark.threads() + "\n" +
                    "Duration, " + duration / 1000.00 + "s\n" +
                    "Total Count, " + (success + failed) + "\n" +
                    "Success Count, " + success + "\n" +
                    "Failed Count, " + failed + "\n" +
                    "Average TPS, " + (duration == 0 ? 0 : success * 1000 / duration) + "\n" +
                    "Average RTT, " + new DecimalFormat("0.00").format(rtt) + "ms\n" +
                    "Max Time Consuming, " + maxTimeConsuming / 1000.00 + "ms\n" +
                    "Min Time Consuming, " + minTimeConsuming / 1000.00 + "ms\n";
            try {
                out.write('\n');
                out.write('\n');
                out.write(result.getBytes());
                out.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
            statistics = true;
        }
    }

}