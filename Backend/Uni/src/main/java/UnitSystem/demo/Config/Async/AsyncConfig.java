package UnitSystem.demo.Config.Async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@EnableAsync
@Configuration
@Slf4j
public class AsyncConfig   implements AsyncConfigurer {


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (exception, method, params) -> {
            log.error("Async method '{}' threw exception: {}",
                    method.getName(), exception.getMessage());
        };
    }

    @Bean(name = "auditLogExecutor")
    public Executor auditExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);

        executor.setMaxPoolSize(20);
        
        executor.setQueueCapacity(100);
        
        executor.setThreadNamePrefix("Audit-");
        
        executor.initialize();
        return executor;
    }

}
