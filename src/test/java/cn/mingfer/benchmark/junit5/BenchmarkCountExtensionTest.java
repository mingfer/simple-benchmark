package cn.mingfer.benchmark.junit5;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;

class BenchmarkCountExtensionTest {

    @BenchmarkCount(count = 100, threads = 10)
    public void test() throws GeneralSecurityException {
        KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

}