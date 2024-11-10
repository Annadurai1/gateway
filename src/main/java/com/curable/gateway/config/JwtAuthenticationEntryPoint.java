package com.curable.gateway.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
/**
 * This class used to add the AuthenticationEntryPoints restrictions.
 * 
 * 
 *
 */
@Component("restAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println("{ \"error\": \"" + authenticationException.getMessage() + "\" }");
        logger.error("AuthenticationEntryPoint : " +authenticationException.getMessage());

    }
}