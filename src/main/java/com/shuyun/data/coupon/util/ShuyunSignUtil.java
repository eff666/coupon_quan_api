package com.shuyun.data.coupon.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ShuyunSignUtil {

    public static String generateSign(String callerService, String contextPath, String version, String timestamp, String serviceSecret, String requestPath) {
        String sign = "";
        if (callerService == null  || contextPath == null
                || timestamp == null || serviceSecret == null) {
            return sign;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("callerService", callerService);
        map.put("contextPath", contextPath);
        try {
        if (requestPath != null) {
            StringBuilder sb = new StringBuilder();
            for(String part : requestPath.split("/")) {
                sb.append("/").append(URLEncoder.encode(part,"utf-8"));
            }
            map.put("requestPath", sb.toString().substring(1));
        }
        map.put("timestamp", timestamp);
        map.put("v", version);

            sign = generateMD5Sign(serviceSecret, map);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        return sign;
    }

    private static String generateMD5Sign(String secret, Map<String, String> parameters) throws NoSuchAlgorithmException,       UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(generateConcatSign(secret, parameters).getBytes("utf-8"));
        return byteToHex(bytes);
    }

    private static String generateConcatSign(String secret, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder().append(secret);
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            sb.append(key).append(parameters.get(key));
        }
        return sb.append(secret).toString();
    }

    private static String byteToHex(byte[] bytesIn) {
        StringBuilder sb = new StringBuilder();
        for (byte byteIn : bytesIn) {
            String bt = Integer.toHexString(byteIn & 0xff);
            if (bt.length() == 1)
                sb.append(0).append(bt);
            else
                sb.append(bt);
        }
        return sb.toString().toUpperCase();
    }

    public static void main(String[] args){
        //注意，日期必须使用如下格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time = sdf.format(cal.getTime()).toString();
        String sign = generateSign("shuyun-searchapi", "shuyun-searchapi", "1.0" ,time, "73687QcHhjxskUzwgCXmL", "/search");


        String url = "{\"string\":[\"http:\\/\\/img1.tbcdn.cn\\/tfscom\\/i1\\/1911441148\\/TB2rNZ0XB8lpuFjSspaXXXJKpXa_!!1911441148.jpg\",\"http:\\/\\/img4.tbcdn.cn\\/tfscom\\/i1\\/1911441148\\/TB27ckFXuJ8puFjy1XbXXagqVXa_!!1911441148.jpg\",\"http:\\/\\/img4.tbcdn.cn\\/tfscom\\/i4\\/1911441148\\/TB2FXY6XrtlpuFjSspfXXXLUpXa_!!1911441148.jpg\",\"http:\\/\\/img1.tbcdn.cn\\/tfscom\\/i1\\/1911441148\\/TB2Bg_6XrFlpuFjy0FgXXbRBVXa_!!1911441148.jpg\"]}";
    }
}
