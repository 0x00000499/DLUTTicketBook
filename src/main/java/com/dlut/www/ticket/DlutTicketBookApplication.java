package com.dlut.www.ticket;

import com.dlut.www.ticket.func.action.BookAction;
import com.dlut.www.ticket.func.dao.DLUTUser;
import com.dlut.www.ticket.func.httprequest.AuthorityRequest;
import com.dlut.www.ticket.func.schedule.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;

@SpringBootApplication
public class DlutTicketBookApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        ConfigurableApplicationContext context = SpringApplication.run(DlutTicketBookApplication.class, args);
        Scheduler scheduler = context.getBean(Scheduler.class);
        scheduler.schedule();
    }

}
