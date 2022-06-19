package cn.mingfer.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.time.Duration;

class TimingBenchmarkTest {
    @Test
    @DisplayName("正常用例：最佳实践")
    public void test() {
        // tag::doc[]
        Benchmark.ofTiming(
                        Duration.ofSeconds(10), // <1>
                        10, // <2>
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair()) // <3>
                .benchmark();
        // end::doc[]
    }

    @Test
    @DisplayName("正常用例：预热场景")
    public void test_warm_up() {
        Benchmark.ofTiming(Duration.ofSeconds(10), 1,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .warmUp(Duration.ofSeconds(3))
                .benchmark();
    }

    @Test
    @DisplayName("正常用例：当设置的时间段极小时，如为 0 为 1 纳秒")
    public void test_duration_limit() {
        Benchmark.ofTiming(Duration.ZERO, 1,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .warmUp(Duration.ZERO)
                .benchmark();
        Benchmark.ofTiming(Duration.ofNanos(1), 1,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .warmUp(Duration.ofNanos(1))
                .benchmark();
    }
}