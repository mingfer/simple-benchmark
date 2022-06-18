package cn.mingfer.benchmark.junit5;

import cn.mingfer.benchmark.Benchmark;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class BenchmarkCountExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext context) throws Throwable {
        Optional<BenchmarkCount> benchmark = findBenchmark(context);
        if (benchmark.isPresent() && invocationContext.getTarget().isPresent()) {
            String displayName = context.getDisplayName();
            BenchmarkCount annotation = benchmark.get();
            invocation.skip();
            Benchmark.ofCount(
                            annotation.count(),
                            annotation.threads(),
                            () -> ReflectionUtils.invokeMethod(invocationContext.getExecutable(),
                                    invocationContext.getTarget().get(),
                                    invocationContext.getArguments().toArray(new Object[0])))
                    .name(displayName)
                    .warmUp(annotation.warmUp())
                    .benchmark();
        } else {
            InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext, context);
        }
    }

    public Optional<BenchmarkCount> findBenchmark(ExtensionContext context) {
        if (!context.getTestMethod().isPresent() || !context.getTestInstance().isPresent()) {
            return Optional.empty();
        }

        Method testMethod = context.getTestMethod().get();
        return findAnnotation(testMethod, BenchmarkCount.class);
    }
}