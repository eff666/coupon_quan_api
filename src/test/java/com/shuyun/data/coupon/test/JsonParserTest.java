package com.shuyun.data.coupon.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.shuyun.query.akka.QueryActor;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.meta.MemberRfmConf;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.process.QueryContext;
import com.shuyun.query.process.QueryFactory;
import com.shuyun.query.util.LoadBalanceUtil;
import com.shuyun.query.util.YeahmobiUtils;
import org.mortbay.util.UrlEncoded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParserTest {
    private static String message = "{\"settings\": {\"query_id\": \"query_id_xxxxxxxx\",\"data_source\": \"member\",\"pagination\": {\"offset\": 5,\"limit\": 15},\"return_format\": \"json\"},\"fields\": [\"col1\",\"col2\",\"col3\"],\"filters\": {\"type\": \"or\",\"fields\": [{\"type\": \"num_selector\",\"dimension\": \"col1\",\"num_value\": 5},{\"type\": \"num_in\",\"dimension\": \"col2\",\"value\":[8,9,10]},{\"type\": \"regex\",\"dimension\": \"col3\",\"pattern\": \"^hello\"},{\"type\": \"gt\",\"dimension\": \"col2\",\"value\": 9},{\"type\": \"function\",\"dimension\":\"col3\",\"function\": \"substr\",\"args\": [1,3],\"value\": \"123\"}]},\"sort\": [{\"orderBy\": \"col1\",\"order\": \"asc\"},{\"orderBy\": \"col2\",\"order\": \"desc\"}]}";
    private static String message2 = "{\"settings\": {\"query_id\": \"query_id_xxxxxxxx\",\"data_source\": \"member\",\"pagination\": {\"offset\": 5,\"limit\": 15},\"return_format\": \"json\"}}";

    public void test() throws IOException {

        String queryData = (String) message2;
        JsonParser jsonParser = new ObjectMapper().readValue(queryData, new TypeReference<JsonParser>() {
        });
        String dataSource = jsonParser.getSettings().getData_source();
        //validate(dataSource, jsonParser.getSettings().getQuery_id());
//        validate(dataSource, jsonParser);

        //需要加密的index
        if(EsQueryConf.getInstance().getNeedCryptIndex().contains(dataSource) && EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt() != null) {
            //需要加密的column
            if(jsonParser.getQuerys() != null){
//                getEncryptDataQuerys(dataSource, jsonParser.getQuerys());
            }
            if(jsonParser.getFilters() != null) {
//                getEncryptDataFilters(dataSource, jsonParser.getFilters());
            }
        }

        List<String> shop_ids = new ArrayList<>();
        QueryContext queryContext = QueryFactory.create(jsonParser, shop_ids);
        String requestStr = queryContext.getQuery().toString();

        String url = "";
        if(EsQueryConf.getInstance().getNeedMoveIndex().contains(dataSource)){
            //index_trade和index_order
            url = String.format("http://%s/%s/%s/_search", EsQueryConf.getInstance().getMoveElasticSearchUrl(),
                    EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource);
        } else if(MemberRfmConf.getInstance().getRfmType().equalsIgnoreCase(dataSource)){
            //index_rfm
            if(shop_ids.size() == 1){
                String indexRfm = "index_rfm_9";
                if(MemberRfmConf.getInstance().getRfm_index_1().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_1";
                } else if(MemberRfmConf.getInstance().getRfm_index_2().contains(shop_ids.get(0))) {
                    indexRfm = "index_rfm_2";
                } else if(MemberRfmConf.getInstance().getRfm_index_3().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_3";
                } else if(MemberRfmConf.getInstance().getRfm_index_4().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_4";
                } else if(MemberRfmConf.getInstance().getRfm_index_5().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_5";
                } else if(MemberRfmConf.getInstance().getRfm_index_6().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_6";
                } else if(MemberRfmConf.getInstance().getRfm_index_7().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_7";
                } else if(MemberRfmConf.getInstance().getRfm_index_8().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_8";
                } else if(MemberRfmConf.getInstance().getRfm_index_9().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_9";
                }
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), indexRfm, dataSource);
            } else {
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), MemberRfmConf.getInstance().getRfmIndex(), dataSource);
            }
        } else if(MemberRfmConf.getInstance().getMemberType().equalsIgnoreCase(dataSource)){
            //index_member
            if(shop_ids.size() == 1) {
                String indexMember = "index_member_8";
                if (MemberRfmConf.getInstance().getMember_index_1().contains(shop_ids.get(0))) {
                    indexMember = "index_member_1";
                } else if(MemberRfmConf.getInstance().getMember_index_2().contains(shop_ids.get(0))){
                    indexMember = "index_member_2";
                } else if(MemberRfmConf.getInstance().getMember_index_3().contains(shop_ids.get(0))){
                    indexMember = "index_member_3";
                } else if(MemberRfmConf.getInstance().getMember_index_4().contains(shop_ids.get(0))){
                    indexMember = "index_member_4";
                } else if(MemberRfmConf.getInstance().getMember_index_5().contains(shop_ids.get(0))){
                    indexMember = "index_member_5";
                } else if(MemberRfmConf.getInstance().getMember_index_6().contains(shop_ids.get(0))){
                    indexMember = "index_member_6";
                } else if(MemberRfmConf.getInstance().getMember_index_7().contains(shop_ids.get(0))){
                    indexMember = "index_member_7";
                } else if(MemberRfmConf.getInstance().getMember_index_8().contains(shop_ids.get(0))){
                    indexMember = "index_member_8";
                }
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), indexMember, dataSource);
            } else {
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), MemberRfmConf.getInstance().getMemberIndex(), dataSource);
            }

        }else {
            String routing = Joiner.on(",").join(shop_ids);
            if(routing.indexOf(",") > -1){
                routing = UrlEncoded.encodeString(routing.substring(1,routing.length()-1));
            }
            url = String.format("http://%s/%s/%s/_search%s", LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
                    EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource,
                    shop_ids.size() > 0 ? "?routing=" + routing : "");
        }

//        String url = "";
//        if(EsQueryConf.getInstance().getNeedMoveIndex().contains(dataSource)){
//            url = String.format("http://%s/%s/%s/_search", EsQueryConf.getInstance().getMoveElasticSearchUrl(),
//                     EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource);
//        } else {
//            url = String.format("http://%s/%s/%s/_search%s", LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
//                     EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource,
//                    shop_ids.size() > 0 ? "?routing=" + routing : "");
//        }

        if (shop_ids.size() == 0) {
            System.out.println("query json do not have shop_id");
        }


//        requestEs(sender, receiver, queryContext, url, requestStr);

    }


    public static void main(String[] args) {
        System.out.println("hello");
        JsonParserTest JsonParserTest = new JsonParserTest();
        try {
            JsonParserTest.test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
