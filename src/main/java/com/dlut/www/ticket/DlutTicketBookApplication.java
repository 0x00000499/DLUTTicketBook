package com.dlut.www.ticket;

import com.dlut.www.ticket.func.action.BookAction;
import com.dlut.www.ticket.func.dao.DLUTUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DlutTicketBookApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        ConfigurableApplicationContext context = SpringApplication.run(DlutTicketBookApplication.class, args);
        DLUTUser dlutUser = context.getBean(DLUTUser.class);
        BookAction bookAction = (BookAction) context.getBean(Class.forName("com.dlut.www.ticket.func.action.impl." + dlutUser.getSport()+"BookAction"));
        bookAction.bookTicket();
    }

}
