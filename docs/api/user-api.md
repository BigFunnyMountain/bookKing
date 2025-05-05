### User API
| 기능             | method | URL                          | requestHeader           | requestBody           | requestParam | responseBody  | responseStatus         |
|:----------------:|:------:|:-----------------------------:|:------------------------:|:----------------------:|:------------:|:--------------:|:------------------------:|
| <nobr>회원&nbsp;조회</nobr>      | GET    | /api/v1/users/myInfo         | Authorization           | -                    | -            | UserResponse   | 200 OK<br>400 BAD_REQUEST |
| <nobr>회원&nbsp;수정</nobr>      | PATCH  | /api/v1/users/myInfo         | Authorization           | UpdateUserRequest     | -            | UserResponse   | 200 OK<br>400 BAD_REQUEST |
| <nobr>ADMIN&nbsp;권한&nbsp;부여</nobr> | PATCH  | /api/v1/users/{userId}/role  | Authorization           | UpdateUserRoleRequest | -            | UserResponse   | 200 OK<br>400 BAD_REQUEST |
| <nobr>회원&nbsp;탈퇴</nobr>      | DELETE | /api/v1/users/{userId}       | Authorization, password | -                    | -            | -              | 200 OK<br>400 BAD_REQUEST |
| <nobr>프로필<br>업로드</nobr>   | POST   | /v1/users/profile-image      | Authorization           | image                 | -            | -              | 200 OK<br>400 BAD_REQUEST |
