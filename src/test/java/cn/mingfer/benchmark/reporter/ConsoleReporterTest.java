package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

class ConsoleReporterTest {

    private final static Benchmark<?> mock =
            Benchmark.ofCount(1, 1, () -> KeyPairGenerator.getInstance("RSA"));

    @Test
    public void test_print() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.statistics(mock);
    }

    @Test
    @DisplayName("设置频率")
    public void test_frequency() throws InterruptedException {
        new ConsoleReporter()
                .frequency(Duration.ofSeconds(2))
                .reportStart(1000, mock);
        Thread.sleep(3000);
    }

    @Test
    @DisplayName("设置时间格式")
    public void test_date_format() throws InterruptedException {
        new ConsoleReporter()
                .datetimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .reportStart(1000, mock);
        Thread.sleep(3000);
    }

}