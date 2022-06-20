package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleReporterTest {

    @Test
    public void test_print() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.statistics(Benchmark.ofCount(1, 1, () -> KeyPairGenerator.getInstance("RSA")));
    }

}