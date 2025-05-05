### Keyword API
| 기능 | method | URL | requestHeader | requestBody | requestParam | responseBody | responseStatus |
|:----:|:------:|:---:|:-------------:|:-----------:|:------------:|:------------:|:--------------:|
| <nobr>키워드 입력해서 받기</nobr> | POST | /api/v1/keywords/suggest | - | KeywordRequest | - | 결제 | 400 BAD_REQUEST, 200 OK |
| <nobr>구매목록 기반 키워드</nobr> | GET | /api/v1/keywords/recommendations | Authorization(JWT) | - | - | 결제 | 400 BAD_REQUEST, 200 OK |
