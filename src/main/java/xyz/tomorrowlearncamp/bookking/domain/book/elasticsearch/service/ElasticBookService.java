package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest.Builder;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.document.ElasticBookDocument;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.ElasticBookSearchResponse;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.ServerException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticBookService {

    private final ElasticsearchClient elasticsearchClient;
    private static final String INDEX_NAME = "books";

    public void save(Book book) {
        ElasticBookDocument elasticBookDocument = ElasticBookDocument.of(book);
        try {
            IndexResponse indexResponse = elasticsearchClient.index(IndexRequest
                    .of(i -> i
                            .index(INDEX_NAME)
                            .id(String.valueOf(elasticBookDocument.getBookId()))
                            .document(elasticBookDocument)
                    ));
            log.info("elastic 색인 성공 {}", indexResponse.id());
        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }

    public void reindexBulkInsert(List<Book> books) {
        Builder builder = new Builder();

        for (Book book : books) {
            ElasticBookDocument document = ElasticBookDocument.of(book);
            builder.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(book.getId().toString())
                    .document(document)
                )
            );
        }
		BulkResponse bulkResponse;
		try {
			bulkResponse = elasticsearchClient.bulk(builder.build());
		} catch (IOException e) {
            throw new ServerException(ErrorMessage.REINDEXING_IO_ERROR);
		}

		if (bulkResponse.errors()) {
            bulkResponse.items().stream()
                .filter(item -> item.error() != null)
                .forEach(item ->
                    log.error("book_id: {}, 색인 실패 이유: {}", item.id(), item.error().reason()));
        }
    }

    public Page<ElasticBookSearchResponse> search(Pageable pageable) {
        try {
            Query matchAll = Query.of(q -> q.matchAll(m -> m));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(matchAll)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()));

            SearchResponse<ElasticBookDocument> response =
                elasticsearchClient.search(searchRequest, ElasticBookDocument.class);

            List<ElasticBookSearchResponse> results = response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(ElasticBookSearchResponse::of)
                .toList();

            long totalHits = response.hits().total() != null
                ? response.hits().total().value() : 0L;

            return new PageImpl<>(results, pageable, totalHits);
        } catch (IOException e) {
            log.error("======색인 실패======", e);
            throw new ServerException(ErrorMessage.INDEX_FAILED_ERROR);
        }
    }

    public Page<ElasticBookSearchResponse> searchByKeyword(String keyword, Pageable pageable) {
        try {
            List<Query> mustQueries = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                mustQueries.add(Query.of(q -> q
                        .multiMatch(m -> m
                                .fields("title", "author", "publisher", "subject")
                                .query(keyword)
                        )
                ));
            }

            Query finalQuery = Query.of(q -> q
                    .bool(b -> b.must(mustQueries))
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(finalQuery)
                    .from((int) pageable.getOffset())
                    .size(pageable.getPageSize())
            );

            SearchResponse<ElasticBookDocument> elasticBookDocumentSearchResponse =
                    elasticsearchClient.search(searchRequest, ElasticBookDocument.class);

            List<ElasticBookSearchResponse> results = elasticBookDocumentSearchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                .map(ElasticBookSearchResponse::of)
                    .toList();

            long totalHits = elasticBookDocumentSearchResponse.hits().total() != null ? elasticBookDocumentSearchResponse.hits().total().value() : 0L;

            return new PageImpl<>(results, pageable, totalHits);

        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }

    /**
     * 자동 완성 기능 v1
     */
    public List<String> searchAutoCompleteTitle(String keyword, int size) {
        try {
            Query autoCompleteQuery = Query.of(q -> q
                    .matchPhrasePrefix(m -> m
                            .field("title").query(keyword)));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(autoCompleteQuery)
                    .size(size)
            );

            SearchResponse<ElasticBookDocument> elasticBookDocumentSearchResponse =
                    elasticsearchClient.search(searchRequest, ElasticBookDocument.class);

            return elasticBookDocumentSearchResponse
                    .hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(ElasticBookDocument::getTitle)
                    .distinct()
                    .toList();
        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }

    /**
     * 자동 완성 기능 v2
     */
    public List<String> searchAutoCompleteTitleV2(String keyword, int size) {
        try {
            Query prefixQuery = Query.of(q -> q
                    .prefix(p -> p
                            .field("title.keyword").value(keyword)));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(prefixQuery)
                    .size(size)
            );

            SearchResponse<ElasticBookDocument> elasticBookDocumentSearchResponse = elasticsearchClient.search(searchRequest, ElasticBookDocument.class);
            return elasticBookDocumentSearchResponse
                    .hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(ElasticBookDocument::getTitle)
                    .toList();
        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }

    /**
     * 자동 완성 기능 V3 - 가중치 부여
     */
    public List<String> searchAutoCompleteTitleV3(String keyword, int size) {
        try {
            Query query = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(keyword)
                            .fields("title^3", "subject^2", "author")
                            .fuzziness("AUTO")));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(query)
                    .size(size));

            SearchResponse<ElasticBookDocument> elasticBookDocumentSearchResponse = elasticsearchClient.search(searchRequest, ElasticBookDocument.class);

            return elasticBookDocumentSearchResponse
                    .hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(ElasticBookDocument::getTitle)
                    .toList();
        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }

    /**
     * 연관 검색어 기능 V1 - terms aggregation
     */
    public List<String> searchRelateKeywords(String keyword) {
        try{
            Query query = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(keyword)
                            .fields("title", "subject", "author", "publisher")));

            Aggregation aggregation = Aggregation.of(a -> a
                    .terms(t -> t
                            .field("all_fields.keyword")
                            .size(10)));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(query)
                    .aggregations("relate_keywords", aggregation)
                    .size(0));

            SearchResponse<ElasticBookDocument> elasticBookDocumentSearchResponse = elasticsearchClient.search(searchRequest, ElasticBookDocument.class);

            List<String> results = new ArrayList<>();
            elasticBookDocumentSearchResponse.aggregations().get("relate_keywords").sterms().buckets().array().forEach(bucket -> results.add(bucket.key().stringValue()));

            return results;
        } catch (IOException e) {
            log.error(ErrorMessage.ELASTICSEARCH_ERROR.getMessage());
            throw new ServerException(ErrorMessage.ELASTICSEARCH_ERROR);
        }
    }
}