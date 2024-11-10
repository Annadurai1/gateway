package com.curable.gateway.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.curable.gateway.server.filter.JwtAuthenticationFilter;

/**
 * WebSecurityConfigurerAdapter extends and configure the http and https access.
 * JwtAuthenticationFilter implemented.
 * 
 *
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] SWAGGER_UNSECURE_PATTERNS = { "/**/swagger-ui/**", "/**/api/v2/**",
			"/api/swagger.json", "/**/v2/api-docs", "/configuration/ui", "/swagger-resources",
			"/configuration/security", "/webjars/**", "/swagger-resources/configuration/ui",
			"/swagger-resources/configuration/security", "/token/**"};

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationFilter();
	}

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Autowired
	private RESTAuthenticationFailureHandler authenticationFailureHandler;
	@Autowired
	private RESTAuthenticationSuccessHandler authenticationSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Note that the CSRf token is disabled for all requests
		log.info("Disabling CSRF, enabling basic authentication...");

		http.formLogin().permitAll().loginProcessingUrl("/login").successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler);

		http.cors().and().csrf().disable().authorizeRequests().antMatchers(SWAGGER_UNSECURE_PATTERNS).permitAll()
				.anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
