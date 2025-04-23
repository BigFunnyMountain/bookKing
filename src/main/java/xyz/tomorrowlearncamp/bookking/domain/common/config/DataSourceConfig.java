package xyz.tomorrowlearncamp.bookking.domain.common.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
public class DataSourceConfig {

	public static final String WRITER = "writer";
	public static final String READER = "reader";
	public static final String WRITER_DATASOURCE = "writerDataSource";
	public static final String READER_DATASOURCE = "readerDataSource";

	@Bean(name = WRITER_DATASOURCE)
	@ConfigurationProperties(prefix = "spring.datasource.hikari.writer")
	public DataSource writerDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = READER_DATASOURCE)
	@ConfigurationProperties(prefix = "spring.datasource.hikari.reader")
	public DataSource readerDataSource() {
		HikariDataSource hikariDataSource = DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
		hikariDataSource.setReadOnly(true);
		return hikariDataSource;
	}

	@Bean
	@DependsOn({WRITER_DATASOURCE, READER_DATASOURCE})
	public DataSource routingDataSource(
		@Qualifier(WRITER_DATASOURCE) DataSource writerDataSource,
		@Qualifier(READER_DATASOURCE) DataSource readerDataSource
	) {
		Map<Object, Object> dataSources = new HashMap<>();
		dataSources.put(WRITER, writerDataSource);
		dataSources.put(READER, readerDataSource);

		RoutingDataSource routingDataSource = new RoutingDataSource();
		routingDataSource.setTargetDataSources(dataSources);
		routingDataSource.setDefaultTargetDataSource(writerDataSource);
		return routingDataSource;
	}

	@Bean
	@Primary
	@DependsOn("routingDataSource")
	public DataSource dataSource(DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}
}
