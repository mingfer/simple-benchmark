package cn.mingfer.benchmark.reporter;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;

class CSVReporterTest {

    @Test
    public void test() throws IOException {
        Benchmark.ofCount(
                        100,
                        10,
                        () -> KeyPairGenerator.getInstance("RSA").generateKeyPair())
                .addReporter(new CSVReporter().resultOutput(System.out))
                .addReporter(CSVReporter.CSV.resultOutput(Files.newOutputStream(Paths.get("target/test.csv"))))
                .benchmark();
    }

}