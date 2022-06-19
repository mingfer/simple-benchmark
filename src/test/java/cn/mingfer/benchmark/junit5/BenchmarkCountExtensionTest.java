package cn.mingfer.benchmark.junit5;

import org.junit.jupiter.api.DisplayName;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;

class BenchmarkCountExtensionTest {

    // tag::doc[]
    @DisplayName("计数性能测试")
    @BenchmarkCount(count = 100, threads = 10)
    public void test() throws GeneralSecurityException {
        KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
    // end::doc[]

}