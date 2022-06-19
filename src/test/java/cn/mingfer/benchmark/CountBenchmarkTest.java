package cn.mingfer.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;

class CountBenchmarkTest {

    @Test
    @DisplayName("正常用例：最佳实践")
    public void test() {
        // tag::doc[]
        Benchmark.ofCount(
                        100, // <1>
                        10, // <2>
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair()) // <3>
                .benchmark();
        // end::doc[]
    }

}