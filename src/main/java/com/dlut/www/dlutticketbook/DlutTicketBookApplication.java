package com.dlut.www.dlutticketbook;

import com.dlut.www.dlutticketbook.func.httprequest.AuthorityRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DlutTicketBookApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DlutTicketBookApplication.class, args);
        AuthorityRequest authorityRequest = context.getBean(AuthorityRequest.class);
        authorityRequest.login();
    }

}
