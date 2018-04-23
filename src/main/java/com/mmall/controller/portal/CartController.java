package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list")
    public ServerResponse list(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.getList(user.getId());
    }

    @RequestMapping("add")
    public ServerResponse<CartVO> add(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update")
    public ServerResponse<CartVO> update(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete_product")
    public ServerResponse<CartVO> update(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deletePrioduct(user.getId(), productIds);
    }


    @RequestMapping("select_all")
    public ServerResponse<CartVO> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),Constant.Cart.CHECKED,null);
    }

    @RequestMapping("un_select_all")
    public ServerResponse<CartVO> unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),Constant.Cart.UN_CHECKED,null);
    }

    @RequestMapping("select")
    public ServerResponse<CartVO> select(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),Constant.Cart.CHECKED,productId);
    }

    @RequestMapping("un_select")
    public ServerResponse<CartVO> unSelect(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),Constant.Cart.UN_CHECKED,productId);
    }

    @RequestMapping("get_cart_product_count")
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }



}
