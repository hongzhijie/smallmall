package com.smallmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * @Author hzj
 * @ClassName RentcomputerApplication
 * @Description 启动类
 * @Date 20:17 2019/1/14
 * @Param 
 * @return 
 **/

@SpringBootApplication
//启用线程池
@EnableAsync
//启用缓存
@EnableCaching
//开启事务支持
@EnableTransactionManagement
@MapperScan("com.smallmall.dao")
public class SmallmallApplication {

	public static void main(String[] args) {
        System.out.println("正在启动boot-------");
		SpringApplication.run(SmallmallApplication.class, args);
	}

}

