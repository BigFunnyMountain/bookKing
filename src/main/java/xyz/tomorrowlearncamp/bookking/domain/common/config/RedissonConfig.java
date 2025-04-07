package xyz.tomorrowlearncamp.bookking.domain.common.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableCaching
public class RedissonConfig {

	@Bean
	public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
		return new RedissonConnectionFactory(redisson);
	}

	@Bean(destroyMethod="shutdown")
	public RedissonClient redissonClient(@Value("classpath:/redisson.yml") Resource configFile) throws IOException {
		Config config = Config.fromYAML(configFile.getInputStream());
		return Redisson.create(config);
	}
}
