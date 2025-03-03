package dev.hireben.demo.web.bff.infrastructure.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  TaskScheduler taskScheduler() {
    ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, virtualThreadFactory);
    return new ConcurrentTaskScheduler(scheduledExecutorService);
  }

}
