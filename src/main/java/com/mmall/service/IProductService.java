package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNo, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNo,int pageSize);

    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNo, int pageSize, String orderBy);
}
