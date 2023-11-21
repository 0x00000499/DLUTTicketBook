package com.dlut.www.ticket.func.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dlut.www.ticket.func.consts.ContentType;
import com.dlut.www.ticket.func.consts.HeaderName;
import com.dlut.www.ticket.func.consts.PayWay;
import com.dlut.www.ticket.func.verify.token.TokenStore;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BookAction implements InitializingBean {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private OkHttpClient httpClient;
    @Value("${dlut.date}")
    private String date;
    @Value("${dlut.start_time}")
    private String start_times;
    @Value("${dlut.court}")
    private List<String> courts;
    @Value("${dlut.pay_method}")
    private String payMethod;
    @Value("${dlut.sport}")
    private String sport;
    private String productId;
    private String orderType;
    private String venueId;
    private String mealId;
    private String fieldType;

    @Override
    public void afterPropertiesSet() throws Exception {
        if ("pingPong".equals(sport)){
            productId = "80";
            orderType = "3";
            venueId = "236";
            mealId = "132";
            fieldType = "1";
        } else if("basketball".equals(sport)){
            productId = "78";
            orderType = "3";
            venueId = "236";
            mealId = "128";
            fieldType = "1";
        } else{
            log.error("没有找到对应的运动类参数,请检查运动名称pingPong/basketball");
            throw new Exception();
        }
    }
    public boolean bookTicket(){
        List<String> courtPrices = null;
        List<String> courtInfos = null;
        // 1.获取场地信息
        try {
            courtInfos = getAllCourtInfos();
        } catch (Exception e){
            log.error("获取场地信息失败:" + e.getMessage());
            return false;
        }
        // 2.获取场地的票务信息
        try {
            courtPrices = getAllCourtPrice();
        } catch (Exception e){
            log.error("获取场地票务信息失败" + e.getMessage());
            return false;
        }
        // 3.根据场地和票务信息组装请求并发送
        try {
            return createOrder(courtPrices, courtInfos);
        } catch (Exception e){
            log.error("下订单失败" + e.getMessage());
            return false;
        }
    }

    /**
     * 返回场地名称和id的对应关系
     *
     * @return map
     */
    public List<String> getAllCourtInfos() throws Exception{
        String token = tokenStore.getTokens("sso.dlut.edu.cn").get(0);
        Headers headers = new Headers.Builder()
                .add(HeaderName.ACCEPT.getName(), "*/*")
                .add(HeaderName.ACCEPT_LANGUAGE.getName(), HeaderName.ACCEPT_LANGUAGE.getValue())
                .add(HeaderName.ACCEPT_ENCODING.getName(), "gzip, deflate")
                .add(HeaderName.AUTHORIZATION.getName(), token)
                .add(HeaderName.CACHE_CONTROL.getName(), HeaderName.CACHE_CONTROL.getValue())
                .add(HeaderName.HOST.getName(), "adm-tycg.dlut.edu.cn")
                .add(HeaderName.ORIGIN.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.PRAGMA.getName(), HeaderName.PRAGMA.getValue())
                .add(HeaderName.REFERER.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.USER_AGENT.getName(), HeaderName.USER_AGENT.getValue())
                .build();
        HttpUrl urlCourtInfo = new HttpUrl.Builder()
                .scheme("http")
                .host("adm-tycg.dlut.edu.cn")
                .addPathSegment("api")
                .addPathSegment("court")
                .addPathSegment("getFieldNoList")
                .addQueryParameter("product_id", productId)
                .build();
        Request request = new Request.Builder()
                .url(urlCourtInfo.url())
                .headers(headers)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject courtInfos = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            log.info("场地信息 " + courtInfos.get("info"));
            JSONArray courtInfo = courtInfos.getJSONObject("data").getJSONArray("result");
            List<String> res = new ArrayList<>();
            for (Object ci : courtInfo) {
                JSONObject temp = (JSONObject) ci;
                res.add(temp.getString("id"));
            }
            return res;
        }

    }

    /**
     *  返回当前票务相关的信息
     * @return 票务相关信息
     */
    public List<String> getAllCourtPrice() throws Exception {
        String token = tokenStore.getTokens("sso.dlut.edu.cn").get(0);
        Headers headers = new Headers.Builder()
                .add(HeaderName.ACCEPT.getName(), "*/*")
                .add(HeaderName.ACCEPT_LANGUAGE.getName(), HeaderName.ACCEPT_LANGUAGE.getValue())
                .add(HeaderName.ACCEPT_ENCODING.getName(), "gzip, deflate")
                .add(HeaderName.AUTHORIZATION.getName(), token)
                .add(HeaderName.CACHE_CONTROL.getName(), HeaderName.CACHE_CONTROL.getValue())
                .add(HeaderName.HOST.getName(), "adm-tycg.dlut.edu.cn")
                .add(HeaderName.ORIGIN.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.PRAGMA.getName(), HeaderName.PRAGMA.getValue())
                .add(HeaderName.REFERER.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.USER_AGENT.getName(), HeaderName.USER_AGENT.getValue())
                .build();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("adm-tycg.dlut.edu.cn")
                .addPathSegment("api")
                .addPathSegment("court")
                .addPathSegment("getCourtPrice")
                .addQueryParameter("product_id", productId)
                .addQueryParameter("venue_id", venueId)
                .addQueryParameter("date", date)
                .build();

        Request request = new Request.Builder()
                .url(url.url())
                .headers(headers)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject ticketInfos = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            log.info("场地票务价格信息 " + ticketInfos.get("info"));
            List<String> res = new ArrayList<>();
            ticketInfos.getJSONObject("data").getJSONArray("result").stream().forEach(x -> {
                JSONObject temp = (JSONObject) x;
                res.add(temp.toJSONString());
            });
            return res;
        }
    }

    /**
     * 创建订单
     * @param courtPrices 票务相关信息
     * @param courtInfos 场地及其名称
     * @return 订票是否成功
     */
    public boolean createOrder(List<String> courtPrices, List<String> courtInfos) throws Exception {
        String token = tokenStore.getTokens("sso.dlut.edu.cn").get(0);
        List<String> priceIds = new ArrayList<>();
        List<String> timeIds = new ArrayList<>();
        List<String> fieldIds = new ArrayList<>();
        List<String> price = new ArrayList<>();
        for (String cp : courtPrices) {
            // 遍历所有的场地信息
            JSONObject temp = JSONObject.parseObject(cp);
            if (start_times.contains(temp.getString("start_time"))) {
                boolean ticketOk = false;
                // 如果当前开始时间在预定时间范围内
                JSONArray fieldList = temp.getJSONArray("fieldlist_s");
                for (String fid : courts) {
                    // 判断所有期望场地中是否还有空闲场地
                    JSONObject field = (JSONObject) fieldList.get(Integer.parseInt(fid));
                    if ("false".equals(field.getString("lock"))) {
                        // 还有空闲场地当前时间段的订购可以满足需求,将该参数进行组装
                        ticketOk = true;
                        priceIds.add(field.getString("price_id"));
                        timeIds.add(field.getString("time_id"));
                        fieldIds.add(field.getString("field_id"));
                        price.add(field.getString("vip_0_price"));
                        break;
                    }
                }
                // 如果没有场地能够满足当前的订购需求则抢票失败
                if (!ticketOk) {
                    log.info("当前票量不能满足当前需求，抢票失败");
                    return false;
                }
            }
        }
        JSONObject selectCourts = new JSONObject();
        selectCourts.put("court_id", productId);
        selectCourts.put("meal_id", mealId);
        selectCourts.put("field_type", fieldType);
        selectCourts.put("price_ids", String.join(",", priceIds));
        selectCourts.put("time_ids", String.join(",", timeIds));
        selectCourts.put("field_ids", String.join(",", fieldIds));
        selectCourts.put("price", String.join(",", price));

        FormBody formBody = new FormBody.Builder()
                .add("goods_id", productId)
                .add("order_type", orderType)
                .add("pay_way", "1".equals(payMethod) ? PayWay.YULANPAY.getCode() : PayWay.WECHATORALIPAY.getCode())
                .add("other_param", selectCourts.toJSONString())
                .add("choose_date", date)
                .build();
        Headers headers = new Headers.Builder()
                .add(HeaderName.ACCEPT.getName(), "*/*")
                .add(HeaderName.ACCEPT_LANGUAGE.getName(), HeaderName.ACCEPT_LANGUAGE.getValue())
                .add(HeaderName.ACCEPT_ENCODING.getName(), "gzip, deflate")
                .add(HeaderName.AUTHORIZATION.getName(), token)
                .add(HeaderName.CACHE_CONTROL.getName(), HeaderName.CACHE_CONTROL.getValue())
                .add(HeaderName.CONTENT_TYPE.getName(), ContentType.APPLICATION_JSON.getValue())
                .add(HeaderName.HOST.getName(), "adm-tycg.dlut.edu.cn")
                .add(HeaderName.ORIGIN.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.PRAGMA.getName(), HeaderName.PRAGMA.getValue())
                .add(HeaderName.REFERER.getName(), "http://h5-tycg.dlut.edu.cn")
                .add(HeaderName.USER_AGENT.getName(), HeaderName.USER_AGENT.getValue())
                .build();
        HttpUrl createOrderUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("adm-tycg.dlut.edu.cn")
                .addPathSegment("api")
                .addPathSegment("pay")
                .addPathSegment("CreateOrder")
                .build();
        Request request = new Request.Builder()
                .url(createOrderUrl.url())
                .post(formBody)
                .headers(headers)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject res = JSONObject.parseObject(response.body().string());
            if ("1".equals(res.getString("code"))) {
                log.info("抢票成功" + res.get("data"));
                return true;
            } else {
                log.info("抢票失败：" + res.get("info"));
                return false;
            }
        }
    }
}
