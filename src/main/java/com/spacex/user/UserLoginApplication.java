package com.spacex.user;

import com.spacex.user.repository.mapper.BaseMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackageClasses = BaseMapper.class)
public class UserLoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserLoginApplication.class);
    }
}
