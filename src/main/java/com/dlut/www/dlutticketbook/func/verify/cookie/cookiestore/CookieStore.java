package com.dlut.www.dlutticketbook.func.verify.cookie.cookiestore;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.Set;

public interface CookieStore {
    void add(HttpUrl url, List<Cookie> cookies);
    void add(HttpUrl url, Cookie cookie);
    List<Cookie> get(String host);
    List<Cookie> getCookies();
    Set<String> getHosts();
    boolean remove(String host, Cookie cookie);
    boolean removeAll();
    Cookie get(String host, String cookieName);
}
