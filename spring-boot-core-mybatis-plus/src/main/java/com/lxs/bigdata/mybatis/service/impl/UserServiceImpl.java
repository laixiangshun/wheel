package com.lxs.bigdata.mybatis.service.impl;

import com.lxs.bigdata.mybatis.mapper.UserMapper;
import com.lxs.bigdata.mybatis.entity.User;
import com.lxs.bigdata.mybatis.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
