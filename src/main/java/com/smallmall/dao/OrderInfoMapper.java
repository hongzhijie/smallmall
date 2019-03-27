package com.smallmall.dao;


import com.smallmall.model.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/*
 * @Author hzj
 * @ClassName OrderInfoMapper
 * @Description 订单实现mapper
 * @Date 15:41 2019/1/18
 * @Param 
 * @return 
 **/
@Mapper
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);
}