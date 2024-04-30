package com.heima;

import com.heima.commons.initial.annotation.EnableRequestInital;
import com.heima.commons.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableRequestInital
@MapperScan("com.heima.storage.mapper")
@Import(SpringUtil.class)
//开启缓存注解
@EnableCaching

public class AllinoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(AllinoneApplication.class, args);

    }
}
