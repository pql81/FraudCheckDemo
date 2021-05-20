package com.pql.fraudcheck.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Created by pasqualericupero on 13/05/2021.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+1)
@Log4j2
public class AuthFilter extends OncePerRequestFilter {


    @Value("${jwt.enc.enabled: false}")
    private Boolean enabled;

    @Value("${jwt.enc.secret: }")
    private String secret;

    private final static String JWT_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (enabled) {
            log.info("Check jwt");

            String jwt = httpServletRequest.getHeader("Authorization");

            try {
                if (jwt.startsWith(JWT_PREFIX)) {
                    jwt = jwt.replace(JWT_PREFIX, "");

                    Claims claims = Jwts.parser()
                            .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                            .parseClaimsJws(jwt).getBody();

                    if (!claims.getIssuer().equals("trans-service")) {
                        log.error("Unauthorized - invalid issuer");
                        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user.");
                        return;
                    }

                    httpServletRequest.setAttribute("correlation-id", claims.getId());

                } else {
                    log.error("Unauthorized - invalid jwt");
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user.");
                    return;
                }
            } catch (Exception e) {
                log.error("Unauthorized", e);
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication");
                return;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/fraud-check");
    }
}
