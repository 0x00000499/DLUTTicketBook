package com.dlut.www.ticket.func.schedule;

import com.dlut.www.ticket.func.schedule.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Scheduler {
    @Autowired
    Task task;
    @Value("${dlut.date}")
    private String date;
    @Value("${dlut.retries}")
    private int retries;
    private static final String SHOW_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public void schedule(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(SHOW_TIME_PATTERN);
        LocalDateTime timeStartToBook = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).minusDays(1).atTime(7, 0,0);
        LocalDateTime currentDateTime = LocalDateTime.now();
        long initDelay = calculateInitialDelay(currentDateTime, timeStartToBook);
        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(4);
        Runnable run = ()->{
            log.info("抢票开始,当前时间：" + LocalDateTime.now().format(timeFormatter));
            // 1. 登陆
            task.authority();
            // 2. 尝试抢票,失败重试retries次
            while (retries-- != 0){
                // 3. 抢票成功的话停止抢票
                if(task.book()) break;
            }
            log.info("抢票结束,当前时间: " + LocalDateTime.now().format(timeFormatter));
        };
        log.info("当前时间 " + LocalDateTime.now().format(timeFormatter) +
                " 抢票任务已经提交,预计于 " + timeStartToBook.format(timeFormatter) + " 开始抢票");
        schedule.schedule(run, initDelay, TimeUnit.SECONDS);
    }
    // 计算延迟时间
    private static long calculateInitialDelay(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration between = Duration.between(now, targetDateTime);
        return Math.max(between.getSeconds(), 0);
    }
}
