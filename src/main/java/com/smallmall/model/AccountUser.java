package com.smallmall.model;

import java.io.Serializable;

/*
 * @Author hzj
 * @ClassName AccountUser
 * @Description 会员用户表
 * @Date 15:00 2019/3/27
 **/
public class AccountUser implements Serializable{

    private static final long serialVersionUID = 288290684315802373L;

    //主键
    private Integer id;

    //会员名称
    private String accountName;

    //会员密码（微信小程序会员密码默认123456）
    private String accountPassword;

    //会员密码加密盐
    private String accountSalt;

    //会员手机号
    private String accountPhone;

    //会员头像
    private String accountPic;

    //性别（0-男1-女）
    private Integer accountSex;

    //地址
    private String uaddress;

    //状态0-上线1-下线
    private Integer ubalance;

    //账号状态（0-正常1-锁定2-删除）
    private Integer status;

    //会员来源（0-微信小程序1-PC注册）
    private Integer accountType;

    //绑定微信openid
    private String wxOpenid;

    //创建时间
    private String createTime;

    //修改时间
    private String updateTime;

    //删除时间
    private String deleteTime;

    //回话秘钥
    private String sessionKey;

    //uuid唯一id
    private String sKey;

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUaddress() {
        return uaddress;
    }

    public void setUaddress(String uaddress) {
        this.uaddress = uaddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getAccountSalt() {
        return accountSalt;
    }

    public void setAccountSalt(String accountSalt) {
        this.accountSalt = accountSalt;
    }

    public String getAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        this.accountPhone = accountPhone;
    }

    public String getAccountPic() {
        return accountPic;
    }

    public void setAccountPic(String accountPic) {
        this.accountPic = accountPic;
    }

    public Integer getAccountSex() {
        return accountSex;
    }

    public void setAccountSex(Integer accountSex) {
        this.accountSex = accountSex;
    }

    public Integer getUbalance() {
        return ubalance;
    }

    public void setUbalance(Integer ubalance) {
        this.ubalance = ubalance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getWxOpenid() {
        return wxOpenid;
    }

    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}
