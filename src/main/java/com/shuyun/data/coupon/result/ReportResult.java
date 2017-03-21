package com.shuyun.data.coupon.result;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ReportResult {

    private static Logger logger = Logger.getLogger(ReportResult.class);
    private String flag;
    private String msg;
    private List<Object> data = new ArrayList<Object>();

    public ReportResult() {
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
