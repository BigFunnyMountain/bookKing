package xyz.tomorrowlearncamp.bookking.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.tomorrowlearncamp.bookking.common.enums.DataSourceType;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

	public static final DataSourceType WRITER = DataSourceType.WRITER;
	public static final DataSourceType READER = DataSourceType.READER;
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
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
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

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		@Qualifier("dataSource") DataSource dataSource,
		JpaProperties jpaProperties
	) {
		Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		properties.put("hibernate.hbm2ddl.auto", "none");
		properties.put("hibernate.physical_naming_strategy",
			"org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
		properties.put("hibernate.connection.provider_disables_autocommit", false);

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("xyz.tomorrowlearncamp.bookking.domain", "xyz.tomorrowlearncamp.bookking.common.entity");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(properties);
		return em;
	}
}
