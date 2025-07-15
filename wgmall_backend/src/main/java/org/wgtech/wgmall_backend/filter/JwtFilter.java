package org.wgtech.wgmall_backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.wgtech.wgmall_backend.utils.JwtUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String path = httpRequest.getRequestURI();

        // 放行无需认证的路径
        if (path.startsWith("/auth/")
                || path.startsWith("/swagger")
                || path.startsWith("/v3")
                || path.startsWith("/webjars")
                || path.startsWith("/uploads")
                || path.startsWith("/products")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                if (JwtUtils.validateToken(jwt)) {
                    String username = JwtUtils.getUsernameFromToken(jwt);
                    String role = JwtUtils.getRoleFromToken(jwt);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null,
                                    List.of(new SimpleGrantedAuthority(role)));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 解析失败不设置认证状态，但也不直接响应，让 Spring Security 自己处理
            }
        }

        // 最终一定要放行，不要直接 return
        chain.doFilter(request, response);
    }

}
