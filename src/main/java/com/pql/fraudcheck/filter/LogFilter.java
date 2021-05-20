package com.pql.fraudcheck.filter;

import lombok.extern.log4j.Log4j2;
import org.jboss.logging.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+2)
@Log4j2
public class LogFilter implements Filter {

    private final static String REQUEST_ID_HEADER = "correlation-id";
    private final static String REQUEST_ID_MDC = "requestId";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        try {
            // try to retrieve the transaction id or request id from header - this is a demo so we wont't get any value here
            String reqId = request.getHeader(REQUEST_ID_HEADER);

            // let the filter generate a UUID instead of getting it from attributes (-> jwt)
//            if (reqId == null && request.getAttribute(REQUEST_ID_HEADER) != null) {
//                reqId = request.getAttribute(REQUEST_ID_HEADER).toString();
//            }

            // this is likely to happen!
            if (reqId == null) {
                reqId = UUID.randomUUID().toString();
            }

            MDC.put(REQUEST_ID_MDC, reqId);

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            MDC.clear();
        }
    }
}
