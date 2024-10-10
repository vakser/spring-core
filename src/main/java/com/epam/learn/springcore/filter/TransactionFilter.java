package com.epam.learn.springcore.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove("transactionId");
        }
    }

}
