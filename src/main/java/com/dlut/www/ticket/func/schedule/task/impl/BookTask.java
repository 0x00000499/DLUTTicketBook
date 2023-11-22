package com.dlut.www.ticket.func.schedule.task.impl;

import com.dlut.www.ticket.func.action.BookAction;
import com.dlut.www.ticket.func.httprequest.AuthorityRequest;
import com.dlut.www.ticket.func.schedule.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class BookTask implements Task, ApplicationContextAware {
    private ApplicationContext context;
    @Value("${dlut.sport}")
    private String sport;

    @Override
    public boolean authority(){
        // 1.先登录
        AuthorityRequest authorityRequest = context.getBean(AuthorityRequest.class);
        try {
            return authorityRequest.login();
        } catch (Exception e){
            log.error("登陆失败请检查账号密码是否有问题:" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean book(){
        // 2.然后进行订票操作
        BookAction bookAction = context.getBean(BookAction.class);
        return bookAction.bookTicket();
    }

    @Override
    public void query() {
        BookAction bookAction = context.getBean(BookAction.class);
        bookAction.showFieldFree();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
