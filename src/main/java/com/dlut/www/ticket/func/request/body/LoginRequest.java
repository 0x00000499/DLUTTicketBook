package com.dlut.www.ticket.func.request.body;

import lombok.Data;

@Data
public class LoginRequest extends BaseRequest{
    // = des encode(username + password + lt, 1, 2, 3) from website js script
    String rsa;
    // username length
    String ul;
    // password length
    String pl;
    // sl ?
    String sl;
    // lt from web html
    String lt;
    String execution;
    String _eventId;
}
