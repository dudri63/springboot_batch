package com.community.batch.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final InactiveUserJobConfig inactiveUserJobConfig;

    // 매일 5시에 실행
    @Scheduled(cron = "0 0 05 * * ?")
    public void task() {

        try {
            jobLauncher.run(inactiveUserJobConfig.inactiveUserJob(), new JobParameters());
        } catch (Exception e) {
            System.out.println("a");
        }
    }
}
