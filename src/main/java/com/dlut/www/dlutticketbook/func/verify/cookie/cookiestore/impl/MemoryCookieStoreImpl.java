package com.dlut.www.dlutticketbook.func.verify.cookie.cookiestore.impl;

import com.dlut.www.dlutticketbook.func.verify.cookie.cookiestore.CookieStore;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemoryCookieStoreImpl implements CookieStore {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Cookie>> cookies;

    public MemoryCookieStoreImpl() {
        this.cookies = new ConcurrentHashMap<>();
    }

    /**
     * add multiple cookie
     * @param url
     * @param cookies
     */
    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        for(Cookie cookie : cookies){
            if(!cookieIsExpired(cookie)){
                this.add(url, cookie);
            }
        }
    }

    /**
     * add single cookie
     * @param url
     * @param cookie
     */
    @Override
    public void add(HttpUrl url, Cookie cookie) {
        ConcurrentHashMap<String, Cookie> urlCookie = this.cookies.getOrDefault(url.host(), new ConcurrentHashMap<>());
        urlCookie.put(cookie.name(), cookie);
        this.cookies.put(url.host(), urlCookie);
    }

    @Override
    public List<Cookie> get(String host) {
        List<Cookie> res = new ArrayList<>();
        if (this.cookies.containsKey(host)) {
            Collection<Cookie> values = this.cookies.get(host).values();
            for(Cookie cookie : values){
                if(cookieIsExpired(cookie)){
                    this.remove(host, cookie);
                } else {
                    res.add(cookie);
                }
            }
        }
        return res;
    }

    @Override
    public Cookie get(String host, String cookieName) {
        return Objects.requireNonNull(this.cookies.get(host)).get(cookieName);
    }

    /**
     * get All cookies
     * @return all cookies
     */
    @Override
    public List<Cookie> getCookies() {
        List<Cookie> res = new ArrayList<>();
        for(String hostKey : this.cookies.keySet()){
            res.addAll(this.get(hostKey));
        }
        return res;
    }

    @Override
    public Set<String> getHosts() {
        return this.cookies.keySet();
    }

    @Override
    public boolean remove(String host, Cookie cookie) {
        if(this.cookies.containsKey(host) && this.cookies.get(host).containsKey(cookie.name())){
            this.cookies.get(host).remove(cookie.name());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll() {
        this.cookies.clear();
        return true;
    }

    private boolean cookieIsExpired(Cookie cookie){
        return cookie.expiresAt() < System.currentTimeMillis();
    }

}
