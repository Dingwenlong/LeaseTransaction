package com.campus.lease.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.campus.lease.mapper")
public class MybatisPlusConfig {

}
