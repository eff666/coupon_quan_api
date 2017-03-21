package com.shuyun.data.coupon.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyun.query.meta.ReportResultSimple;
import com.shuyun.query.parser.JsonParser;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 简单调用数据中心接口实现。
 * 需要调用者直接传入要调用的服务地址，及过滤条件。
 */
public class ConsituteDataSetQueryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger("ConsituteDataSetQuerySimple");
    private CloseableHttpClient httpClient;
    private final String url = "http://api.shuyun.com/shuyun-searchapi/1.0/search";
    private String query = "{\"settings\":{\"data_source\":\"item_onsale\",\"pagination\":{\"offset\":0,\"limit\":1},\"return_format\":\"json\",\"query_id\":\"query_id_item_onsale\"},\"fields\":[\"num_iid\",\"title\",\"price\",\"detail_url\", \"pic_url\", \"approve_status\"],\"filters\":{\"type\":\"and\",\"fields\":[{\"type\":\"and\",\"fields\":[{\"type\":\"str_selector\",\"dimension\":\"num_iid\",\"value\":\"%s\"}]}]}}";

    private final String callerService = "shuyun-searchapi";
    private final String serviceSecret = "73687QcHhjxskUzwgCXmL";
    private final String contextPath = "shuyun-searchapi";
    private final String version = "1.0";
    private final String requestPath = "/search";


    public ConsituteDataSetQueryUtil() {
        httpClient = HttpClients.createDefault();

//        String parts[] = url.split("/");
//        String request[] = url.split(parts[4]);
//        contextPath = parts[3];
//        version = parts[4];
//        requestPath = request[1];
    }

    public JsonNode search(String numId) {
        HttpPost httpPost = new HttpPost(url);
        String body = null;
        JsonNode result = null;
        try {
            //set headers
            setHeaders(httpPost);
            // 创建参数队列
            query = String.format(query, numId);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            formParams.add(new BasicNameValuePair("param",query));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, org.apache.commons.lang3.CharEncoding.UTF_8);

            httpPost.setEntity(uefEntity);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(4000).setConnectionRequestTimeout(3000).setSocketTimeout(30000).build();//设置请求和传输超时时间

            httpPost.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            body = EntityUtils.toString(entity, CharEncoding.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            //JSON ----> JsonNode
            result = mapper.readTree(body);
            String flag = result.get("flag").asText();
            if (!"success".equals(flag)) {
                //LOGGER.error("查询数据集接口响应flag非success,query:{},result:{}", query, body);
                LOGGER.error("查询数据集接口响应flag非success！query:{}, url:{}, body:{}", query, url, body);
                throw new RuntimeException("查询数据中心接口失败！");
            }
        }catch (Exception e){
            LOGGER.error("查询数据中心接口失败！query:{}, url:{}, body:{}, error:{}", query, url, body, e.getMessage());
            //throw new DataSetQueryException("发生未知异常！", e);
        }finally {
            httpPost.releaseConnection();
        }
        return result;
    }

    /**
     * 初始化header参数
     */
    private void setHeaders(HttpPost httpPost) throws Exception {
        try {
            String timestamp = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            String sign = ShuyunSignUtil.generateSign(callerService, contextPath, version, timestamp, serviceSecret, requestPath);

            httpPost.addHeader("X-Caller-Service", callerService);
            httpPost.addHeader("X-Caller-Timestamp", timestamp);
            httpPost.addHeader("X-Caller-Sign", sign);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

//    public static void main(String[] args) {
//        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
////        for(int i=0;i<1000;i++){
////            cachedThreadPool.submit(new Runnable() {
////                @Override
////                public void run() {
////                    ConsituteDataSetQuerySimple consituteDataSetQuerySimple =
////                            new ConsituteDataSetQuerySimple("http://api.stage.shuyun.com/shuyun-searchapi/1.0/search");
////                    JsonNode result=  consituteDataSetQuerySimple.search("");
////
////                    System.err.println(result.toString());
////                }
////            });
////
////        }
//
//        ConsituteDataSetQueryUtil consituteDataSetQuery = new ConsituteDataSetQueryUtil();
//        JsonNode result=  consituteDataSetQuery.search("");
//
//        System.err.println(result.toString());
//
//    }
}
