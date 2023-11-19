package com.dlut.www.ticket.func.request.header;

import lombok.Getter;
import okhttp3.Headers;

public class LoginBaseHeader {
    @Getter
    private final static Headers baseHeaders = new Headers.Builder()
            .add(HeaderName.ACCEPT_LANGUAGE.getName(), HeaderName.ACCEPT_LANGUAGE.getValue())
            .add(HeaderName.CACHE_CONTROL.getName(), HeaderName.CACHE_CONTROL.getValue())
            .add(HeaderName.PRAGMA.getName(), HeaderName.PRAGMA.getValue())
            .add(HeaderName.SEC_CH_UA.getName(), HeaderName.SEC_CH_UA.getValue())
            .add(HeaderName.SEC_CH_UA_MOBILE.getName(), HeaderName.SEC_CH_UA_MOBILE.getValue())
            .add(HeaderName.SEC_CH_UA_PLATFORM.getName(), HeaderName.SEC_CH_UA_PLATFORM.getValue())
            .add(HeaderName.USER_AGENT.getName(), HeaderName.USER_AGENT.getValue()).build();;
    private LoginBaseHeader(){}
}
