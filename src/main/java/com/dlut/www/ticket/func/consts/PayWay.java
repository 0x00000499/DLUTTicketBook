package com.dlut.www.ticket.func.consts;

public enum PayWay {
    YULANPAY("1", "玉兰卡支付"),
    WECHATORALIPAY("3", "微信/支付宝");
    private final String code;
    private final String way;
    PayWay(String code, String way){
        this.code = code;
        this.way = way;
    }
    public String getCode(){
        return this.code;
    }
    public String getWay(){
        return this.way;
    }
}
