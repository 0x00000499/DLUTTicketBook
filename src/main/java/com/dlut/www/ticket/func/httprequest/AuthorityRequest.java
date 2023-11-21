package com.dlut.www.ticket.func.httprequest;

import com.dlut.www.ticket.func.exception.CustomException;
import com.dlut.www.ticket.func.verify.cookie.cookiestore.CookieStore;
import com.dlut.www.ticket.func.dao.DLUTUser;
import com.dlut.www.ticket.func.request.body.LoginRequest;
import com.dlut.www.ticket.func.consts.ContentType;
import com.dlut.www.ticket.func.consts.HeaderName;
import com.dlut.www.ticket.func.request.header.LoginBaseHeader;
import com.dlut.www.ticket.func.utils.DesUtils;
import com.dlut.www.ticket.func.verify.token.TokenStore;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class AuthorityRequest{
    private static final String loginPageURL = "https://sso.dlut.edu.cn/login";

    private static final String ssoURL = "https://sso.dlut.edu.cn/cas/login?service=http://adm-tycg.dlut.edu.cn/api/login/login";
    @Autowired
    OkHttpClient okHttpClient;
    @Autowired
    CookieStore cookieStore;
    @Autowired
    TokenStore tokenStore;
    @Autowired
    DLUTUser dlutUser;

    @Value("${dlut.keys.firstKey}")
    String firstKey;
    @Value("${dlut.keys.secondKey}")
    String secondKey;
    @Value("${dlut.keys.thirdKey}")
    String thirdKey;

    private String getLoginPage() throws IOException{
        //1. build login request
        Request request = new Request.Builder().url(loginPageURL).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            log.info("Lt and cookie JSESSIONIDCAS has been got");
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private String getLt(String loginPage) {
        // 2. get LT for rsa builder
        Document document = Jsoup.parse(loginPage);
        Element elementLt = document.selectFirst("#lt");
        return Objects.requireNonNull(elementLt).attr("value");
    }

    private LoginRequest requestBuidler(String lt) {
        // 3. build login requestbody
        LoginRequest req = new LoginRequest();
        req.setLt(lt);
        req.setRsa(DesUtils.encode(dlutUser.getUserName() + dlutUser.getPassWord() + lt, firstKey, secondKey, thirdKey));
        req.setSl("0");
        req.setUl(String.valueOf(dlutUser.getUserName().length()));
        req.setPl(String.valueOf(dlutUser.getPassWord().length()));
        req.setExecution("e1s1");
        req.set_eventId("submit");
        return req;
    }

    /**
     * get CASTGC Cookie
     */
    private void cookieCASTGC(LoginRequest requestBody) throws IOException{
        // 4. get cookie CASTGC
        String cookie = "cas_hash=; Language=zh_CN;JSESSIONIDCAS=" + cookieStore.get(HttpUrl.parse(loginPageURL).host(), "JSESSIONIDCAS").value();
        String refer = "https://sso.dlut.edu.cn/cas/login;JSESSIONIDCAS=" + cookieStore.get(HttpUrl.parse(loginPageURL).host(), "JSESSIONIDCAS").value() + "?service=https%3A%2F%2Fportal.dlut.edu.cn%2Ftp%2F";
        Headers headers = new Headers.Builder()
                .add(HeaderName.AUTHORITY.getName(), "sso.dlut.edu.cn")
                .add(HeaderName.ACCEPT.getName(), "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .add(HeaderName.CONTENT_TYPE.getName(), ContentType.APPLICATION_FORM_URLENCODED.getValue())
                .add(HeaderName.COOKIE.getName(), cookie)
                .add(HeaderName.ORIGIN.getName(), "https://sso.dlut.edu.cn")
                .add(HeaderName.REFER.getName(), refer)
                .add(HeaderName.SEC_FETCH_DEST.getName(), "document")
                .add(HeaderName.SEC_FETCH_MODE.getName(), "navigate")
                .add(HeaderName.SEC_FETCH_SITE.getName(), "same-origin")
                .add(HeaderName.SEC_FETCH_USER.getName(), "?1")
                .add(HeaderName.UPGRADE_INSECURE_REQUESTS.getName(), "1")
                .addAll(LoginBaseHeader.getBaseHeaders())
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("rsa", requestBody.getRsa())
                .add("ul", requestBody.getUl())
                .add("pl", requestBody.getPl())
                .add("sl", requestBody.getSl())
                .add("lt", requestBody.getLt())
                .add("execution", requestBody.getExecution())
                .add("_eventId", requestBody.get_eventId())
                .build();

        Request request = new Request.Builder()
                .url(ssoURL)
                .headers(headers)
                .post(formBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            log.info("Cookie CASTGC has been got");
        }
    }

    private String getURLWithTicket() throws IOException{
        String cookie = "CASTGC=" + cookieStore.get(HttpUrl.parse(ssoURL).host(), "CASTGC")
                + ";Language=zh_CN; JSESSIONIDCAS=" + cookieStore.get(HttpUrl.parse(ssoURL).host(), "JESSIONIDCAS")
                + "; path=/; httponly; cas_hash=";
        Headers headers = new Headers.Builder()
                .add(HeaderName.AUTHORITY.getName(), "sso.dlut.edu.cn")
                .add(HeaderName.ACCEPT.getName(), "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .add(HeaderName.COOKIE.getName(), cookie)
                .add(HeaderName.SEC_FETCH_DEST.getName(), "document")
                .add(HeaderName.SEC_FETCH_MODE.getName(), "navigate")
                .add(HeaderName.SEC_FETCH_SITE.getName(), "cross-site")
                .add(HeaderName.SEC_FETCH_USER.getName(), "?1")
                .add(HeaderName.UPGRADE_INSECURE_REQUESTS.getName(), "1")
                .addAll(LoginBaseHeader.getBaseHeaders())
                .build();
        Request req = new Request.Builder()
                .url(ssoURL)
                .headers(headers)
                .build();
        try (Response response = okHttpClient.newCall(req).execute()) {
            log.info("URL with Ticket has been got");
            return response.header("Location");
        }
    }

    // redirect to get token
    private String getToken(String ticketURL) throws IOException{
        Headers headers = new Headers.Builder()
                .addAll(LoginBaseHeader.getBaseHeaders())
                .add(HeaderName.AUTHORITY.getName(), "portal.dlut.edu.cn")
                .add(HeaderName.ACCEPT.getName(), "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .add(HeaderName.SEC_FETCH_DEST.getName(), "document")
                .add(HeaderName.SEC_FETCH_MODE.getName(), "navigate")
                .add(HeaderName.SEC_FETCH_SITE.getName(), "same-site")
                .add(HeaderName.SEC_FETCH_USER.getName(), "?1")
                .add(HeaderName.REFER.getName(), "https://sso.dlut.edu.cn/")
                .add(HeaderName.UPGRADE_INSECURE_REQUESTS.getName(), "1")
                .build();
        Request request = new Request.Builder()
                .url(ticketURL)
                .headers(headers)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String location = response.header("Location");
            if (Objects.isNull(location)) {
                log.info("location get failed");
                return null;
            } else {
                log.info("Ticket has been got");
            }
            return Objects.requireNonNull(location).substring(location.indexOf("token=") + "token=".length());

        }
    }


    public boolean login() throws IOException{
        // 1.JSESSIONIDCAS
        String loginPage = getLoginPage();
        String lt = getLt(loginPage);
        // 2.CASTGC
        LoginRequest loginRequest = requestBuidler(lt);
        cookieCASTGC(loginRequest);
        // 3.TICKET
        String ticketURL = getURLWithTicket();
        // 4.TOKEN
        String token = getToken(ticketURL);
        // 5.Save Token
        this.tokenStore.addToken(HttpUrl.parse(ssoURL).host(), token);
        return Objects.nonNull(token);
    }
}
