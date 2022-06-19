package cn.mingfer.benchmark.junit5;

import org.junit.jupiter.api.DisplayName;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;

class BenchmarkTimingExtensionTest {

    // tag::doc[]
    @DisplayName("计时性能测试")
    @BenchmarkTiming(duration = 10, threads = 10)
    public void test() throws GeneralSecurityException {
        KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
    // end::doc[]

}