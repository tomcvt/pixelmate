package com.tomcvt.pixelmate.auth;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.tomcvt.pixelmate.exceptions.IllegalUsageException;
import com.tomcvt.pixelmate.network.BanRegistry;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
    private final LoginTracker loginTracker;
    private final BanRegistry banRegistry;
    
    public AuthFailureHandler(LoginTracker loginTracker, BanRegistry banRegistry) {
        this.loginTracker = loginTracker;
        this.banRegistry = banRegistry;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException exception)
            throws java.io.IOException, ServletException {
        String clientIp = request.getRemoteAddr();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            clientIp = xff.split(",")[0].trim();
        }
        try {
            loginTracker.recordFailedLogin(clientIp);
        } catch (IllegalUsageException e) {
            banRegistry.banIp(clientIp, 15);
        }
        response.sendRedirect("/login?error");
    }
}
