# 테스트 환경 실행

<h4>실행 환경 준비</h4>

.local.env 을 만들어준다.
```
DB_PASSWORD="DB_PASSWORD"
LOGSTASH_SERVER="logstash-container:5044"
SPRING_PROFILE=dev

LIBRARY_API_KEY="YOUR_API_KEY"

AWS_ACCESS_KEY="AWS_IAM_ACCESS_KEY"
AWS_SECRET_KEY="AWS_IAM_PRIVATE_KEY"
```
LOGSTAH_SERVER,SPRING_PROFILE은 고정값이다.

추가적으로 값을 넣고 싶다면 application-{이름}.yml 을 만들고 
SPRING_PROFILE={이름} 을 넣으면 된다.

<h4>실행 방법</h4>

```
cd {project_directory}
docker-compose -f docker-compose.local.yml --env-file .local.env config
docker-compose -f docker-compose.local.yml build
docker-compose up -d
```

<h4>재 빌드</h4>

```
docker-compose down
docker-compose -f docker-compose.local.yml build
docker-compose up -d
```

