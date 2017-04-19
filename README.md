Original Project: https://github.com/mitre/HTTP-Proxy-Servlet

### Smiley's HTTP Proxy Servlet Custom Application

* 'Smiley's HTTP Proxy Servlet' 기반 서버 응용 프로그램

* 커스텀 내용
   - URI Prefix 화 매핑하는 다수 Host의 동적 분기 처리
   - 환경 설정 정보를 properties 파일(http-proxy.properties)에서 관리
   - Spring Boot 1.3.8.RELEASE 적용

### Execution

* Main Class: http-rest-proxy/src/main/java/soonhyuk/yoon/http/proxy/boot/Application.java

* Checkout 이후 로컬 실행

```shell
mvn spring-boot:run
```

* jar 생성 및 실행

```shell
mvn package
(실행)
java -jar http-rest-proxy-1.0.0.jar
```

### Configuration

* Path: http-rest-proxy/src/main/resources/property/http-proxy.properties

```properties
#
# HTTP Proxy Configuration
#
# 필독!!!
# 원래 'Smiley's HTTP Proxy Servlet' 설정은 web.xml 파일의 'init-param'에 입력하지만
# 관리의 편의를 위해 본 응용 프로그램은 properties 파일에 설정하고
# Servlet 로딩시 이 파일에 작성한 설정을 바라보도록 구현되어 있다.

#
# 신규 설정
#

# HTTP Proxy Target
# - 설정규칙: targetUri.TARGET_CODE
# - 'TARGET_CODE'는 사용자가 정하는 Host를 구분하는 코드 값이며 아래와 같이 Schema + Domain 으로 추가한다.
# - 아래는 기능 테스트를 위한 오픈 날씨 API 설정이다.

targetUri.WEATHER=http://samples.openweathermap.org


#
# Original 'Smiley's HTTP Proxy Servlet' Configuration
#

# HTTP Proxy Target Prefix (더미 값: 의미 없음)
targetUri=http://127.0.0.1:8080

# A boolean parameter name to enable logging of input and target URLs to the servlet log.
log=true

# A boolean parameter name to enable forwarding of the client IP
forwardip=false

# A boolean parameter name to keep HOST parameter as-is
preserveHost=false

# A boolean parameter name to keep COOKIES as-is
preserveCookies=false

# A boolean parameter name to have auto-handle redirects: defines whether redirects should be handled automatically
http.protocol.handle-redirects=false

# A integer parameter name to set the socket connection timeout (millis)
http.socket.timeout=10000
```