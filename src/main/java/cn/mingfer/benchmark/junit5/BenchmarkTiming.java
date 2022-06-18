package cn.mingfer.benchmark.junit5;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 注解一个方法，对该方法进行计时压测
 */
@Test
@ExtendWith(BenchmarkTimingExtension.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BenchmarkTiming {

    /**
     * @return 压测线程数，参见 {@link Benchmark#threads()}，默认一个线程
     */
    int threads() default 1;

    /**
     * @return 压测时长，默认 1 秒
     */
    int duration() default 1;

    /**
     * @return 压测前的预热时间，单位为秒，默认不预热
     */
    int warmUpSeconds() default 0;

    /**
     * @return 压测时长单位，默认为秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

}
