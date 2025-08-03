package com.peter.tanxuannewapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        String[] whiteList = {
//                "/",
//                "/api/v1/login", "/api/v1/register", "/storage/**", "/api/v1/account", "/api/v1/admin/login",
//                "/api/v1/admin/register", "/api/v1/products", "/api/v1/categories"
//        };
//        registry.addInterceptor(permissionInterceptor()).excludePathPatterns(whiteList);
//    }
}
