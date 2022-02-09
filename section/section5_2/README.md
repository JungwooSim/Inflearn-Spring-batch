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
