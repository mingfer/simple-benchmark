package cn.mingfer.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;

class CountBenchmarkTest {

    @Test
    @DisplayName("正常用例：最佳实践")
    public void test() {
        new CountBenchmark(100, 10, () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .warmUp(1)
                .benchmark();
    }

}