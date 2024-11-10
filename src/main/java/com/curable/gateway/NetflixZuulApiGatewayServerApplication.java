package com.curable.gateway;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
@EnableZuulProxy
@SpringBootApplication
public class NetflixZuulApiGatewayServerApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(NetflixZuulApiGatewayServerApplication.class);

	private static final HttpMethod[] SUPPORTED_MULTIPART_METHODS = { HttpMethod.POST, HttpMethod.PUT };

	public static void main(String[] args) {
		log.info("NetflixZuul Gateway Server starting...");
		SpringApplication.run(NetflixZuulApiGatewayServerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NetflixZuulApiGatewayServerApplication.class);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*");
			}
		};
	}

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		config.setAllowedMethods(Arrays.stream(HttpMethod.values()).map(HttpMethod::name).collect(Collectors.toList()));
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver() {
			@Override
			public boolean isMultipart(HttpServletRequest request) {
				boolean methodMatches = Arrays.stream(SUPPORTED_MULTIPART_METHODS)
						.anyMatch(method -> method.matches(request.getMethod()));
				String contentType = request.getContentType();
				return methodMatches && (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
			}
		};
	}

	@GetMapping("/")
	public String root() {
		return "redirect:/swagger-ui/";
	}

	@GetMapping("/swagger-ui.html")
	public String swaggerUI() {
		return "redirect:/swagger-ui/";
	}
}
