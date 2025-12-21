package com.tomcvt.pixelmate.network;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tomcvt.pixelmate.exceptions.IllegalUsageException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final IpRegistry ipRegistry;
    private final BanRegistry banRegistry;

    public RateLimitingFilter(IpRegistry ipRegistry, BanRegistry banRegistry) {
        this.ipRegistry = ipRegistry;
        this.banRegistry = banRegistry;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        String clientIp = request.getRemoteAddr();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            clientIp = xff.split(",")[0].trim();
        }
        if (banRegistry.isBanned(clientIp)) {
            response.setStatus(403); // Forbidden
            response.getWriter().write("IP " + clientIp + " is temporarily banned.");
            return;
        }

        try {
            boolean allowed = ipRegistry.incrementAndCheckIfAllowed(clientIp);
            if (!allowed) {
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("Hourly request limit exceeded.");
                return;
            }
        } catch (IllegalUsageException e) {
            banRegistry.banIp(clientIp);
            response.setStatus(403); // Forbidden
            response.getWriter().write(e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
