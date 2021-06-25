import cn.mpy.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public class Test {
    public RestHighLevelClient rest;

    @Before
    public void before() {
        rest = new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "HTTP"))
        );
    }

    @org.junit.Test
    public void test() throws IOException {
        //创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("student");
        CreateIndexResponse createIndexResponse = rest.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    @org.junit.Test
    public void test1() throws IOException {
        //查询student索引
        GetIndexRequest getIndexRequest = new GetIndexRequest("student");
        GetIndexResponse getIndexResponse = rest.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(getIndexResponse.getMappings());
        System.out.println(getIndexResponse.getAliases());
        System.out.println(getIndexResponse.getSettings());
    }

    @org.junit.Test
    public void test2() throws IOException {
        //向索引添加数据
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("student").id("1001");
        User user = new User();
        user.setAge(19);
        user.setName("王五");
        user.setSex("女");
        ObjectMapper mapepr = new ObjectMapper();
        indexRequest.source(mapepr.writeValueAsString(user), XContentType.JSON);
        IndexResponse index = rest.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index.getResult());
        System.out.println(index.getIndex());
    }

    @org.junit.Test
    public void test22() throws IOException {
        //向索引批量添加数据
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest().index("student").id("1001").source(XContentType.JSON, "name", "mpy1", "sex", "女", "age", 19));
        bulkRequest.add(new IndexRequest().index("student").id("1002").source(XContentType.JSON, "name", "mpy2", "sex", "女", "age", 13));
        bulkRequest.add(new IndexRequest().index("student").id("1003").source(XContentType.JSON, "name", "mpy3", "sex", "女", "age", 10));
        bulkRequest.add(new IndexRequest().index("student").id("1004").source(XContentType.JSON, "name", "mpy4", "sex", "男", "age", 19));
        bulkRequest.add(new IndexRequest().index("student").id("1005").source(XContentType.JSON, "name", "mpy5", "sex", "女", "age", 32));
        bulkRequest.add(new IndexRequest().index("student").id("1006").source(XContentType.JSON, "name", "mpy6", "sex", "男", "age", 45));
        bulkRequest.add(new IndexRequest().index("student").id("1007").source(XContentType.JSON, "name", "mpy7", "sex", "女", "age", 43));
        bulkRequest.add(new IndexRequest().index("student").id("1008").source(XContentType.JSON, "name", "mpy8", "sex", "男", "age", 23));
        bulkRequest.add(new IndexRequest().index("student").id("1009").source(XContentType.JSON, "name", "mpy9", "sex", "女", "age", 32));
        bulkRequest.add(new IndexRequest().index("student").id("1010").source(XContentType.JSON, "name", "mpy0", "sex", "男", "age", 23));
        BulkResponse bulk = rest.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.getTook());
        System.out.println(bulk.getItems());
    }

    @org.junit.Test
    public void testsearch() throws IOException {
        //查询student里的数据
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        searchRequest.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @org.junit.Test
    public void test3() throws IOException {
        //更新student里的数据
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("student").id("1001");
        updateRequest.doc(XContentType.JSON, "sex", "男", "name", "赵柳");
        UpdateResponse update = rest.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.getIndex());
        System.out.println(update.getResult());
    }

    @org.junit.Test
    public void test4() throws IOException {
        //删除student里的数据
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("student").id("1001");
        DeleteResponse delete = rest.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.toString());
    }

    @org.junit.Test
    public void test44() throws IOException {
        //批量删除student里的数据
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new DeleteRequest().index("student").id("1001"));
        bulkRequest.add(new DeleteRequest().index("student").id("1002"));
        bulkRequest.add(new DeleteRequest().index("student").id("1003"));
        bulkRequest.add(new DeleteRequest().index("student").id("1004"));
        BulkResponse bulk = rest.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.getItems());
        System.out.println(bulk.getTook());
    }

    @org.junit.Test
    public void testSearchall() throws IOException {
        //条件查询
//        SearchRequest searchRequest=new SearchRequest();
//        searchRequest.indices("student");
//        searchRequest.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("age",19)));
//        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = search.getHits();
//        System.out.println(hits.getTotalHits());
//        hits.forEach(x->{
//            System.out.println(x.getSourceAsString());
//        });
        //分页查询 排序 查询字段
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        query.from(0);//页数
        query.size(10);//每页的数量
        query.sort("age", SortOrder.DESC);//排序
        String[] excludes = {"age"};//排除字段
        String[] includes = {};//包含字段
        query.fetchSource(includes, excludes);
        searchRequest.source(query);
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @org.junit.Test
    public void zuhesearch() throws IOException {
        //组合查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        SearchSourceBuilder query = new SearchSourceBuilder();
        //必须 或者
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.should(QueryBuilders.matchQuery("age",19));
////        boolQueryBuilder.mustNot(QueryBuilders.matchQuery("sex","男"));
//        boolQueryBuilder.should(QueryBuilders.matchQuery("age",32));
        //区间查询
        RangeQueryBuilder age = QueryBuilders.rangeQuery("age");
        age.gte(30);//大于
        age.lt(45);//小于
        query.query(age);
        searchRequest.source(query);
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @org.junit.Test
    public void zuhelikesearch() throws IOException {
        //高亮查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        SearchSourceBuilder query = new SearchSourceBuilder();
        TermsQueryBuilder termQueryBuilder = QueryBuilders.termsQuery("name", "mpy1");
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");
        query.highlighter(highlightBuilder);
        query.query(termQueryBuilder);
        searchRequest.source(query);
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @org.junit.Test
    public void zuhejuhesearch() throws IOException {
        //聚合查询 最大值
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        SearchSourceBuilder query = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.max("maxAge").field("age");
        query.aggregation(aggregationBuilder);
        searchRequest.source(query);
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @org.junit.Test
    public void zuhegroupsearch() throws IOException {
        //聚合查询 分组查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("student");
        SearchSourceBuilder query = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("ageGroup").field("age");
        query.aggregation(aggregationBuilder);
        searchRequest.source(query);
        SearchResponse search = rest.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(x -> {
            System.out.println(x.getSourceAsString());
        });
    }

    @After
    public void after() {
        try {
            rest.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
