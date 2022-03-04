# 5-2. 스프링 배치 청크 프로세스 활용 - ItemReader
### 1. Flat Files

### FlatFileItemReader

- 기본개념
    - 2차원 데이터(표)로 표현된 유형의 파일을 처리하는 ItemReader
    - 일반적으로 고정 위치로 정의된 데이터 필드나 특수문자에 의해 구별된 데이터의 행을 읽는다
    - Resource 와 LineMapper 두 가지 요소가 필요하다

```
String encoding = DEFAULT_CHARSET // 문자열 인코딩. 디폴트는 Charset.defaultCharset()
int linesToSkip // 파일 상단에 있는 무시할 라인수
String[] comments // 해당 코멘트 기호가 있는 라인은 무시
Resource resouce // 읽어야할 리소스
LineMapper<T> lineMapper // String 을 Object 로 변환
LineCallbackHandler skippedLinesCallback // 건너뛸 라인의 원래 내용을 전달하는 인터페이스

```

- Resource
    - FileSystemResource - new FileSystemResource(”resource/path/config.xml”)
    - ClassPathResource - new ClassPathResource(”classpath:path/config.xml”)
- LineMapper
    - 파일의 라인 한줄을 Object 로 변환해서 FlatFileItemReader 로 리턴한다
    - 단순히 문자열을 받기 때문에 문자열을 토큰화해서 객체로 매핑하는 과정이 필요하다
    - LineTokenizer 와 FieldSetMapper 를 사용해서 처리한다
        - FieldSet
            - 라인을 필드로 구분해서 만든 배열 토큰을 전달하면 토큰 필드를 참조할 수 있도록 한다
            - JDBC 의 ResultSet 과 유사하다
        - LineTokenizer
            - 입력받은 라인을 FieldSet 으로 변환해서 리턴한다
            - 파일마다 형식이 다르기 때문에 문자열을 FieldSet 으로 변환하는 작업을 추상화시켜야 한다
        - FieldSetMapper
            - FieldSet 객체를 받아서 원하는 객체로 매핑해서 리턴한다
            - JdbcTemplate 의 RowMapper 와 동일한 패턴을 사용한다

```
public FlatFileItemReader itemReader() {
	return new FlatFileItemReaderBuilder<T>()
	.name(String name) // 이름 설정. ExecutionContext 내에서 구분하기 위한 key 저장
	.resoruce(Resource) // 읽어야할 리소스 설정 
	.delimited().delimiter("|") // 파일의 구분자를 기준으로 파일을 읽어들이는 설정
	.fixedLength() // 파일의 고정길이를 기준으로 파일을 읽어들이는 설정
	.addColumns(Range..) // 고정길이 범위를 정하는 설정
	.names(String[] fieldNames) //  LineTokenizer 로 구분된 라인의 항목을 객체의 필드명과 매핑하도록 설정
	.targetType(Class class) // 라인의 각 항목과 매핑할 객체 타입 설정
	.addComment(String Comment) // 무시할 라인의 코멘트 기호 설정
	.strict(boolean) // 라인을 읽거나 토큰화할 때 Parsing 예외가 발생하지 않도록 검증 생략하도록 설정
	.encoding(String encoding) // 파일 인코딩 설정
	.linesToSkip(int linesToSkip) // 파일 상단에 있는 무시할 라인 수 설정
	.saveState(boolean) // 상태정보를 저장할 것인지 설정
	.setLineMapper(LineMapper) // LineMapper 객체 설정
	.setFieldSetMapper(FieldSetMapper) // FieldSetMapper 객체 설정
	.setLineTokenizer(LineTokenizer) // LineTokenizer 객체 설정
	.build();
}
```
### DelimitedLineTokenizer

- 기본개념
    - 한 개 라인의 String 을 구분자 기준으로 나누어 토큰화 하는 방식


### FixedLengthTokenizer

- 기본개념
    - 한 개 라인의 String 을 사용자가 설정한 고정길이 기준으로 나누어 토큰화 하는 방식
    - 범위는 문자열 형식으로 설정할 수 있다
        - “1-4” 또는 “1-3, 4-6, 7” 또는 “1-2, 4-5, 7-10”
        - 마지막 범위가 열려있으면 나머지 행이 해당 열로 읽혀진다

### Exception Handling

- 라인을 읽거나 토큰화할 때 발생하는 Parsing 예외를 처리할 수 있도록 예외 계층 제공
- 토큰화 검증을 엄격하게 적용하지 않도록 설정하면 Parsing 예외가 발생하지 않도록 할 수 있다

## 2. XML StaxEventItemReader
- 기본개념
    - Stax API 방식으로 데이터를 읽어들이는 ItemReader

- JAVA 에서 제공하는 XML API
    - DOM 방식
        - 문서 전체를 메모리에 로드한 후 Tree 형태로 만들어서 데이터를 처리하는 방식. (pull 방식)
        - 엘리먼트 제어는 유연하나 문서크기가 클 경우 메모리 사용이 많고 속도가 느리다
    - SAX 방식
        - 문서의 항목을 읽을때 마다 이벤트가 발생하여 데이터를 처리하는 push 방식
        - 메모리 비용이 적고 속도가 빠른 장점은 있으나 엘리먼트 제어가 어렵다
    - StAX 방식(Streaming API for XML)
        - DOM 과 SAX 의 장점과 단점을 보완한 API 모델로서 push 와 pull 을 동시에 제공
        - XML 문서를 읽고 쓸 수 있는 양방향 파서기 지원
        - XML 파일의 항목에서 항목으로 직접 이동하면서 Stxa 파서기를 통해 구문 분석
        - 유형
            - Iterator API 방식
                - XMLEventReader 의 nextEvent() 를 호출해서 이벤트 객체를 가지고 온다
                - 이벤트 객체는 XML 태그 유형(요소, 텍스트, 주석 등)에 대한 정보를 제공한다
            - Cursor API 방식
                - JDBC Resultset 처럼 작동하는 API 로서 XMLStreamReader 는 XML 문서의 다음 요소로 커서를 이동한다
                - 커서에서 직접 메서드를 호출하여 현재 이벤트에 대한 자세한 정보를 얻는다
- Spring-OXM
    - 스프링의 Object XML Mapping 기술로 XML 바인딩 기술을 추상화 한다
        - Marshaller
        - Unmarchaller
        - Marshaller 와 Unmarshaller 바인딩 기능을 제공하는 오픈소스로 JaxB2, Castor, XmlBeans, Xstream 등이 있다
    - 스프링 배치는 특정한 XML 바인딩 기술을 강요하지 않고 Spring OXM 에 위임한다
        - 바인딩 기술을 제공하는 구현체를 선택해서 처리하도록 한다
- Spring Batch XML
    - 스프링 배치에서는 StAX 방식으로 XML 문서를 처리하는 StaxEventItemReader 를 제공한다
    - XML 을 읽어 자바 객체로 매핑하고 자바 객체를 XML 로 쓸 수 있는 트랜잭션 구조를 지원한다

## 3. JsonItemReader

- 기본개념
    - Json 데이터의 Parsing 과 Binding 을 JsonObjectReader 인터페이스 구현체에 위임하여 처리하는 ItemReader
    - 두 가지 구현체 제공
        - JacksonJsonObjectReader
        - GsonJsonObjectReader

## 4. Database

### Cursor  & Paging

- 기본개념
    - 배치 어플리케이션은 실시간적 처리가 어려운 대용량 데이터를 다루며 이 때 DB I/O 의 성능문제와 메모리 자원의 효율성 문제를 해결할 수 있여야 한다
    - 스프링 배치에서는 대용량 데이터 처리를 위한 두가지 해결방안을 제시하고 있다 (Cusor Based, Paging Based)


Cursor Based

- JDBC ResultSet 의 기본 메커니즘 사용
- 현재 행에 커서를 유지하며 다음 데이터를 호출하면 다음 행으로 커서를 이동하며 데이터 반환이 이루어지는 Streaming 방식의 I/O 이다
- ResultSet 이 open 될 때마다 next() 메소드가 호출되어 Database 의 데이터가 반환되고 객체와 매핑이 이루어진다
- DB Connection 이 연결되면 배치 처리가 완료될 때까지 데이터를 읽어오기 때문에 DB 와 SoketTimeout 을 충분히 큰값으로 설정 필요하다
- 모든 결과를 메모리에 할당하기 때문에 메모리 사용량이 많아지는 단점이 있다
- Connection 연결 유지 시간과 메모리 공간이 충분하다면 대량의 데이터 처리에 적합할 수 있다 (fetchSize 조절)

Paging Based

- 페이징 단위로 데이터를 조회하는 방식으로 Page Size 만큼 한번에 메모리로 가지고 온 다음 한 개씩 읽는다
- 한 페이지를 읽을때마다 Connection 을 맺고 끊기 때문에 대량의 데이터를 처리하더라도 SoketTimeout 예외가 거의 일어나지 않는다
- 시작 행 번호를 지정하고 페이지에 반환시키고자 하는 행의 수를 지정한 후 사용 - Offset, Limit
- 페이징 단위의 결과만 메모리에 할당하기 때문에 메모리 사용량이 적어지는 장점이 있다
- Connection 연결 유지 시간이 길지 않고 메모리 공간을 효율적으로 사용해야 하는 데이터 처리에 적합할 수 있다

<img src="/img/14.png" width="1000px;">

### JdbcCursorItemReader

- 기본개념
    - Cursor 기반의 JDBC 구현체로서 ResultSet 과 함께 사용되며 Datasource 에서 Connection 을 얻어와서 SQL 을 실행한다
    - Thread 안정성을 보장하지 않기 때문에 멀티 스레드 환경에서 사용할 경우 동시성 이슈가 발생하지 않도록 별도 동기화 처리가 필요하다

```
public JdbcCursorItemReader itemReader() {
	return new JdbcCursorItemReaderBuilder<T>()
		.name()
		.fetchSize() // Cursor 방식으로 데이터를 가지고 올 때 한번에 메모리에 할당할 크기를 설정
		.dataSoruce() // DB 에 접근하기 위해 Datasoruce 설정
		.rowMapper() // 쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper 설정
		.beanRowMapper() // 별도의 RowMapper 을 설정하지 않고 클래스 타입을 설정하면 자동으로 객체와 매핑
		.sql() // ItemReader 가 조회할 때 사용할 쿼리 문장 설정
		.queryArguments() // 쿼리 파라미터 설정
		.maxItemCount() // 조회할 최대 item 수
		.currentItemCount() // 조회 item 의 시작 지점
		.maxRows() // ResultSet 오브젝트가 포함할 수 있는 최대 행 수
		.build();
}
```

<img src="/img/15.png" width="1000px;">

### JpaCursorItemReader

- 기본개념
    - Spring Batch 4.3 버전부터 지원
    - Cursor 기반의 JPA 구현체로서 EntityManagerFactory 객체가 필요하며 쿼리는 JPQL 을 사용한다

```
public JpaCursorItemReader itemReader() {
	return new JpaCusorItemReaderBuilder<T>()
		.name()
		.queryString() // ItemReader 가 조회할 때 사용할 JPQL 문장 설정
		.EntityManagerFactory() // JPQL 을 실행하는 EntityManager 를 생성하는 팩토리
		.parameterValue() // 쿼리 파라미터 설정
		.maxItemCount() // 조회할 최대 item 수
		.currentItemCount() // 조회 item 의 시작 지점
		.build();
}
```

<img src="/img/16.png" width="1000px;">

### JdbcPagingItemReader

- 개념
    - Paging 기반의 JDBC 구현체로서 쿼리에 시작 행 번호(offset) 와 페이지에서 반환 할 행 수(limit)를 지정해서 SQL 을 실행한다
    - 스프링 배치에서 offset 과 limit 을 PageSize 에 맞게 자동으로 생성해주며 페이징 단위로 데이터를 조회할 때 마다 새로운 쿼리가 실행된다
    - 페이지마다 새로운 쿼리를 실행하기 때문에 페이징 시 결과 데이터의 순서가 보장될 수 있도록 order by 구문이 작성되도록 한다
    - 멀티 스레드 환경에서 Thread 안정성을 보장하기 때문에 별도의 동기화를 할 필요가 없다

PagingQueryProvider

- 쿼리 실행에 필요한 쿼리문을 ItemReader 에게 제공하는 클래스
- 데이터베이스마다 페이징 전략이 다르기 때문에 각 데이터 베이스 유형마다 다른 PagingQueryProvider 를 사용한다
- SELECT , FROM , sortKey 는 필수로 설정해야 하며 where, group by 절은 필수가 아니다

```
public JdbcPagingItemReader itemReader() {
	return new JdbcPagingItemReaderBuilder<T>()
	.name()
	.pageSize() //페이지 크기 설정(쿼리당 요청할 레코드 수)
	.dataSource() // DB 에 접근하기 위한 Datasource 설정
	.queryProvider() // DB 페이징 전략에 따른 PagingQueryProvider
	.rowMapper() // 쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper 설정
	.selectClause() // select 구문
	.fromClause() // from 구문
	.whereClause() // where 구문
	.groupClause() // group by 구문
	.sortKeys() // 정렬을 위한 유니크 키 설정
	.parameterValues() // 쿼리 파라미터 설정
	.maxItemCount() // 조회할 최대 item 수
	.currentItemCount() // 조회 Item 의 시작 지점
	.maxRows() // ResultSet 오브젝트가 포함할 수 있는 최대 행 수
	.build();
}
```

<img src="/img/17.png" width="1000px;">

### JpaPagingItemReader

- 개념
    - Paging 기반의 JPA 구현체로서 EntityManagerFactory 객체가 필요하며 쿼리는 JPQL 을 사용한다


```
public JpaPagingItemReader itemReader() {
	return new JpaPagingItemReaderBuilder<>()
	.name()
	.pageSize() //페이지 크기 설정(쿼리당 요청할 레코드 수)
	.queryString() // ItemReader 가 조회할 때 사용할 JPQL 문장 설정
	.EntityManagerFactory() // JPQL 을 실행하는 EntityManager 를 생성하는 팩토리
	.parameterValue() // 쿼리 파라미터 설정
	.build();
}
```

<img src="/img/18.png" width="1000px;">

### ItemReaderAdapter

- 개념
    - 배치 Job 안에서 이미 있는 DAO 나 다른 서비스를 ItemReader 안에서 사용하고자 할 때 위임 역할을 한다
