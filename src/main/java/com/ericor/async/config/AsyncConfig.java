package com.ericor.async.config;

import com.ericor.async.CustomAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@ComponentScan("com.ericor.async")
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Spring documentation
     * https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/htmlsingle/#scheduling-annotation-support-async
     * */
    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-example-");
        executor.setWaitForTasksToCompleteOnShutdown(true);

        /***
         * CallerRunsPolicy
         * Instead of throwing an exception or discarding tasks, that policy will simply force the thread that is calling the submit method to run the task itself.
         * The idea is that such a caller will be busy while running that task and not able to submit other tasks immediately.
         * Therefore it provides a simple way to throttle the incoming load while maintaining the limits of the thread pool and queue.
         * Typically this allows the executor to "catch up" on the tasks it is handling and thereby frees up some capacity on the queue, in the pool, or both.
        * */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor();
    }


    /**
     * Exception management with @Async
     When an @Async method has a Future typed return value, it is easy to manage an exception that was
     thrown during the method execution as this exception will be thrown when calling get on the Future result.
     With a void return type however, the exception is uncaught and cannot be transmitted.For those cases,
     an AsyncUncaughtExceptionHandler can be provided to handle such exceptions.
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
