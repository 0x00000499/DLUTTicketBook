package com.dlut.www.ticket.func.verify.cookie;

import com.dlut.www.ticket.func.verify.cookie.cookiestore.CookieStore;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.Objects;

public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;
    public CookieJarImpl(CookieStore cookieStore){
        if(Objects.isNull(cookieStore)){
            throw new IllegalArgumentException("cookie store cannot be null");
        }
        this.cookieStore = cookieStore;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        this.cookieStore.add(httpUrl, list);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return this.cookieStore.get(httpUrl.host());
    }
}
