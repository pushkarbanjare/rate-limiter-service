package com.pushkar.ratelimiter.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.pushkar.ratelimiter.service.RateLimiterService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter implements Filter {
    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientId = request.getRemoteAddr();

        if (rateLimiterService.isAllowed(clientId))
            chain.doFilter(servletRequest, servletResponse);
        else {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Try again later");
        }
    }
}
