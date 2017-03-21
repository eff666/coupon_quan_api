package com.shuyun.data.coupon.result;

import java.util.ArrayList;
import java.util.List;

public class GoodsResult {
    private String flag;
    private String msg;
    //private List<Object> data = new ArrayList<Object>();

    public GoodsResult(){}

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
