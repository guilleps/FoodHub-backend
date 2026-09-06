package com.project.FoodHub.config.cors;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebConfig implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        filterChain.doFilter(servletRequest, servletResponse);

        log.info(
                "METHOD: {} - STATUS CODE: {} - REQUEST: [{}] - ORIGIN: [{}]",
                request.getMethod(),
                response.getStatus(),
                request.getRequestURI(),
                request.getHeader("Origin")
        );

    }
}
