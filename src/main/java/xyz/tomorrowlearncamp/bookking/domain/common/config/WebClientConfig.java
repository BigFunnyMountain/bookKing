package xyz.tomorrowlearncamp.bookking.domain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.AccessDeniedHandlerImpl;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebClientConfig {

	private final AccessDeniedHandlerImpl accessDeniedHandler;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs()
					.maxInMemorySize(10 * 1024 * 1024))
				.build())
			.build();
	}
}
