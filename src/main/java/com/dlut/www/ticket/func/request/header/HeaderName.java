package com.dlut.www.ticket.func.request.header;

public enum HeaderName {
    AUTHORITY("authority"),
    ACCEPT("accept"),
    ACCEPT_LANGUAGE("accept_language", "zh-CN,zh;q=0.9"),
    CACHE_CONTROL("cache-control", "no-cache"),
    CONTENT_TYPE("content_type"),
    COOKIE("cookie"),
    ORIGIN("origin"),
    PRAGMA("pragma", "no-cache"),
    REFER("refer"),
    SEC_CH_UA("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"115\", \"Chromium\";v=\"115\""),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile", "?0"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform", "\"macOS\""),
    SEC_FETCH_DEST("sec-fetch-dest"),
    SEC_FETCH_MODE("sec-fetch-mode"),
    SEC_FETCH_SITE("sec-fetch-site"),
    SEC_FETCH_USER("sec-fetch-user"),
    UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests"),
    USER_AGENT("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
    private String name;
    private String value;
    HeaderName(String name, String value){
        this.name = name;
        this.value = value;
    }
    HeaderName(String name){
        this.name = name;
    }
    public String getValue(){
        return this.value;
    }

    public String getName(){
        return this.name;
    }
}
