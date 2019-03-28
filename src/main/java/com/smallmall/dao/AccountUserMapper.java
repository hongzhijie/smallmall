package com.smallmall.dao;


import com.smallmall.model.AccountUser;
import org.apache.ibatis.annotations.Mapper;

/*
 * @Author hzj
 * @ClassName AccountUserMapper
 * @Description 会员用户mapper
 * @Date 15:05 2019/3/27
 **/
@Mapper
public interface AccountUserMapper {

    //通过微信openid获取会员信息
    public AccountUser getAccountUserByOpenId(String openId);

    //新增会员用户实体
    public void insertAccountUser(AccountUser accountUser);
}
