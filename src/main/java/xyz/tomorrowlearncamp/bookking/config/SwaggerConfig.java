package xyz.tomorrowlearncamp.bookking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
	info = @Info(
		title = "API 명세서",
		description = "API 명세서",
		version = "v1"
	)
)
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openApi() {
		SecurityScheme scheme = new SecurityScheme();
		scheme.type(Type.HTTP)
			.in(In.HEADER)
			.name("Authorization")
			.scheme("bearer")
			.bearerFormat("JWT");

		SecurityRequirement requirement = new SecurityRequirement()
			.addList("Bearer Token");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("Bearer Token", scheme))
			.addSecurityItem(requirement)
			.addServersItem(new Server().url("/"));
	}
}
