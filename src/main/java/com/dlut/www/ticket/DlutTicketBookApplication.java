package com.dlut.www.ticket;

import com.dlut.www.ticket.func.schedule.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DlutTicketBookApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        ConfigurableApplicationContext context = SpringApplication.run(DlutTicketBookApplication.class, args);
        Scheduler scheduler = context.getBean(Scheduler.class);
        scheduler.schedule();
    }

}
