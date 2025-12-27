package com.tomcvt.pixelmate.network;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tomcvt.pixelmate.exceptions.IllegalUsageException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimitingFilter.class);
    private final IpRegistry ipRegistry;
    private final BanRegistry banRegistry;
    private final String [] excludedUri = { "/api/", "/generated/", "/images/", "/js/", "/css/", "/static/" };
    private final String [] excludedIps = { "0.0.0.0.0.0.0.1"};

    public RateLimitingFilter(IpRegistry ipRegistry, BanRegistry banRegistry) {
        this.ipRegistry = ipRegistry;
        this.banRegistry = banRegistry;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            UserDetails user = (UserDetails) principal;
            if (user != null) {
                log.info("Authenticated user {}, skipping rate limiting.", user.getUsername());
                filterChain.doFilter(request, response);
                return;
            }
        }
        String clientIp = request.getRemoteAddr();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            clientIp = xff.split(",")[0].trim();
        }
        for (String ip : excludedIps) {
            if (clientIp.equals(ip)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        String requestUri = request.getRequestURI();
        for (String uri : excludedUri) {
            if (requestUri.startsWith(uri)) {
                filterChain.doFilter(request, response);
                return;
            }
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
