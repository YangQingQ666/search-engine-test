package cn.mpy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;


import java.io.IOException;

public class ESTest_Doc_Insert {
    public static void main(String[] args) throws IOException {
        //创建es客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
        );
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("user").id("1001");
        User user = new User();
        user.setAge(18);
        user.setName("张三");
        user.setSex("男");
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(user);
        indexRequest.source(s, XContentType.JSON);
        //插入数据
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index.getResult());
        System.out.println(index.getIndex());
        //关闭连接
        restHighLevelClient.close();
    }
}
