package cn.mingfer.benchmark.junit5;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSVReport {

    /**
     * @return 报告结果写入位置
     */
    String result() default "";

    /**
     * @return 报告输出频率，单位为秒
     */
    int frequency() default 1;

    /**
     * @return 报告中记录的时间格式，默认为 HH:mm:ss
     */
    String dateTimeFormat() default "HH:mm:ss";

}
