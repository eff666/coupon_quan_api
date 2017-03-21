package com.shuyun.data.coupon.pojo;

public class JsonParsers {
    private String data_source;
    private String detail_url;

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    @Override
    public String toString() {
        return "JsonParser{" +
                "\"data_source\":\"" + data_source +
                "\", \"detail_url\":\"" + detail_url + "\"" +
                '}';
    }
}
