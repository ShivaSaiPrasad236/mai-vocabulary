package com.my_projects.mai_vocabulary.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;

@Configuration
public class AsyncExceptionHandler implements AsyncConfigurer {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    AsyncExceptionHandler.class
            );

    @Override
    public AsyncUncaughtExceptionHandler
    getAsyncUncaughtExceptionHandler() {

        return (throwable, method, params) ->
                logger.error(
                        "Async operation failed: {}",
                        method.getName(),
                        throwable
                );
    }
}
