package com.smallmall.service;

import com.smallmall.dao.AccountUserMapper;
import com.smallmall.model.AccountUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * @Author hzj
 * @ClassName UserService
 * @Description 会员用户service
 * @Date 17:06 2019/3/27
 * @Param 
 * @return 
 **/
@Service
public class AccountUserService {

    //会员用户mapper
    @Autowired
    private AccountUserMapper accountUserMapper;

    //通过微信openid获取会员信息
    public AccountUser getAccountUserByOpenId(String openId){
        AccountUser accountUser = accountUserMapper.getAccountUserByOpenId(openId);
        return accountUser;
    }

    //新增会员用户实体
    public void insertAccountUser(AccountUser accountUser){
        accountUserMapper.insertAccountUser(accountUser);
    }
}
