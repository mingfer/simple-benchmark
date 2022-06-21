package cn.mingfer.benchmark;

import cn.mingfer.benchmark.reporter.ConsoleReporter;
import cn.mingfer.benchmark.reporter.Reporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.time.Duration;

class BenchmarkTest {


    @Test
    @DisplayName("正常用例：创建测试计时压测器")
    public void test_timing() {
        Benchmark.ofTiming(Duration.ofSeconds(5), 1,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .benchmark();
    }

    @Test
    @DisplayName("正常用例：配置项")
    public void test_config() {
        Benchmark.ofTiming(Duration.ofSeconds(5), 1,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .name("配置项测试")
                .addReporter(new ConsoleReporter())
                .benchmark();
    }


}