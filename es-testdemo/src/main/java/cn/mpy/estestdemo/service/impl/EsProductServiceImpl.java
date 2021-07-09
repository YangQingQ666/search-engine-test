package cn.mpy.estestdemo.service.impl;

import cn.mpy.estestdemo.dao.EsProductDao;
import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProduct;
import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProductAttributeValue;
import cn.mpy.estestdemo.nosql.elasticsearch.repository.EsProductRepository;
import cn.mpy.estestdemo.service.EsProductService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 商品搜索管理Service实现类
 * @author Admin
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsProductDao productDao;
    @Autowired
    private EsProductRepository productRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public int importAll() {
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
        Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);
        Iterator<EsProduct> iterator = esProductIterable.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            result = productRepository.save(esProduct);
        }
        return result;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<EsProduct> esProductList = new ArrayList<>();
            for (Long id : ids) {
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                esProductList.add(esProduct);
            }
            productRepository.deleteAll(esProductList);
        }
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        //按照id降序分页查询
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.Direction.DESC, "id");
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    @Override
    public List<EsProduct> searchName(String name) {
        return this.productRepository.findEsProductsByBrandNameLike(name);
    }

    @Override
    public List<EsProduct> searchHight(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.Direction.DESC, "id");
        //根据一个值查询多个字段  并高亮显示  这里的查询是取并集，即多个字段只需要有一个字段满足即可
        //需要查询的字段
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                //精确查找
                //.must(QueryBuilders.commonTermsQuery("brandName",keyword))
                //模糊查询加强 Fuzziness.TWO ONE ZERO
                //.should(QueryBuilders.fuzzyQuery("name",keyword).fuzziness(Fuzziness.TWO))
                //分词查找
                .should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.matchQuery("subTitle", keyword))
                .should(QueryBuilders.matchQuery("keywords", keyword));
        //构建高亮查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("name"),
                        new HighlightBuilder.Field("subTitle"),
                        new HighlightBuilder.Field("keywords"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .build();
        //查询
        SearchHits<EsProduct> search = elasticsearchRestTemplate.search(searchQuery, EsProduct.class);
        //得到查询返回的内容
        List<SearchHit<EsProduct>> searchHits = search.getSearchHits();
        //设置一个最后需要返回的实体类集合
        List<EsProduct> esProducts = new ArrayList<>();
        //遍历返回的内容进行处理
        for (SearchHit<EsProduct> searchHit : searchHits) {
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setName(highlightFields.get("name") == null ? searchHit.getContent().getName() : highlightFields.get("name").get(0));
            searchHit.getContent().setSubTitle(highlightFields.get("subTitle") == null ? searchHit.getContent().getSubTitle() : highlightFields.get("subTitle").get(0));
            searchHit.getContent().setKeywords(highlightFields.get("keywords") == null ? searchHit.getContent().getKeywords() : highlightFields.get("keywords").get(0));
            //放到实体类中
            esProducts.add(searchHit.getContent());
        }
        esProducts.forEach(message -> {
            System.out.println(JSONObject.toJSON(message));
        });
        return esProducts;
    }

}
