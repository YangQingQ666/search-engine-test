package cn.mpy.estestdemo.service;

import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 商品搜索管理Service
 * Created by macro on 2018/6/19.
 */
public interface EsProductService {
    /**
     * 从数据库中导入所有商品到ES
     * @return 导入数据数
     */
    int importAll();

    /**
     * 根据id删除商品
     * @param id 商品编号
     */
    void delete(Long id);

    /**
     * 根据id创建商品
     */
    EsProduct create(Long id);

    /**
     * 批量删除商品
     */
    void delete(List<Long> ids);

    /**
     * 根据关键字搜索名称或者副标题
     */
    Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);

    List<EsProduct> searchName(String name);

     List<EsProduct> searchHight(String keyword, Integer pageNum, Integer pageSize);
}