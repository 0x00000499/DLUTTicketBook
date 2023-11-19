package com.dlut.www.dlutticketbook.func.verify.token;

import java.util.List;
import java.util.Map;

public interface TokenStore {
    Map<String, List<String>> getTokens();
    List<String> getTokens(String host);
    boolean addToken(String host, String token);
    boolean addTokens(String host, List<String> tokens);
    boolean removeAll();
    boolean removeToken(String host, String token);
    boolean removeTokens(String host, List<String> tokens);
}
