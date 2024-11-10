package com.curable.gateway.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.curable.gateway.user.AppUserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 */
@Component
public class JwtTokenUtil implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 90 * (24 * 3600000); // 1h * 24 h * days

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	/**
	 * 
	 */
	public String generate(AppUserDTO jwtUser) {
		Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
		claims.put("userId", String.valueOf(jwtUser.getId()));
		claims.put("role", jwtUser.getUserName());
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	/**
	 * 
	 * @param username
	 * @param roles
	 * @return
	 */
	public String createToken(AppUserDTO jwtUser, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
		claims.put("userName", jwtUser.getUserName());
		claims.put("roles", roles);
		// claims.put("password", jwtUser.getPassword());
		Date now = new Date();

		Date date = new Date();
		long timeLong = date.getTime();
		Date expirationTime = new Date(timeLong + validityInMilliseconds); // set seconds

		logger.info("Secret Key  :::: " + secretKey);
		logger.info("TOKEN validity :::: " + validityInMilliseconds);
		logger.info("TOKEN expirationTime :::: " + expirationTime);

		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.setExpiration(expirationTime)//
				.signWith(SignatureAlgorithm.HS512, secretKey)//
				.compact();
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public String getUsername(String token) {
		logger.info("User name ::" + token);
		logger.info("Token expired ::"
				+ Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration());
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject() + "--->"
				+ Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

			logger.info("validateToken name ::" + claims.getBody().getExpiration());

			if (claims.getBody().getExpiration().before(new Date())) {
				return false;
			}

			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
		}
	}

	/**
	 * 
	 * @param authToken
	 * @return
	 */
	public String getUsernameFromToken(String authToken) {
		return getUsername(authToken);
	}

	/**
	 * 
	 * @param authToken
	 * @param userDetails
	 * @return
	 */
	public boolean validateToken(String authToken, UserDetails userDetails) {
		return validateToken(authToken);
	}

	public boolean isValidToken(String authToken) {
		return validateToken(authToken);
	}

	/**
	 * Tries to parse specified String as a JWT token. If successful, returns User
	 * object with username, id and role prefilled (extracted from token). If
	 * unsuccessful (token is invalid or not containing all required user
	 * properties), simply returns null.
	 * 
	 * @param token the JWT token to parse
	 * @return the User object extracted from specified token or null if a token is
	 *         invalid.
	 */
	public AppUserDTO parseToken(String token) {
		try {
			Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
			AppUserDTO u = new AppUserDTO();
			u.setUserName(body.getSubject());
			u.setId(Long.parseLong((String) body.get("userId")));
			u.setUserName((String) body.get("username"));
			u.setRole((String) body.get("role"));

			return u;

		} catch (JwtException | ClassCastException e) {
			return null;
		}
	}

	/**
	 * Generates a JWT token containing username as subject, and userId and role as
	 * additional claims. These properties are taken from the specified User object.
	 * Tokens validity is infinite.
	 * 
	 * @param u the user for which the token will be generated
	 * @return the JWT token
	 */
	public String generateToken(AppUserDTO u) {
		Claims claims = Jwts.claims().setSubject(u.getUserName());
		claims.put("userId", u.getId() + "");
		claims.put("role", u.getRole());

		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	/**
	 * 
	 * @param jwtUser
	 * @param roles
	 * @param expirationTime
	 * @return
	 */
	public String createAccessToken(AppUserDTO jwtUser, List<String> roles, Date expirationTime) {
		Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
		claims.put("userName", jwtUser.getUserName());
		claims.put("roles", roles);
		// claims.put("password", jwtUser.getPassword());
		Date now = new Date();

		logger.info("Secret Key  [static]:::: " + secretKey);
		logger.info("TOKEN validity :::: " + validityInMilliseconds);
		logger.info("TOKEN expirationTime :::: " + expirationTime);
		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.setExpiration(expirationTime)//
				.signWith(SignatureAlgorithm.HS512, secretKey)//
				.compact();
	}

}
