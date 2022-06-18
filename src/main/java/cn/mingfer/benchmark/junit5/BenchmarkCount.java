package cn.mingfer.benchmark.junit5;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Test
@ExtendWith(BenchmarkCountExtension.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BenchmarkCount {

    /**
     * @return 压测线程数，参见 {@link Benchmark#threads()}，默认一个线程
     */
    int threads() default 1;

    /**
     * @return 压测总次数，默认为 1 次
     */
    int count() default 1;

    /**
     * @return 压测前的预热次数，默认为 0 次
     */
    int warmUp() default 0;
}
