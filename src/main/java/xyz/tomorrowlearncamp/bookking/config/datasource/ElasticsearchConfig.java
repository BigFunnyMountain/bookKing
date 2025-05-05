package xyz.tomorrowlearncamp.bookking.config.datasource;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(host)).build();
    }

    @Bean
    public RestClientTransport restClientTransport() {
        return new RestClientTransport(restClient(), new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(restClientTransport());
    }
}