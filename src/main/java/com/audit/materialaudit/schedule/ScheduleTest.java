package com.audit.materialaudit.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduleTest {

    //@Scheduled(cron = "0/2 * * * * ?")
    public void work(){
        log.error("eeeeeeeeeeee");
    }
}
