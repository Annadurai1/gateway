package com.curable.gateway.util;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @author Annadurai S
 *
 */
public class InvalidJwtAuthenticationException extends AuthenticationException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}