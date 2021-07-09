package cn.mpy.estestdemo.controller;

import cn.mpy.estestdemo.api.CommonPage;
import cn.mpy.estestdemo.api.CommonResult;
import cn.mpy.estestdemo.nosql.elasticsearch.document.EsProduct;
import cn.mpy.estestdemo.service.EsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索商品管理Controller
 * @author Admin
 */
@RestController
@RequestMapping("/esProduct")
public class EsProductController {

    @Autowired
    private EsProductService esProductService;

    @PostMapping("/importAll")
    public CommonResult<Integer> importAllList() {
        int count = esProductService.importAll();
        return CommonResult.success(count);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Object> delete(@PathVariable Long id) {
        System.out.println(id);
        esProductService.delete(id);
        return CommonResult.success(null);
    }

    @DeleteMapping("/delete/batch")
    public CommonResult<Object> delete(@RequestParam("ids") List<Long> ids) {
        System.out.println(ids.size());
        esProductService.delete(ids);
        return CommonResult.success(null);
    }

    @PutMapping("/create/{id}")
    public CommonResult<EsProduct> create(@PathVariable Long id) {
        System.out.println(id);
        EsProduct esProduct = esProductService.create(id);
        if (esProduct != null) {
            return CommonResult.success(esProduct);
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/search/simple")
    public CommonResult<CommonPage<EsProduct>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.search(keyword, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

    @GetMapping("/search/simplehight")
    public CommonResult searchHight(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        List<EsProduct> esProductPage = esProductService.searchHight(keyword,pageNum,pageSize);
        return CommonResult.success(esProductPage);
    }
    @GetMapping("/search/name")
    public CommonResult searchName(@RequestParam(required = false) String name) {
        List<EsProduct> esProductPage = esProductService.searchName(name);
        return CommonResult.success(esProductPage);
    }

}
