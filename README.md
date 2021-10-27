# Spring-batch-elk

### 목표
- Spring-batch를 이용하여 로그를 출력하는 application 생성
- Docker를 이용한 ELK 기동
- Logstash를 이용하여 해당 로그를 Elasticsearch에 전송하고, Kibana를 통해 이를 확인

### Spring-batch
- Spring Batch, Lombok, H2
- Application class에 @EnableBatchProcessing 추가
    ```
    @EnableBatchProcessing
    ```
- BatchConfig.java 파일 생성 후, log 100번 출력
- tcp 통신을 통하여 바로 logstash 로 전송할 수 있는 appender 추가
    ```
    compile 'net.logstash.logback:logstash-logback-encoder:6.3'
    ```
- logback.xml 파일 생성
    ```
    <configuration>
        <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                <pattern>%-5level %d [%thread] %logger - %msg%n</pattern>
            </encoder>
        </appender>
    
        <appender name="stash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
            <destination>127.0.0.1:5000</destination>
    
            <!-- encoder is required -->
            <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
        </appender>
    
        <root level="INFO">
            <appender-ref ref="console"/>
            <appender-ref ref="stash"/>
        </root>
    </configuration>
    ```
- application.yml
    ```
    logging.config: classpath:logback.xml
    ```

### Docker ELK 설정
- Docker ELK git URL
```
git clone https://github.com/deviantony/docker-elk.git
```

- docker-elk/elasticsearch/config/elasticsearch.yml 수정
```
xpack.security.enabled: false
xpack.monitoring.collection.enabled: false
```

- docker-elk/logstash/pipeline/logstash.conf 수정
```
# tcp port 5000 open
input {
	tcp {
		port => 5000
		codec => json_lines
	}
}

# 아래 설정한 index가 elasticsearch에 생성됨
output {
	elasticsearch {
		hosts => "elasticsearch:9200"
		index => "logstash-20211027"
		user => "elastic"
		password => "changeme"
	}
}
```

- 새로운 이미지 build하고 컨테이너 재실행
```
docker-compose build
docker volume rm docker-elk_elasticsearch
docker-compose up
```
***

### Kibana에서 index 생성

- spring-batch-elk application 구동
- kibana 접속 > Index Pattern > Create Index Pattern
    - logstash.conf에 설정한 index 추가
- kibana > discovery에서 확인
```
# Create an index pattern via the Kibana API:
$ curl -XPOST -D- 'http://localhost:5601/api/saved_objects/index-pattern' \
    -H 'Content-Type: application/json' \
    -H 'kbn-version: 7.15.1' \
    -u elastic:changeme \
    -d '{"attributes":{"title":"logstash-leetsh","timeFieldName":"@timestamp"}}'
```

***
### Ref

https://github.com/deviantony/docker-elk.git
https://techblog.woowahan.com/2659/
https://investment-engineer.tistory.com/m/4
