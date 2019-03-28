package com.smallmall.service.goods;

import com.smallmall.dao.LitemallSystemMapper;
import com.smallmall.model.goods.LitemallSystem;
import com.smallmall.model.goods.LitemallSystemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LitemallSystemConfigService {
    @Autowired
    private LitemallSystemMapper systemMapper;

    public List<LitemallSystem> queryAll() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or();
        return systemMapper.selectByExample(example);
    }
}
