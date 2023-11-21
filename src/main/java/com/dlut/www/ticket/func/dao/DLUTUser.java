package com.dlut.www.ticket.func.dao;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DLUTUser {
    @Value("${dlut.username}")
    private String userName;
    @Value("${dlut.password}")
    private String passWord;
}
