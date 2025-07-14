package org.wgtech.wgmall_backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.wgtech.wgmall_backend.utils.JwtUtils;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // 路径白名单：跳过 token 校验
        if (path.startsWith("/auth/") || path.startsWith("/swagger") || path.startsWith("/v3")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                if (JwtUtils.validateToken(jwt)) {
                    String username = JwtUtils.getUsernameFromToken(jwt);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } else {
                // 没 token 也拒绝
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token");
                return;
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            if (!httpResponse.isCommitted()) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
            }
        }
    }

}
