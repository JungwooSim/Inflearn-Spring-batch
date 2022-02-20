# 5-3. 스프링 배치 청크 프로세스 활용 - ItemWriter
## 1. Flat Files - FlatFileItemWriter

- 개념
    - 2차원 데이터로(표)로 표현된 유형의 파일을 처리하는 ItemWriter
    - 고정 위치로 정의된 데이터 필드나 특스 문자에 의해 구별된 데이터의 행을 기록한다
    - Resource 와 LineAggregator 두 가지 요소가 필요하다

```
public FlatFileItemReader itemReader() {
	return new FlatFileItemWriterBuilder<T> () {
		.name()
		.resource() // 쓰기할 리소스 설정
		.lineAggregator() // 객체를 String 으로 변환하는 LineAggregator 객체 설정
		.append() // 존재하는 파일에 내용을 추가할 것인지 여루 설정
		.fieldExtracotr() // 객체 필드를 추출해서 배열로 만드는 FieldExtrator 설정
		.headerCallback() // 헤더를 파일에 쓰기 위한 콜백 인터페이스
		.footerCallback() // 푸터를 파일에 쓰기 위한 콜백 인터페이스
		.shouldDeleteIfExists() // 파일이 이미 존재한다면 삭제
		.shouldDeleteIfEmpty() // 파일의 내용이 비어 있다면 삭제
		.delimited().delimiter() // 파일의 구분자를 기준으로 파일을 작성하도록 설정
		.formatted().format() // 파일의 고정길이를 기준으로 파일을 작성하도록 설정
		.build();
	}
}
```

### LineAggregator
- Item 을 받아서 String 으로 변환하여 리턴
- FieldExtractor 를 사용해서 처리할 수 있다
- 구현체
    - PassThroughLineAggregator, DelimitedLineAggregator, FormatterLineAggregator

### FieldExtractor
- 전달 받은 Item 객체의 필드를 배열로 만들고 배열을 합쳐서 문자열을 만들도록 구현하도록 제공하는 인터페이스
- 구현체
    - BeanWrapperFieldExtractor, PassThroughFieldExtractor

### DelimitedLineAggregator
- 객체의 필드 사이에 구분자를 삽입해서 한 문자열로 변환

### FormatterLineAggregator
- 객체의 필드를 사용자가 설정한 Formatter 구문을 통해 문자열로 변환

## 2. XML - StaxEventItemWriter

- 개념
  - XML 쓰는 과정은 읽기 과정에 대칭적이다
  - StaxEventItemWriter 는 Resource, marshaller, rootTagName 가 필요하다

```
public StaxEventItemReader itemReader() {
	return new StaxEventItemWriterBuilder<T>()
	.name()
	.resource() // 쓰기할 리소스 설정
	.rootTagName() // 조각단위의 루트가 될 이름 설정
	.overwriteOutput() // 파일이 존재하면 덮어 쓸 것인지 설정
	.marshaller() // Marshaller 객체 설정
	.headerCallback() // 헤더를 파일에 쓰기위한 콜백 인터페이스
	.footerCallback() // 푸터를 파일에 쓰기위한 콜백 인터페이스
	.build();
}
```
## 3. Json - JsonFileItemWriter
- 객체를 받아 JSON String 으로 변환하는 역할

```
public JsonFileWriterBuilder itemReader() {
	return JsonFileItemWriterBuilder<T>()
		.name()
		.resource() // 쓰기할 리소스 설정
		.append() // 존재하는 파일에 내용을 추가할 것인지 여부 설정
		.jsonObjectMarshaller() // JsonObjectMarshaller 객체 설정
		.headerCallback() // 헤더를 파일에 쓰기위한 콜백 인터페이스
		.footerCallback() // 푸터를 파일에 쓰기위한 콜백 인터페이스
		.shouldDeleteIfExists() // 파일이 이미 존재한다면 삭제
		.shouldDeleteIfEmpty() // 파일의 내용이 비어있다면 삭제
		.build();
}
```
