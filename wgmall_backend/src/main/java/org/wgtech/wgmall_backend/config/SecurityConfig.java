package org.wgtech.wgmall_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.wgtech.wgmall_backend.filter.JwtFilter;

@Configuration // 声明这是一个配置类，相当于 applicationContext.xml 的替代
public class SecurityConfig {

    /**
     * 配置 Spring Security 的过滤器链
     *
     * @param http      HttpSecurity 对象，用于构建安全策略
     * @param jwtFilter 自定义的 JWT 过滤器，用于解析和验证 Token
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        return http
                .cors() // 👈 添加这一行，启用 CORS 支持
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/auth/login",
                        "/auth/register",
                        "/auth/login-admin",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/uploads/products/**",
                        "/products/random",
                        "/products/type").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }



    /**
     * 注入 AuthenticationManager，提供认证功能（用于登录验证等）
     *
     * @param config AuthenticationConfiguration 系统自动注入的认证配置
     * @return AuthenticationManager 身份认证管理器
     * @throws Exception 获取过程中可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
