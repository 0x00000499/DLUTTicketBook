package com.dlut.www.ticket;

import com.dlut.www.ticket.func.schedule.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Copyright (c) 2023, Gao Xin zhi
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. 本代码仅供个人学习交流使用,请勿用于非法用途
 * 2. 如果代码中有任何侵犯您权益的行为请联系作者删除
 */

@SpringBootApplication
@Slf4j
public class DlutTicketBookApplication {
    public static void main(String[] args){
        if(networkCheck()){
            ConfigurableApplicationContext context = SpringApplication.run(DlutTicketBookApplication.class, args);
            Scheduler scheduler = context.getBean(Scheduler.class);
            scheduler.schedule();
        } else {
            log.error("网络未连接");
        }
    }

    public static boolean networkCheck(){
        String host = "ehall.dlut.edu.cn";
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(2000);
        } catch (Exception e){
            return false;
        }
    }
}
