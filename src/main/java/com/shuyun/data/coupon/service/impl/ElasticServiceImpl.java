package com.shuyun.data.coupon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.shuyun.data.coupon.pojo.CouponDetails;
import com.shuyun.data.coupon.pojo.Goods;
import com.shuyun.data.coupon.pojo.GoodsJson;
import com.shuyun.data.coupon.pojo.GoodsParser;
import com.shuyun.data.coupon.result.GoodsResult;
import com.shuyun.data.coupon.service.IElasticService;
import com.shuyun.data.coupon.util.JsonUtil;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("elasticService")
public class ElasticServiceImpl implements IElasticService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${cluster.name}")
    private String clusterName;
    @Value("${cluster.hosts}")
    private String hosts;
    @Value("${cluster.port}")
    private int port;
    @Value("${goods.indexName}")
    private String indexName;
    @Value("${goods.typeName}")
    private String typeName;

    private static Client client = null;

    GoodsParser goodsParser = null;
    GoodsResult goodsResult = new GoodsResult();

    @PostConstruct
    public void initESClient() {
        try {
            Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch-cluster").build();
            TransportClient transportClient = TransportClient.builder().settings(settings).build();
            //String host = "10.10.138.183";
            //for (String ip : host.split(";")) {
                transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.10.138.183"), 9500));
            //}
            client = transportClient;
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

//    static  {
//        try {
//            Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch-cluster").build();
//            TransportClient transportClient = TransportClient.builder().settings(settings).build();
//            //String host = "10.10.138.183";
//            //for (String ip : host.split(";")) {
//            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.10.138.183"), 9500));
//            //}
//            client = transportClient;
//        }catch (Exception e){
//            //LOG.error(e.getMessage());
//        }
//    }

    @Override
    public GoodsResult saveGoods(String saveData){
        try{
            if(Strings.isNullOrEmpty(saveData)){
                throw new RuntimeException("data must not be blank");
            }
            LOG.info("insert data begin, data is:[ " + saveData + " ]");

            int total = 0;
            try {
                goodsParser = new ObjectMapper().readValue(saveData, new TypeReference<GoodsParser>() {});
            } catch (IOException e) {
                throw new IOException("Sorry, maybe the param is not match, please check.");
            }

            List<Goods> goodsList = goodsParser.getGoods_list();
            if(goodsList != null && goodsList.size() > 0){
                //String key = goodsParser.getCoupon().getShop_id() + "_" + goodsParser.getCoupon().getCoupon_name();
                for(Goods goods : goodsList){
                    if(Strings.isNullOrEmpty(goods.getGoods_id())){
                        throw new RuntimeException("goods_id must not be blank!");
                    }
                    //goods.setKey(key);
                    total++;
                    if(!upsert(goods)){
//                        goodsResult.setCode("2");
//                        goodsResult.setMessage("save failed!");
//                        return goodsResult;
                        throw new RuntimeException("save goods failed! error occur goods_id : [ " + goods.getGoods_id() + " ]");

                    }
                }
                goodsResult.setFlag("true");
                goodsResult.setMsg("save goods success!");
                LOG.info("insert data end, total insert data: " + total);
            } else {
                throw new RuntimeException("data must not be blank!");
            }
        } catch (Exception e){
            LOG.error("save failed，data is: [ " + saveData + " ]" + ", cause: [ " + e.getMessage() + " ]");
            goodsResult.setFlag("false");
            goodsResult.setMsg(e.getMessage());
        } finally {
            //client.close();
        }
        return goodsResult;
    }

    @Override
    public boolean upsert(Goods goods) {
        String indexName = "index_tuibao";
        String typeName = "tuibao_goods";
        if (goodsParser == null) {
            LOG.error("goods is null");
            return false;
        }
        String json = null;
        try {
            String id = goods.getGoods_id();
            LOG.info("start to deal id:{}", id);

            GetResponse getResponse = client.prepareGet(indexName, typeName, id).execute().actionGet();
            if (getResponse == null) {
                LOG.error("response is null  with id :{}", id);
                return false;
            } else if (getResponse.isExists()) {
                // update logic
                LOG.error("id: {} already exist in elastic", id);
                throw new RuntimeException("save goods failed! already exist in elastic, goods_id : [ " + goods.getGoods_id() + " ]");
            } else {
                try {
                    // insert logic
                    GoodsJson goodsJson = getGoodsJson(goods);
                    json = JsonUtil.OBJECT_MAPPER.writeValueAsString(goodsJson);

                    IndexResponse response = client.prepareIndex(indexName, typeName)
                            .setId(id)
                            // if there is an existing document with the id, then it will throw exception
                            .setOpType(IndexRequest.OpType.CREATE)
                            .setSource(json.getBytes("utf-8"))
                            .execute().actionGet();

                    if (response != null && response.isCreated()) {
                        LOG.info("insert success with id: {}", id);
                        return true;
                    } else {
                        LOG.error("index failed for response is null or created failed for id:{}", id);
                        return false;
                    }
                } catch (DocumentAlreadyExistsException e) {
                    LOG.error(e.getMessage(), e);
                    LOG.info("Document Already Exists and will try again, id : " + id);
                    // recursion
                    return upsert(goods);
                }
            }
        } catch (Exception e) {
            LOG.error("error occur, cause: " + e.getMessage());
            //LOG.error(json);
        }
        return false;
    }

    public GoodsJson getGoodsJson(Goods goods){
        GoodsJson goodsJson = new GoodsJson();
        CouponDetails coupon = goodsParser.getCoupon();

        goodsJson.setKey(coupon.getShop_id() + "_" + coupon.getCoupon_name());

        goodsJson.setGoods_id(goods.getGoods_id());
        goodsJson.setGoods_seller_id(goods.getGoods_seller_id());
        goodsJson.setGoods_activity_id(goods.getGoods_activity_id());
        goodsJson.setGoods_name(goods.getGoods_name());
        goodsJson.setGoods_price(goods.getGoods_price());
        goodsJson.setGoods_image_url(goods.getGoods_image_url());
        goodsJson.setGoods_detail_url(goods.getGoods_detail_url());
        goodsJson.setGoods_state(goods.getGoods_state());

        goodsJson.setStart_date(coupon.getStart_date());
        goodsJson.setEnd_date(coupon.getEnd_date());
        goodsJson.setCoupon_name(coupon.getCoupon_name());
        goodsJson.setCoupons(Long.parseLong(coupon.getCoupons()));
        goodsJson.setAlready_receive(Long.parseLong(coupon.getAlready_receive()));
        goodsJson.setCoupons_desc(coupon.getCoupons_desc());
        goodsJson.setShop_name(coupon.getShop_name());
        goodsJson.setShop_id(coupon.getShop_id());
        goodsJson.setImage_url(coupon.getImage_url());

        goodsJson.setGoods_cps(goodsParser.getGoods_cps());

        goodsJson.setPromotion_start_date(goodsParser.getPromotion_date().getPromotion_start_date());
        goodsJson.setPromotion_end_date(goodsParser.getPromotion_date().getPromotion_end_date());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date());
        Date date = null;
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime() / 1000;
        goodsJson.setInsert_date(time);
        goodsJson.setModify_date(time);

        return goodsJson;
    }

    @Override
    public List<Goods> queryGoods(String title, int offset, int limit) {
        if (title == null) {
            LOG.error("query param title is null");
            return null;
        }
        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("title", title))
                .setFrom(offset).setSize(limit).setExplain(true)
                .execute()
                .actionGet();


        return initGoods(searchResponse);
    }

    @Override
    public List<Goods> getGoodsList(int offset, int limit) {

        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.ma)
                .addSort("ctime", SortOrder.DESC)
                .setFrom(offset).setSize(limit).setExplain(true)
                .execute()
                .actionGet();

        return initGoods(searchResponse);
    }

    private List<Goods> initGoods(SearchResponse searchResponse)  {
        List<Goods> goodsList = new ArrayList<>();
        if (searchResponse == null) {
            return goodsList;
        }
        for (SearchHit hit : searchResponse.getHits()) {
            try {
                byte[] source = hit.source();
                String s = new String(source, "UTF-8");
                Goods goods = JsonUtil.OBJECT_MAPPER.readValue(source, Goods.class);
                goodsList.add(goods);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return goodsList;

    }
}
