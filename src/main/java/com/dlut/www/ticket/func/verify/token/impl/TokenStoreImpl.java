package com.dlut.www.ticket.func.verify.token.impl;

import com.dlut.www.ticket.func.verify.token.TokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TokenStoreImpl implements TokenStore {
    private final ConcurrentHashMap<String, List<String>> tokenStore;

    public TokenStoreImpl(){
        this.tokenStore = new ConcurrentHashMap<>();
    }

    @Override
    public Map<String, List<String>> getTokens() {
        return this.tokenStore;
    }

    @Override
    public List<String> getTokens(String host) {
        return this.tokenStore.get(host);
    }

    @Override
    public boolean addToken(String host, String token) {
        List<String> tokens = this.tokenStore.getOrDefault(host, new ArrayList<>());
        tokens.add(token);
        this.tokenStore.put(host, tokens);
        return true;
    }

    @Override
    public boolean addTokens(String host, List<String> tokens) {
        List<String> token = this.tokenStore.getOrDefault(host, new ArrayList<>());
        token.addAll(tokens);
        this.tokenStore.put(host, token);
        return true;
    }

    @Override
    public boolean removeAll() {
        this.tokenStore.clear();
        return true;
    }

    @Override
    public boolean removeToken(String host, String token) {
        if(this.tokenStore.containsKey(host)){
            this.tokenStore.get(host).remove(token);
            return true;
        } else {
            log.warn("cannot find host = " + host);
            return false;
        }
    }

    @Override
    public boolean removeTokens(String host, List<String> tokens) {
        if(this.tokenStore.containsKey(host)){
            this.tokenStore.get(host).removeAll(tokens);
            return true;
        } else {
            log.warn("cannot find host = " + host);
            return false;
        }
    }
}
