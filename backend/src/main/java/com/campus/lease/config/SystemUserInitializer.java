package com.campus.lease.config;

import com.campus.lease.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemUserInitializer implements ApplicationRunner {

    private final SystemUserService systemUserService;

    @Value("${admin.bootstrap.username:admin}")
    private String username;

    @Value("${admin.bootstrap.password:Admin@123456}")
    private String password;

    @Value("${admin.bootstrap.display-name:系统管理员}")
    private String displayName;

    @Override
    public void run(ApplicationArguments args) {
        systemUserService.ensureBootstrapAdmin(username, password, displayName);
    }
}
