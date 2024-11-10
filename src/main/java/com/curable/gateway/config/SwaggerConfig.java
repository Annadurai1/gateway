package com.curable.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Primary
@Configuration
public class SwaggerConfig implements SwaggerResourcesProvider {
	@Autowired
	private RouteLocator routeLocator;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30).select()
				.apis(RequestHandlerSelectors.basePackage("com.curable.gateway.rest")).paths(PathSelectors.any()).build();
	}

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<>();
		resources.add(swaggerResource("gateway", "/v2/api-docs"));
		routeLocator.getRoutes().stream().forEach(route -> resources
				.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"))));
		return resources;
	}

	private SwaggerResource swaggerResource(final String name, final String location) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion("3.0");
		return swaggerResource;
	}
}