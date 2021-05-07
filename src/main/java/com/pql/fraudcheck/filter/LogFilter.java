package com.pql.fraudcheck.filter;

import lombok.extern.log4j.Log4j2;
import org.jboss.logging.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+1)
@Log4j2
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("requestId", UUID.randomUUID().toString());

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
