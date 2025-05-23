package xyz.tomorrowlearncamp.bookking.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import xyz.tomorrowlearncamp.bookking.common.exception.AccessDeniedHandlerImpl;
import xyz.tomorrowlearncamp.bookking.common.jwt.JwtAuthenticationFilter;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

import java.util.List;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors
				.configurationSource(request -> {
					CorsConfiguration config = new CorsConfiguration();
					config.addAllowedOriginPattern("*");
					config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
					config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
					config.setAllowCredentials(true);
					return config;
				})
			)
			.exceptionHandling(
				configurer ->
					configurer.accessDeniedHandler(new AccessDeniedHandlerImpl())
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.formLogin(AbstractHttpConfigurer::disable)
			.anonymous(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v*/auth/**").permitAll()
				.requestMatchers("/api/v*/users/**").authenticated()
				.requestMatchers(HttpMethod.GET, "/api/v*/books/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/books/keywords/suggest").authenticated()
				.requestMatchers(HttpMethod.PATCH, "/api/v*/users/*/role").hasAuthority(UserRole.ROLE_ADMIN.name())
				.requestMatchers("/api/v*/books/**").hasAuthority(UserRole.ROLE_ADMIN.name())
				.requestMatchers("/api/v1/elasticsearch/reindex").hasAuthority(UserRole.ROLE_ADMIN.name())
				.requestMatchers("/api/test/**").hasAuthority(UserRole.ROLE_ADMIN.name())
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
					"/swagger-resources/**", "/webjars/**").permitAll()
				.requestMatchers("/actuator", "/actuator/**", "/_cluster/health").permitAll()
				.anyRequest().authenticated()
			)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
