### Search API
| 기능 | method | URL | requestHeader | requestBody | requestParam | responseBody | responseStatus |
|:----:|:------:|:---:|:-------------:|:-----------:|:------------:|:------------:|:--------------:|
| <nobr>검색 | GET | /api/v1/elasticsearch | - | - | keyword | Page<br>&lt;ElasticBookSearchResponseDto&gt; | 200 OK<br>500 SERVER_ERROR |
| <nobr>자동완성</nobr> | GET | /api/v3/elasticsearch/autocomplete | - | - | keyword, size | List&lt;String&gt; | 200 OK<br>500 SERVER_ERROR |
| <nobr>연관 검색어</nobr> | GET | /api/v1/elasticsearch/relate | - | - | keyword | List&lt;String&gt; | 200 OK<br>500 SERVER_ERROR |
