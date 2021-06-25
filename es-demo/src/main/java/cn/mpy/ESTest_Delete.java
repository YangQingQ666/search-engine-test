package cn.mpy;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

public class ESTest_Delete {
    public static void main(String[] args) throws IOException {
        //创建es客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
        );
        //创建索引
        DeleteIndexRequest user = new DeleteIndexRequest("user");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(user, RequestOptions.DEFAULT);
        //响应状态
        System.out.println(delete.isAcknowledged());
        //关闭连接
        restHighLevelClient.close();
    }
}
