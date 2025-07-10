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
                .csrf().disable() // 关闭 CSRF 防护，适用于前后端分离的接口调用
                .authorizeHttpRequests()
                .requestMatchers(
                        "/auth/login",        // 登录接口放行
                        "/auth/register",     // 注册接口放行
                        "/administrator/**",  // 后台管理接口放行（可根据需要修改）
                        "/products/**",       // 商品相关接口放行
                        "/user/**",           // 用户相关接口放行
                        // ✅ Swagger 接口文档资源放行，方便开发调试
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll() // 上述接口无需认证即可访问
                .anyRequest().authenticated() // 其余所有请求都需认证
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不使用 Session，改用 JWT 方式
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 在用户名密码过滤器之前加入 JWT 过滤器
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
