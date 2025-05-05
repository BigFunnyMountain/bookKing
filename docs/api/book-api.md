### Book API
| 기능 | method | URL | requestHeader | requestBody | requestParam | responseBody | responseStatus |
|:----:|:------:|:---:|:-------------:|:-----------:|:------------:|:------------:|:--------------:|
| <nobr>OpenAPI 책 검색</nobr> | POST | /api/v1/books/search | - | SearchBookRequestDto | - | SearchBookResponseDto | 200 OK<br>500 SERVER_ERROR |
| <nobr>OpenAPI 책 DB에 추가</nobr> | POST | /api/v1/books/import | - | - | pageSize, totalPage | - | 200 OK<br>500 SERVER_ERROR |
| <nobr>새로운 책 등록</nobr> | POST | /api/v1/books | Authorization(ADMIN) | AddBookRequestDto | - | 1 | 200 OK<br>400 BAD_REQUEST |
| <nobr>책 내용 수정</nobr> | PATCH | /api/v1/books/{bookId} | Authorization(ADMIN) | UpdateBookRequestDto | - | - | 200 OK<br>404 NOT_FOUND |
| <nobr>책 수량 수정</nobr> | PATCH | /api/v1/books/{bookId}/stock | Authorization(ADMIN) | UpdateBookStockRequestDto | - | - | 200 OK<br>404 NOT_FOUND |
| <nobr>책 삭제</nobr> | DELETE | /api/v1/books/{bookId} | Authorization(ADMIN) | - | - | - | 200 OK<br>404 NOT_FOUND |
| <nobr>책 목록 가져오기</nobr> | GET | /api/v1/books | - | - | - | BookResponseDto | 200 OK |
| <nobr>책<br>가져오기</nobr> | GET | /api/v1/books/{bookId} | - | - | - | Page&lt;BookResponseDto&gt; | 200 OK |
