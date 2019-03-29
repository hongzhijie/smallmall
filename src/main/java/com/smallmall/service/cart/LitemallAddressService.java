package com.smallmall.service.cart;

import com.github.pagehelper.PageHelper;
import com.smallmall.dao.cart.LitemallAddressMapper;
import com.smallmall.model.cart.LitemallAddress;
import com.smallmall.model.cart.LitemallAddressExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAddressService {
    @Autowired
    private LitemallAddressMapper addressMapper;

    public List<LitemallAddress> queryByUid(Integer uid) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        return addressMapper.selectByExample(example);
    }

    public LitemallAddress findById(Integer id) {
        return addressMapper.selectByPrimaryKey(id);
    }

    public int add(LitemallAddress address) {
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.insertSelective(address);
    }

    public int update(LitemallAddress address) {
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.updateByPrimaryKeySelective(address);
    }

    public void delete(Integer id) {
        addressMapper.logicalDeleteByPrimaryKey(id);
    }

    public LitemallAddress findDefault(Integer userId) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return addressMapper.selectOneByExample(example);
    }

    public void resetDefault(Integer userId) {
        LitemallAddress address = new LitemallAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        addressMapper.updateByExampleSelective(address, example);
    }

    public List<LitemallAddress> querySelective(Integer userId, String name, Integer page, Integer limit, String sort, String order) {
        LitemallAddressExample example = new LitemallAddressExample();
        LitemallAddressExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return addressMapper.selectByExample(example);
    }
}
