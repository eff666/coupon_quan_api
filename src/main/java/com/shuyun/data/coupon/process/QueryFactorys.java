package com.shuyun.data.coupon.process;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.shuyun.data.coupon.pojo.CouponDetails;
import com.shuyun.data.coupon.pojo.JsonParsers;
import com.shuyun.data.coupon.result.ReportResult;
import com.shuyun.data.coupon.util.ConsituteDataSetQueryUtil;
import com.shuyun.data.coupon.util.HttpRequestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class QueryFactorys {

    private final Logger logger = LoggerFactory.getLogger(QueryFactorys.class);

    public static ReportResult getGoodsDetailByEs(JsonParsers jsonParser){
        ReportResult reportResult = new ReportResult();
        String flag = "false";
        String message = "The goods have been off the shelf!";
        try {
            String detailUrl = jsonParser.getDetail_url();
            detailUrl = detailUrl.substring(detailUrl.indexOf("id") + 3, detailUrl.length());
            ConsituteDataSetQueryUtil consituteDataSetQueryUtil = new ConsituteDataSetQueryUtil();
            JsonNode result = null;
            try {
                result = consituteDataSetQueryUtil.search(detailUrl);
            }catch (Exception e){
                reportResult.setFlag("4");
                reportResult.setMsg(e.getClass().getName() + " " + e.getMessage());
                return reportResult;
            }
            List<Object> data = new ArrayList<>();
            if (result != null) {
                JsonNode approveStatus = null;
                try {
                    approveStatus = result.get("data").get("data").findValue("approve_status");
                } catch (NullPointerException n) {
                }

                if (approveStatus != null) {
                    if ("onsale".equalsIgnoreCase(approveStatus.asText())) {
                        flag = "true";
                        message = "ok";
                        data.add(result.get("data").get("data").get(0));
                    }
                }
            }
            reportResult.setFlag(flag);
            reportResult.setMsg(message);
            reportResult.setData(data);
        }catch (Exception e){
            reportResult.setFlag(flag);
            reportResult.setMsg(message);
        }
        return reportResult;
    }

    public static ReportResult getCouponDetailsByHttp(JsonParsers jsonParser){
        ReportResult reportResult = new ReportResult();
        String flag = "false";
        String message = "coupon invalid URL!";
        List<Object> data = new ArrayList<>();
        try {
            String detailUrl = jsonParser.getDetail_url();
            String html = HttpRequestUtil.getStatusBySendGet(detailUrl);
            if (!Strings.isNullOrEmpty(html)) {
                if (html.contains("该优惠券不存在或者已经过期") || html.contains("您浏览店铺不存在或已经关闭")) {
                    flag = "false";
                    message = "the coupon not exist or expired!";
                } else {
                    Document searchDoc = Jsoup.parse(html);
                    //org.jsoup.select.Elements elements = searchDoc.select("table.gridtable tbody tr td font");
                    String shopId = searchDoc.select(".coupon-shop-title a").toString();
                    if (!Strings.isNullOrEmpty(shopId)) {
                        shopId = shopId.substring(shopId.indexOf("shop_id") + 8, shopId.indexOf("\">"));
                    }

                    org.jsoup.select.Elements couponDetails = searchDoc.select("#tbh5v0 .coupon dl dd");
                    String date = couponDetails.get(2).text();
                    //10元优惠券 剩13700 张（已领用46300 张） 单笔满37元可用，每人限领3 张 有效期:2016-08-13至2017-01-31

                    CouponDetails coupon = new CouponDetails();
                    coupon.setAlready_receive(couponDetails.select("span").get(1).text());
                    coupon.setCoupon_name(searchDoc.select("#tbh5v0 .coupon dl dt").text());
                    coupon.setCoupons(couponDetails.select("span").get(0).text());
                    coupon.setCoupons_desc(couponDetails.get(1).text().replaceAll(" ", ""));
                    coupon.setStart_date(date.substring(date.indexOf("有效期") + 4, date.indexOf("至")));
                    coupon.setEnd_date(date.substring(date.indexOf("至") + 1, date.length()));
                    //coupon.setImage_url();
                    coupon.setShop_id(shopId);
                    coupon.setShop_name(searchDoc.select(".coupon-shop-title a").text());

                    data.add(coupon);
                    flag = "true";
                    message = "ok";
                }
            }
            reportResult.setFlag(flag);
            reportResult.setMsg(message);
            reportResult.setData(data);
        } catch (Exception e){
            reportResult.setFlag(flag);
            reportResult.setMsg(message);
        }
        return reportResult;
    }

}
