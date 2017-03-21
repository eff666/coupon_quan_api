package com.shuyun.data.coupon.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.shuyun.data.coupon.cache.RedisCache;
import com.shuyun.data.coupon.pojo.JsonParsers;
import com.shuyun.data.coupon.process.QueryFactorys;
import com.shuyun.data.coupon.result.ReportResult;
import com.shuyun.data.coupon.service.IElasticService;
import com.shuyun.data.coupon.service.IGoodsService;
import com.shuyun.data.coupon.util.SmallUrlUtil;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.meta.ReportResultSimple;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.parser.Settings;
import com.shuyun.query.process.QueryContext;
import com.shuyun.query.process.QueryFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service("goodsService")
public class GoodsServiceImpl implements IGoodsService {
    private final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Resource
    private IElasticService elasticService;
    @Autowired
    private RedisCache cache;
    @Value("${searchUrl}")
    private String searchUrl;

    ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

    /**
     * 深度分页最大限制
     */
    private static final int PAGE_DEPTH=500;

    @Override
    public ReportResult queryGoods(String queryData) {
        ReportResult reportResult = new ReportResult();
        JsonParsers jsonParser = null;
        try {
            if(Strings.isNullOrEmpty(queryData)){
                throw new RuntimeException("query must not be blank");
            }
            logger.info("query begin, query is:[ " + queryData + " ]");
            try {
                jsonParser = new ObjectMapper().readValue(queryData, new TypeReference<JsonParsers>() {});
            } catch (IOException e) {
                throw new IOException("Sorry, maybe the param is not match, please check.");
            }

            validateJsonParser(jsonParser);
            String dataSource = jsonParser.getData_source();

            if("item_onsale".equalsIgnoreCase(dataSource)){
                reportResult = QueryFactorys.getGoodsDetailByEs(jsonParser);
            }else if("coupon_receive".equalsIgnoreCase(dataSource)){
                reportResult = QueryFactorys.getCouponDetailsByHttp(jsonParser);
            }
            logger.info("query end, query is:[ " + jsonParser.toString() + " ]");
        } catch (Exception e) {
            logger.error("query failed，query is: [ " + queryData + " ]" + ", cause: [ " + e.getMessage() + " ]");
            reportResult.setFlag("false");
            reportResult.setMsg(e.getMessage());
        }

        return reportResult;
    }


    @Override
    public ReportResultSimple searchGoods(String queryData) {
        ReportResultSimple result = new ReportResultSimple();
        try {
            logger.info("query begin, query is:[ " + queryData + " ]");
            JsonParser jsonParser = new ObjectMapper().readValue(queryData, new TypeReference<JsonParser>() {
            });
            pageDepthLimit(jsonParser);
            String cacheKey = RedisCache.CAHCENAME + "|" + queryData;
            //暂时只对没有search的查询条件做缓存
            if (jsonParser.getQuerys() == null) {
                //先去缓存中取
                result = cache.getCache(cacheKey, ReportResultSimple.class);
            }
            if (result == null || jsonParser.getQuerys() != null) {
                //缓存没有结果,从elasticsearch查询
                String dataSource = jsonParser.getSettings().getData_source();
                validate(dataSource, jsonParser);
                List<String> shop_ids = new ArrayList<>();
                QueryContext queryContext = QueryFactory.create(jsonParser, shop_ids);
                String requestStr = queryContext.getQuery().toString();
                JSONObject json = null;
                if("tuibao_goods".equalsIgnoreCase(dataSource)){
                    json = doPost("http://10.10.145.181:9400/index_tuibao/tuibao_goods/_search", requestStr);
                } else if("goods_coupon".equalsIgnoreCase(dataSource)){
                    json = doPost("http://10.10.138.183:9400/index_goods_coupon/goods_coupon/_search", requestStr);
                }else if("shop_rating".equalsIgnoreCase(dataSource)){
                    json = doPost("http://10.10.138.183:9400/index_shop_rating/shop_rating/_search", requestStr);
                }else if("goods_mysql".equalsIgnoreCase(dataSource)) {
                    json = doPost("http://10.10.138.183:9400/coupon/goods_mysql/_search", requestStr);
                }else if("goods_detail".equalsIgnoreCase(dataSource)) {
                    json = doPost("http://10.10.138.183:9400/index_goods_detail/goods_detail/_search", requestStr);
                } else {
                    throw new RuntimeException("this dataSource do not contain");
                }
                //JSONObject json = doPost(searchUrl, requestStr);
                result = initReportResultForEs(json);
                if (jsonParser.getQuerys() == null) {
                    cache.putCacheWithExpireTime(cacheKey, result, RedisCache.CAHCETIME);
                }
            }
            if (result == null || jsonParser.getQuerys() != null) {
                logger.info("query end, query from es, query is: [ " + jsonParser.toString() + " ]");
            } else {
                logger.info("query end, query from cache, query is: [ " + jsonParser.toString() + " ]");
            }
//        } catch (IOException e) {
//            LOG.error(e.getMessage(), e);
//            LOG.error("query is:[ " + queryData + " ]");
//            result.setFlag("false");
//            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("query is:[ " + queryData + " ]");
            result.setFlag("false");
            result.setMsg(e.getMessage());
        }
        return result;
    }

//    @Override
//    public List<Goods> getGoodsList(int offset, int limit) {
//        String cacheKey = RedisCache.CAHCENAME + "|getGoodsList|" + offset + "|" + limit;
//        //先去缓存中取
//        List<Goods> resultCache = cache.getListCache(cacheKey, Goods.class);
//        if (resultCache == null) {
//            //缓存中没有再去数据库取，并插入缓存（缓存时间为60秒）
//            resultCache = elasticService.getGoodsList(offset, limit);
//            cache.putListCacheWithExpireTime(cacheKey, resultCache, RedisCache.CAHCETIME);
//            logger.info("put cache with key:" + cacheKey);
//        } else {
//            logger.info("get cache with key:" + cacheKey);
//        }
//        return resultCache;
//
//    }

    private ReportResultSimple initReportResultForEs(JSONObject json) {
        ReportResultSimple result = new ReportResultSimple();
        if (json != null) {

            List<Object> list = new ArrayList<>();
            result.setData(list);
            JSONObject hitsJson = json.getJSONObject("hits");
            JSONArray hitsArray = hitsJson.getJSONArray("hits");
            if (hitsArray.size() != 0) {
                for (int i = 0; i < hitsArray.size(); i++) {
                    JSONObject goodsJson = (JSONObject) hitsArray.get(i);
                    JSONObject resource = goodsJson.getJSONObject("_source");

                    //解决smallurl问题
                    String smallUrl = null;
                    try{
                        smallUrl = resource.get("smallurl").toString();
                    }catch (NullPointerException n){
                    }
                    if(smallUrl != null){
                        try {
                            SmallUrlUtil smallUrlUtil = new ObjectMapper().readValue(smallUrl, new TypeReference<SmallUrlUtil>() {});
                            List<String> listSmallUrl = smallUrlUtil.getString();
                            if(listSmallUrl.size() > 0){
                                String urlStr = Joiner.on(",").join(listSmallUrl);
                                resource.put("smallurl", urlStr);
                            }
                        } catch (IOException e) {
                            logger.error("smallurl resolve occur error!");
                        }
                    }

                    list.add(resource);
                }
            }
            result.setFlag("true");
            result.setMsg("ok");
        } else {
            logger.info("elastic return json is null");
            result.setFlag("false");
            result.setMsg("elastic search return null");
        }
        return result;
    }

    public JSONObject doPost(String url, String json) throws IOException {
        HttpClient httpClient = null;
        PostMethod postMethod = null;
        try {
            httpClient = new HttpClient();
            postMethod = new PostMethod(url);
            RequestEntity se = new StringRequestEntity(json, "application/json", "UTF-8");
            postMethod.setRequestEntity(se);
            httpClient.executeMethod(postMethod);
            if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
                return JSONObject.parseObject(postMethod.getResponseBodyAsString());
            } else {
                return null;
            }
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    private void validateJsonParser(JsonParsers jsonParsers){
        String dataSource = jsonParsers.getData_source();
        String detailUrl = jsonParsers.getDetail_url();
        if (Strings.isNullOrEmpty(dataSource)) {
            throw new RuntimeException("data_source must not be blank");
        }

        if (Strings.isNullOrEmpty(detailUrl)) {
            throw new RuntimeException("detail_url must not be blank");
        } else {
            if("item_onsale".equalsIgnoreCase(dataSource)) {
                if (!((detailUrl.contains("//item.taobao.com/") || detailUrl.contains("//detail.tmall.com/")) && detailUrl.contains("id"))) {
                    throw new RuntimeException("item invalid URL");
                }
            } else if("coupon_receive".equalsIgnoreCase(dataSource)){
                if (detailUrl.contains("//shop.m.taobao.com/shop/coupon.htm") || detailUrl.contains("//taoquan.taobao.com/coupon/unify_apply.htm")) {
                    if(detailUrl.contains("//taoquan.taobao.com/coupon/unify_apply.htm")){
                        //https://taoquan.taobao.com/coupon/unify_apply.htm?sellerId=1849722037&activityId=7e7bbf0d554842b1a68b434df14bf724
                        String url = "https://shop.m.taobao.com/shop/coupon.htm?" + detailUrl.substring(detailUrl.indexOf("?") + 1, detailUrl.length());
                        jsonParsers.setDetail_url(url);
                    }
                } else {
                    throw new RuntimeException("coupons invalid URL");
                }
            } else {
                throw new RuntimeException("the data_source do not contain");
            }
        }

        //return jsonParsers;
    }

    private void validate(String dataSource, JsonParser jsonParser) {

        if (Strings.isNullOrEmpty(jsonParser.getSettings().getQuery_id())) {
            throw new RuntimeException("report_id must not be blank");
        }

        if (Strings.isNullOrEmpty(dataSource)) {
            throw new RuntimeException("dataSource must not be blank");
        }

        if (!EsQueryConf.getInstance().getDataSources().keySet().contains(dataSource)) {
            throw new RuntimeException("this dataSource do not contain");
        }

        if (dataSource.equalsIgnoreCase(EsQueryConf.getInstance().getTradeFeature())) {
            List<String> tradeFeatures = jsonParser.getFields();
            if (tradeFeatures.size() > 0) {
                for (String trade : tradeFeatures) {
                    if (!EsQueryConf.getDataSourceInstance(dataSource).getStringDimensions().contains(trade)) {
                        throw new RuntimeException("Sorry, the " + trade + "is not exist.");
                    }
                }
            } else {
                throw new RuntimeException("return_trade_features must not be blank");
            }
        }
    }

    /**
     * 深度分页限制，当分页深度大于500时，返回第500页内容
     * @param jsonParser
     */
    private void pageDepthLimit(JsonParser jsonParser) {
        if(jsonParser == null){
            return ;
        }
        Settings settings = jsonParser.getSettings();
        if(settings == null) {
            return;
        }
        Settings.Pagination pagination = settings.getPagination();
        if(pagination== null){
            return;
        }
        int limit = pagination.getLimit();
        int offset = pagination.getOffset();
        if(limit != 0 ){
            int page = offset/limit;
            if(page>PAGE_DEPTH){
                pagination.setOffset(limit*PAGE_DEPTH);
            }
        }
    }

}
