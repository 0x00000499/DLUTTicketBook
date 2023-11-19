package com.dlut.www.ticket.func.config;

import com.dlut.www.ticket.func.verify.cookie.CookieJarImpl;
import com.dlut.www.ticket.func.verify.cookie.cookiestore.CookieStore;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DLUTConfiguration {
    @Autowired
    CookieStore cookieStore;
    @Bean
    public OkHttpClient getHttpClient(){
        return new OkHttpClient().newBuilder().cookieJar(new CookieJarImpl(cookieStore)).followRedirects(false).build();
    }
}
