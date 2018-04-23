package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
                int resultCount;
                if (product.getId() != null) {
                    resultCount = productMapper.updateByPrimaryKey(product);
                    if (resultCount > 0) {
                        return ServerResponse.createBySuccessMessage("更新商品成功");
                    }
                    return ServerResponse.createByErrorMessage("更新商品失败");
                } else {
                    resultCount = productMapper.insert(product);
                    if (resultCount > 0) {
                        return ServerResponse.createBySuccessMessage("新增商品成功");
                    }
                    return ServerResponse.createByErrorMessage("新增商品失败");
                }
        }
        return ServerResponse.createByErrorMessage("新增或更新商品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int resultCount = productMapper.updateByPrimaryKeySelective(product);

        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("更新商品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("更新商品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVO productDetailVO = assembalProductDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    private ProductDetailVO assembalProductDetailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setName(product.getName());

        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVO.setParentCategoryId(0);
        } else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }

        productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        return productDetailVO;

    }


    @Override
    public ServerResponse<PageInfo> getProductList(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVO> productListVOList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServerResponse.createBySuccess(pageResult);
    }


    private ProductListVO assembleProductListVO(Product product) {
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setName(product.getName());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        return productListVO;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectProductByNameAndProductId(productName, productId);
        List<ProductListVO> productListVOList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        if (product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVO productDetailVO = assembalProductDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNo, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNo, pageSize);
                List<ProductListVO> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.getDeepCategory(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNo, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);

        List<ProductListVO> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVO productListVo = assembleProductListVO(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
