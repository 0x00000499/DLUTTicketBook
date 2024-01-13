package com.dlut.www.ticket.func.schedule.task.impl;

import com.dlut.www.ticket.func.action.BookAction;
import com.dlut.www.ticket.func.action.AuthorityAction;
import com.dlut.www.ticket.func.schedule.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BookTask implements Task{
    @Value("${dlut.retries}")
    private int retries;
    private static final String SHOW_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AuthorityAction authorityAction;

    @Autowired
    private BookAction bookAction;

    public boolean authority(){
        // 1.先登录
        try {
            return authorityAction.login();
        } catch (Exception e){
            log.error("登陆失败请检查账号密码是否有问题:" + e.getMessage());
            return false;
        }
    }

    @Override
    public void book(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(SHOW_TIME_PATTERN);
        // 进行订票操作
        LocalDateTime startTime = LocalDateTime.now();
        log.info("抢票开始,当前时间：" + startTime.format(timeFormatter));
        // 1. 登陆
        int loginRetries = 100;
        boolean loginRes = false;
        while (loginRetries-- >= 0) {
            loginRes = authority();
            if (loginRes) break;
        }
        if (loginRes) {
            log.info("登陆成功");
            // 1.获取场地信息
            List<String> courtInfos = getAllCourtInfos();
            // 尝试抢票,失败重试retries次
            while (retries-- >= 0) {
                // 2.获取票务信息
                List<String> courtPrices = getAllCourtPrices();
                if(Objects.isNull(courtPrices)) continue;
                // 3.根据场地和票务信息组装请求并发送
                if(createOrder(courtPrices, courtInfos))break;
            }

        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("抢票结束,当前时间: " + endTime.format(timeFormatter) + " 总计用时:" + calculateInitialDelay(startTime, endTime) + "s");
        System.exit(0);
    }

    @Override
    public void query() {
        // 1. 登陆
        int loginRetries = 100;
        boolean loginRes = false;
        while (loginRetries-- >= 0) {
            loginRes = authority();
            if (loginRes) break;
        }
        // 2.查询
        if(loginRes) {
            bookAction.showFieldFree();
        }
        System.exit(0);
    }

    private long calculateInitialDelay(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration between = Duration.between(now, targetDateTime);
        return Math.max(between.getSeconds(), 0);
    }

    @Override
    public List<String> getAllCourtInfos() {
        List<String> courtInfos = null;
        try {
            courtInfos = bookAction.getAllCourtInfos();
        } catch (Exception e){
            log.error("获取场地信息失败:" + e.getMessage());
        }
        return courtInfos;
    }

    @Override
    public List<String> getAllCourtPrices() {
        List<String> courtPrices = null;
        try {
            courtPrices = bookAction.getAllCourtPrice();
        } catch (Exception e) {
            log.error("获取场地票务信息失败" + e.getMessage());
        }
        return courtPrices;
    }

    public boolean createOrder(List<String> courtPrices, List<String> courtInfos){
        boolean res = false;
        try {
            res = bookAction.createOrder(courtPrices, courtInfos);
        } catch (Exception e) {
            log.error("下订单失败" + e.getMessage());
        }
        return res;
    }
}
