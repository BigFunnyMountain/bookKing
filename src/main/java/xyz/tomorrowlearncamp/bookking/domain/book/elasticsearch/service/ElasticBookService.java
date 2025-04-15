package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.ElasticBookSearchResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.entity.ElasticBookDocument;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticBookService {

    private final ElasticsearchClient client;

    public void save(ElasticBookDocument document) {
        try {
            client.index(IndexRequest.of(i -> i
                    .index("books")
                    .id(document.bookId().toString())
                    .document(document)
            ));
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 색인 실패", e);
        }
    }

    public List<ElasticBookSearchResponseDto> search(String keyword) {
        try {
            SearchResponse<ElasticBookDocument> response = client.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("title", "author", "publisher", "subject")
                                            .query(keyword)
                                    )
                            ),
                    ElasticBookDocument.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(ElasticBookSearchResponseDto::of)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 실패", e);
        }
    }

    /*
     TODO : 하나의 키워드로 여러가지 검색 될 수 있게, 예) '짱구'검색하면, title, 저자, 뭐 어디서든 '짱구'가 포함된 정보는 검색될 수 있게.
     */
}