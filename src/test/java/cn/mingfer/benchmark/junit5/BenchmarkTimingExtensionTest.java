package cn.mingfer.benchmark.junit5;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;

class BenchmarkTimingExtensionTest {

    @BenchmarkTiming(duration = 10, threads = 10)
    public void test() throws GeneralSecurityException {
        KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

}