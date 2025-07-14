package org.wgtech.wgmall_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 如果前端要发送 cookies，一定要设置这个
        config.setAllowedOriginPatterns(List.of("*")); // 允许所有来源
        config.setAllowedHeaders(List.of("*"));        // 允许所有请求头
        config.setAllowedMethods(List.of("*"));        // 允许所有 HTTP 方法
        config.setMaxAge(3600L);                       // 预检请求缓存时间
        config.setExposedHeaders(List.of("Authorization")); // 如果需要暴露 JWT 等头部

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean(CorsConfigurationSource source) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0); // 确保优先执行
        return bean;
    }
}
