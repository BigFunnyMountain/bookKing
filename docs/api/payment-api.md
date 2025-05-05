### Payment API
| 기능 | method | URL | requestHeader | requestBody | requestParam | responseBody | responseStatus |
|:----:|:------:|:---:|:-------------:|:-----------:|:------------:|:------------:|:--------------:|
| <nobr>결제</nobr> | POST | /api/v1/payments | Authorization(JWT) | PaymentBuyRequest | - | 결제 | 400 BAD_REQUEST, 200 OK |
| <nobr>결제 취소</nobr> | POST | /v1/payment/{orderId} | Authorization(JWT) | - | - | PaymentReturnResponse | 400 BAD_REQUEST, 200 OK |
