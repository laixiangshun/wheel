package com.lxs.bigdata.mybatis.controller;


import com.lxs.bigdata.common.controller.BaseController;
import com.lxs.bigdata.mybatis.entity.User;
import com.lxs.bigdata.mybatis.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jobob
 * @since 2019-07-31
 */
@RestController
@RequestMapping("/user/")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @ResponseBody
    @RequestMapping(value = "find", method = RequestMethod.POST)
    public ResponseEntity<List<User>> findUser() {
        List<User> list = userService.list();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
