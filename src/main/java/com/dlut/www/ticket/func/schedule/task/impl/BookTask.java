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

@Component
@Slf4j
public class BookTask implements Task, ApplicationContextAware {
    private ApplicationContext context;
    @Value("${dlut.sport}")
    private String sport;

    @Override
    public void authority(){
        // 1.先登录
        AuthorityRequest authorityRequest = context.getBean(AuthorityRequest.class);
        authorityRequest.login();
    }

    @Override
    public boolean book(){
        // 2.然后进行订票操作
        try {
            BookAction bookAction = (BookAction) context.getBean(Class.forName("com.dlut.www.ticket.func.action.impl." + sport +"BookAction"));
            return bookAction.bookTicket();
        } catch (ClassNotFoundException e) {
            log.error("类找不到");
            return false;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
