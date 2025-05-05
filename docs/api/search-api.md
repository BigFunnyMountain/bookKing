### Search API
| 기능 | method | URL | requestHeader | requestBody | requestParam | responseBody | responseStatus |
|------|--------|-----|---------------|-------------|--------------|--------------|----------------|
| 전체 책 검색 | GET | /api/v1/elasticsearch | - | - | page, size | Page<ElasticBookSearchResponse> | 200 OK |
| 키워드로 책 검색 | GET | /api/v1/elasticsearch/keyword | Authorization | - | keyword, page, size | Page<ElasticBookSearchResponse> | 200 OK |
| 책 재색인 | POST | /api/v1/elasticsearch/reindex | - | - | pageSize, startPage, endPage | - | 200 OK |
| 자동완성 v1 | GET | /api/v1/elasticsearch/autocomplete | - | - | keyword, size | List<String> | 200 OK |
| 자동완성 v2 | GET | /api/v2/elasticsearch/autocomplete | - | - | keyword, size | List<String> | 200 OK |
| 자동완성 v3 | GET | /api/v3/elasticsearch/autocomplete | - | - | keyword, size | List<String> | 200 OK |
| 연관 검색어 조회 | GET | /api/v1/elasticsearch/relate | - | - | keyword | List<String> | 200 OK |
