package cn.mpy.estestdemo.dao;

import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 搜索系统中的商品管理自定义Dao
 * @author Admin
 */
@Mapper
public interface EsProductDao {
    List<EsProduct> getAllEsProductList(@Param("id") Long id);
}
