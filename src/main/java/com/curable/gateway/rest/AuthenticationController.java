package com.curable.gateway.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.curable.gateway.user.AppUserDTO;
import com.curable.gateway.user.UserService;
import com.curable.gateway.util.JwtTokenUtil;

/**
 * 
 * @author Annadurai S
 *
 */
@RestController
@RequestMapping("/token")
public class AuthenticationController {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@Autowired
	HttpServletRequest httpServletRequest;

	@PostMapping("/healthCheck")
	public String echoServer(@RequestBody String message) {
		message = message + " I am gateway.";
		return message;
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> authendicateUsingJwtV2(@RequestBody AppUserDTO userDetails)
			throws AuthenticationException {
		logger.info("User details ::" + userDetails);
		return userService.loadUserByUsernameByKeyCloak(userDetails);
	}

	@PostMapping("/validateToken")
	public String checkValidation(@RequestBody String token) {
		logger.info("Token: " + token);
		return jwtTokenUtil.getUsername(token);
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public ResponseEntity<?> authendicateUsingJwtuserName(@RequestBody AppUserDTO userDetails)
			throws AuthenticationException {
		logger.info("User details ::" + userDetails);
		return userService.resetPassword(userDetails);
	}
}
