package com.curable.gateway.server.filter;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * This class used to add the default header in Zuul requests. [Authorization]
 * @author Annadurai S
 *
 */
public class PreFilter extends ZuulFilter {
	private static final Logger log = LoggerFactory.getLogger(PreFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }
    @Override
    public int filterOrder() {
        return 1;
    }
    @Override
    public boolean shouldFilter() {
        return true;
    }
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //--copyRequestHeaders(ctx);
        HttpServletRequest request = ctx.getRequest();    
        ctx.addZuulRequestHeader("Authorization",request.getHeader("Authorization"));
        log.debug("http request {}",ctx );
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        return null;
    }
    
    /**
     * 
     * @param context
     */
	private void copyRequestHeaders(RequestContext context ) {
         Enumeration<String> headerNames = context.getRequest().getHeaderNames();
        while(headerNames.hasMoreElements()) {
           String headerName = headerNames.nextElement();
           context.addZuulRequestHeader(headerName, context.getRequest().getHeader(headerName));
        }
    }
}