package com.smallmall.service;

import com.github.pagehelper.PageHelper;
import com.smallmall.dao.LitemallRegionMapper;
import com.smallmall.model.LitemallRegion;
import com.smallmall.model.LitemallRegionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LitemallRegionService {

    @Autowired
    private LitemallRegionMapper regionMapper;

    public List<LitemallRegion> getAll(){
        LitemallRegionExample example = new LitemallRegionExample();
        byte b = 4;
        example.or().andTypeNotEqualTo(b);
        return regionMapper.selectByExample(example);
    }

    public List<LitemallRegion> queryByPid(Integer parentId) {
        LitemallRegionExample example = new LitemallRegionExample();
        example.or().andPidEqualTo(parentId);
        return regionMapper.selectByExample(example);
    }

    public LitemallRegion findById(Integer id) {
        return regionMapper.selectByPrimaryKey(id);
    }

    public List<LitemallRegion> querySelective(String name, Integer code, Integer page, Integer size, String sort, String order) {
        LitemallRegionExample example = new LitemallRegionExample();
        LitemallRegionExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(code)) {
            criteria.andCodeEqualTo(code);
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return regionMapper.selectByExample(example);
    }
}
