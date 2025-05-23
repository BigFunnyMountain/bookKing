package xyz.tomorrowlearncamp.bookking;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableJpaRepositories(
	basePackages = "xyz.tomorrowlearncamp.bookking",
	entityManagerFactoryRef = "entityManagerFactory"
)
public class BookKingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookKingApplication.class, args);
	}

}
