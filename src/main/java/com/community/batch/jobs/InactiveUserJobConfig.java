package com.community.batch.jobs;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.jobs.readers.QueueItemReader;
import com.community.batch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by KimYJ on 2018-03-07.
 */

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class InactiveUserJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final UserRepository userRepository;

    @Bean
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .start(inactiveJobStep())
                .build();
    }

    @Bean
    public Step inactiveJobStep() {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User>chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();

//        return stepBuilderFactory.get("inactiveUserStep")
//                .tasklet(new MessageTasklet("World!")) // 실행하는 Tasklet을 지정
//                .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE);
        return new QueueItemReader<>(oldUsers);
    }

    public ItemProcessor<User, User> inactiveUserProcessor() {
        return User::setInactive;
        /*return new ItemProcessor<User, User>() {

            @Override
            public User process(User user) throws Exception {
                return user.setInactive();
            }

        };*/
    }

    public ItemWriter<User> inactiveUserWriter() {
        return ((List<? extends User> users) -> userRepository.saveAll(users));
    }
}
