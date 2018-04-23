package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail")
    public ServerResponse<ProductDetailVO> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "orderBy",defaultValue = "")String orderBy,
                                         @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNo,pageSize,orderBy);
    }

}
