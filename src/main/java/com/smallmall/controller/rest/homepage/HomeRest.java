package com.smallmall.controller.rest.homepage;

import com.smallmall.model.LitemallCategory;
import com.smallmall.model.LitemallGoods;
import com.smallmall.service.LitemallAdService;
import com.smallmall.service.LitemallCategoryService;
import com.smallmall.service.LitemallCouponService;
import com.smallmall.service.LitemallGoodsService;
import com.smallmall.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/*
 * @Author hzj
 * @ClassName HomeRest
 * @Description 小程序首页相关接口
 * @Date 17:06 2019/3/27
 **/
@RestController
@RequestMapping("ws/homePage")
public class HomeRest {

    @Autowired
    private LitemallAdService adService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallCouponService couponService;

    /**
     * 首页数据
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/index")
    @ResponseBody
    public Object index(Integer userId) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Map<String, Object> data = new HashMap<>();
        Callable<List> bannerListCallable = () -> adService.queryIndex();
        Callable<List> channelListCallable = () -> categoryService.queryChannel();
        Callable<List> couponListCallable;
        if(userId == null){
            couponListCallable = () -> couponService.queryList(0, 3);
        } else {
            couponListCallable = () -> couponService.queryAvailableList(userId,0, 3);
        }


        Callable<List> newGoodsListCallable = () -> goodsService.queryByNew(0, 6);

        Callable<List> hotGoodsListCallable = () -> goodsService.queryByHot(0, 6);

//        Callable<List> brandListCallable = () -> brandService.queryVO(0, 4);

//        Callable<List> topicListCallable = () -> topicService.queryList(0, 4);

        //团购专区
//        Callable<List> grouponListCallable = () -> grouponRulesService.queryList(0, 5);

        Callable<List> floorGoodsListCallable = this::getCategoryList;

        FutureTask<List> bannerTask = new FutureTask<>(bannerListCallable);
        FutureTask<List> channelTask = new FutureTask<>(channelListCallable);
        FutureTask<List> couponListTask = new FutureTask<>(couponListCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsListCallable);
        FutureTask<List> hotGoodsListTask = new FutureTask<>(hotGoodsListCallable);
//        FutureTask<List> brandListTask = new FutureTask<>(brandListCallable);
//        FutureTask<List> topicListTask = new FutureTask<>(topicListCallable);
//        FutureTask<List> grouponListTask = new FutureTask<>(grouponListCallable);
        FutureTask<List> floorGoodsListTask = new FutureTask<>(floorGoodsListCallable);

        executorService.submit(bannerTask);
        executorService.submit(channelTask);
        executorService.submit(couponListTask);
        executorService.submit(newGoodsListTask);
        executorService.submit(hotGoodsListTask);
//        executorService.submit(brandListTask);
//        executorService.submit(topicListTask);
//        executorService.submit(grouponListTask);
        executorService.submit(floorGoodsListTask);

        try {
            data.put("banner", bannerTask.get());
            data.put("channel", channelTask.get());
            data.put("couponList", couponListTask.get());
            data.put("newGoodsList", newGoodsListTask.get());
            data.put("hotGoodsList", hotGoodsListTask.get());
            data.put("brandList", "");
            data.put("topicList", "");
            data.put("grouponList", "");
            data.put("floorGoodsList", floorGoodsListTask.get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //缓存数据
        executorService.shutdown();
        return ResponseUtil.ok(data);
    }

    private List<Map> getCategoryList() {
        List<Map> categoryList = new ArrayList<>();
        List<LitemallCategory> catL1List = categoryService.queryL1WithoutRecommend(0,4);
        for (LitemallCategory catL1 : catL1List) {
            List<LitemallCategory> catL2List = categoryService.queryByPid(catL1.getId());
            List<Integer> l2List = new ArrayList<>();
            for (LitemallCategory catL2 : catL2List) {
                l2List.add(catL2.getId());
            }

            List<LitemallGoods> categoryGoods;
            if (l2List.size() == 0) {
                categoryGoods = new ArrayList<>();
            } else {
                categoryGoods = goodsService.queryByCategory(l2List, 0,4);
            }

            Map<String, Object> catGoods = new HashMap<>();
            catGoods.put("id", catL1.getId());
            catGoods.put("name", catL1.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        }
        return categoryList;
    }
}
