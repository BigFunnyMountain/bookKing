#  ElasticSearch 서비스 단위 테스트

## 테스트 대상 상세

- ElasticsearchService의 도서 검색, 자동완성, 연관 키워드 기능

## 테스트 목적

- Elasticsearch 기반 검색 기능이 정확하게 작동하는지 확인하고,<br>
 검색 결과의 정확성과 성능을 검증

## 가정 및 필요 조건
- Elasticsearch 인덱스에 도서 데이터가 색인되어 있음
- 검색 쿼리는 사전에 정의된 DSL 형식에 따라 작성

## 테스트 시나리오 목록

| ID  | 테스트 이름                      | 전제조건                                                       | 테스트 단계 요약                                                                 | 입력값                                                                                                     | 기댓값                                                               | 성공 여부 |
|-----|----------------------------------|------------------------------------------------------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------|-----------|
| 1   | 전체 책 목록 검색 성공           | Elasticsearch에 색인된 책 데이터가 존재함                        | 1. `search()` 호출<br>2. `matchAll` 쿼리 실행<br>3. 결과 페이지 생성            | `elasticBookService.search(PageRequest.of(0, 10))`                                                          | `Page<ElasticBookSearchResponse>` 반환                               | ✅         |
| 2   | 키워드 기반 검색 성공           | 특정 키워드와 일치하는 책이 색인되어 있음                         | 1. `searchByKeyword()` 호출<br>2. `multiMatch` 쿼리 실행<br>3. 로그 저장        | `elasticBookService.searchByKeyword(1L, "자바", PageRequest.of(0, 10))`                                    | `Page<ElasticBookSearchResponse>` 반환<br>검색 로그 저장              | ✅         |
| 3   | 키워드 기반 검색 - 키워드 없음  | 키워드 파라미터가 공백 또는 null                                 | 1. `searchByKeyword()` 호출<br>2. 빈 must 쿼리 실행                            | `elasticBookService.searchByKeyword(1L, "", PageRequest.of(0, 10))`                                        | 전체 결과 또는 빈 페이지 반환                                       | ✅         |
| 4   | 자동완성 V1 성공                | `title` 필드에 `matchPhrasePrefix` 조건으로 검색 가능            | 1. `searchAutoCompleteTitle()` 호출<br>2. `matchPhrasePrefix` 실행             | `elasticBookService.searchAutoCompleteTitle("자", 10)`                                                    | 책 제목 리스트 반환<br>중복 제거                                     | ✅         |
| 5   | 자동완성 V2 성공 (prefix 기반)   | `title.keyword`에 `prefix` 조건으로 검색 가능                    | 1. `searchAutoCompleteTitleV2()` 호출<br>2. `prefix` 쿼리 실행                 | `elasticBookService.searchAutoCompleteTitleV2("자", 5)`                                                   | 책 제목 리스트 반환                                                  | ✅         |
| 6   | 자동완성 V3 성공 (가중치 + fuzzy)| 키워드에 대해 `multiMatch` + `boost` + `fuzziness` 적용           | 1. `searchAutoCompleteTitleV3()` 호출<br>2. 복합 쿼리 실행                     | `elasticBookService.searchAutoCompleteTitleV3("자바", 5)`                                                  | 책 제목 리스트 반환                                                  | ✅         |
| 7   | 연관 키워드 검색 성공            | aggregation 가능한 문서가 색인되어 있음                          | 1. `searchRelateKeywords()` 호출<br>2. `multiMatch` + aggregation 실행         | `elasticBookService.searchRelateKeywords("자바")`                                                          | 연관 키워드 리스트 반환                                              | ✅         |
| 8   | 색인 재등록 성공                 | 책 데이터가 DB에 존재하고 Elasticsearch 연결됨                   | 1. `reindexBooks()` 호출<br>2. `bookService.reindexBooks()` 실행               | `bookService.reindexBooks(pageSize=500, startPage=0, endPage=100)`                                        | 색인 재등록 성공<br>BulkResponse 처리 완료                          | ✅         |
