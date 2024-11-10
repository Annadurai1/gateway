package com.curable.gateway.config;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netflix.zuul.context.RequestContext;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class Slf4jMDCFilter extends OncePerRequestFilter {

	private final String responseHeader;
	private final String mdcTokenKey;
	private final String requestHeader;

	public Slf4jMDCFilter() {
		responseHeader = Slf4jMDCFilterConfiguration.DEFAULT_RESPONSE_TOKEN_HEADER;
		mdcTokenKey = Slf4jMDCFilterConfiguration.DEFAULT_MDC_UUID_TOKEN_KEY;
		requestHeader = Slf4jMDCFilterConfiguration.DEFAULT_REQUEST_TOKEN_HEADER;
	}

	public Slf4jMDCFilter(final String responseHeader, final String mdcTokenKey, final String requestHeader) {
		this.responseHeader = responseHeader;
		this.mdcTokenKey = mdcTokenKey;
		this.requestHeader = requestHeader;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws java.io.IOException, ServletException {
		try {
			final String token;
			if (!StringUtils.isEmpty(requestHeader) && !StringUtils.isEmpty(request.getHeader(requestHeader))) {
				token = request.getHeader(requestHeader);
			} else {
				token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
			}
			MDC.put(mdcTokenKey, token);
			RequestContext.getCurrentContext().addZuulRequestHeader(requestHeader, token);
			if (!StringUtils.isEmpty(responseHeader)) {
				response.addHeader(responseHeader, token);
			}
			
			chain.doFilter(request, response);
		} finally {
			MDC.remove(mdcTokenKey);
		}
	}
}
