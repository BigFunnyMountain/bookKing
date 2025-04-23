package xyz.tomorrowlearncamp.bookking.domain.common.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Bean
	public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
		return new RedissonConnectionFactory(redisson);
	}

	@Bean(destroyMethod="shutdown")
	public RedissonClient redissonClient() throws IOException {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("rediss://" + redisHost);
		return Redisson.create(config);
	}
}
