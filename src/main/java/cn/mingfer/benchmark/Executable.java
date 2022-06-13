package cn.mingfer.benchmark;

/**
 * 用于执行测试内容的接口
 */
@FunctionalInterface
public interface Executable {

    /**
     * 执行测试内容，没有异常抛出视为执行成功，有异常抛出视为执行失败
     *
     * @throws Throwable 导致失败的异常
     */
    void execute() throws Throwable;
}
