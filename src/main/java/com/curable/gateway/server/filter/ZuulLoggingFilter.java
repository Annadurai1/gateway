package com.curable.gateway.server.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @author Annadurai S
 *
 */
@Component
public class ZuulLoggingFilter extends ZuulFilter {

	private static final Logger log = LoggerFactory.getLogger(ZuulLoggingFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		log.info("request -> {} method {} request uri -> {}", request, request.getMethod(), request.getRequestURI());
		try {
			if (log.isTraceEnabled() && !request.getMethod().equals("GET")) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] contentData = new byte[0];
				try {
					IOUtils.copy(request.getInputStream(), baos);
					contentData = baos.toByteArray();
					log.trace("contentData -> {}", baos.toString("UTF-8"));
				} catch (SocketTimeoutException e) {
					log.error("SocketTimeoutException => ", e);
					log.trace("SocketTimeoutException reading request body from inputstream. error {}",
							String.valueOf(e.getMessage()));
				}
				log.trace("ContentLength={},ContentDataLength={}", request.getContentLength(), contentData.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}
}
