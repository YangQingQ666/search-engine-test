package cn.mpy.estestdemo.nosql.elasticsearch.repository;

import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 商品ES操作类
 * Spring Data 无需写实现类直接操作
 * @author Admin
 */
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Long> {
    /**
     * 搜索查询
     *
     * @param name              商品名称
     * @param subTitle          商品标题
     * @param keywords          商品关键字
     * @param page              分页信息
     * @return
     */
    Page<EsProduct> findByNameOrSubTitleOrKeywords(String name, String subTitle, String keywords, Pageable page);

    /**
     * 根据brandName查询商品
     */
    List<EsProduct> findEsProductsByBrandNameLike(String bradName);

}
