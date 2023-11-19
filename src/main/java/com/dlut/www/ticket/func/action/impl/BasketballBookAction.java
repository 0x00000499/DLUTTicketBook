package com.dlut.www.ticket.func.action.impl;

import com.dlut.www.ticket.func.action.BookAction;
import com.dlut.www.ticket.func.verify.token.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasketballBookAction implements BookAction {
    @Autowired
    TokenStore tokenStore;
    @Override
    public void bookTicket() {
        // book basketball ticket
    }
}
