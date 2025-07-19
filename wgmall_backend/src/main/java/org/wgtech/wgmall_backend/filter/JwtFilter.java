package org.wgtech.wgmall_backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.AdministratorService;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.JwtUtils;


import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends GenericFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private AdministratorService administratorService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // 放行无需认证的路径
        if (
                path.equals("/auth/login") ||
                        path.equals("/auth/login-admin") ||
                        path.equals("/auth/login-boss") ||
                        path.equals("/auth/register") ||
                        path.startsWith("/swagger") ||
                        path.startsWith("/v3") ||
                        path.startsWith("/webjars") ||
                        path.startsWith("/uploads") ||
                        path.equals("/products/random") ||
                        path.equals("/products/type") ||
                        path.equals("/products/random/type")
        ) {
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

                    boolean isBanned = false;

                    switch (role.toUpperCase()) {
                        case "BUYER":
                        case "SELLER":
                            User user = userService.findByUsername(username);
                            isBanned = user != null && user.isBanned();
                            break;
                        case "SALES":
                        case "BOSS":
                            Administrator admin = administratorService.findByUsername(username);
                            isBanned = admin != null && admin.isBanned();
                            break;
                        default:
                            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            httpResponse.setContentType("application/json;charset=UTF-8");
                            httpResponse.getWriter().write("{\"message\": \"非法身份访问\"}");
                            return;
                    }

                    if (isBanned) {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.setContentType("application/json;charset=UTF-8");
                        httpResponse.getWriter().write("{\"message\": \"账号已被封禁，请联系管理员\"}");
                        return;
                    }

                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                    org.springframework.security.core.userdetails.User userDetails =
                            new org.springframework.security.core.userdetails.User(username, "", authorities);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"message\": \"Token 无效或已过期，请重新登录\"}");
                return;
            }
        }

        // 放行
        chain.doFilter(request, response);
    }
}
