package com.smallmall.service.goods;

import com.github.pagehelper.PageHelper;
import com.smallmall.dao.goods.LitemallIssueMapper;
import com.smallmall.model.goods.LitemallIssue;
import com.smallmall.model.goods.LitemallIssueExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallIssueService {
    @Autowired
    private LitemallIssueMapper issueMapper;

    public List<LitemallIssue> query() {
        LitemallIssueExample example = new LitemallIssueExample();
        example.or().andDeletedEqualTo(false);
        return issueMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        issueMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallIssue issue) {
        issue.setAddTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        issueMapper.insertSelective(issue);
    }

    public List<LitemallIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(question)) {
            criteria.andQuestionLike("%" + question + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return issueMapper.selectByExample(example);
    }

    public int updateById(LitemallIssue issue) {
        issue.setUpdateTime(LocalDateTime.now());
        return issueMapper.updateByPrimaryKeySelective(issue);
    }

    public LitemallIssue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
