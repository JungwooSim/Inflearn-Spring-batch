# 5. 스프링 배치 청크 프로세스 이해
## 1. Chunk

- 기본개념
    - Chunk 란 여러 개의 아이템을 묶은 하나의 덩어리, 블록을 의미
    - 한번에 하나씩 아이템을 입력 받아 Chunk 단위의 덩어리로 만든 후 Chunk 단위로 트랜잭션을 처리한다.
      (즉 Chunk 단위의 Commit 과 Rollback 이 이루어진다)
    - 일반적으로 대용량 데이터를 한번에 처리하는 것이 아닌 청크 단위로 쪼개어서 더 이상 처리할 데이터가 없을 때까지 반복해서 입출력하는데 사용된다
    - Chunk<input> vs Chunk<output>
        - Chunk<input> 는 ItemReader 로 읽은 하나의 아이템을 Chunk 에서 정한 개수만큼 반복해서 저장하는 타입이다
        - Chunk<output> 는 ItemReader 로부터 전달받은 Chunk<input> 를 참조해서 ItemProcessor 에서 적절하게 가공, 필터링한 다음 ItemWriter 에 전달하는 타입이다

<img src="/img/6.png" width="500px;">

## 2. ChunkOrientedTasklet

- 기본개념
    - ChunkOrientedTasklet 은 스프링 배치에서 제공하는 Tasklet 의 구현체로서 Chunk 지향 프로세싱를 담당하는 도메인 객체
    - ItemReader, ItemWriter, ItemProcessor 를 사용해 Chunk 기반의 데이터 입출력 처리를 담당한다
    - TaskletStep 에 의해서 반복적으로 실행되며 ChunkOrientedTasklet 이 실행될 때마다 매번 새로운 트랜잭션이 생성되어 처리가 이루어진다
    - exception 이 발생할 경우, 해당 Chunk 는 롤백되며 이전에 커밋한 Chunk 는 완료된 상태가 유지된다
    - 내부적으로 ItemReader 를 핸들링하는 ChunkProvider 와 ItemProcessor, ItemWriter 를 핸들링하는 ChunkProcessor 타입의 구현체를 가진다


<img src="/img/7.png" width="500px;">

```
public Step chunkStep() {
	return stepBuilderFactory.get("chunkStep")
		.<I, O>chunk(10) // chunk size 설정. commit interval 을 의미. input, ouput 제네릭 타입 설정
		.<I, O>chunk(CompletionPolicy) // Chunk 프로세스를 완료하기 위한 정책 설정 클래스 지정
		.reader(itemReader()) // 소스로부터 item 을 읽거나 가져오는 ItemReader 구현체 설정
		.writer(itemWriter()) // item 을 목적지에 쓰거나 보내기 위한 ItemWriter 구현체 설정
		.processor(itemProcessor()) // item 을 변형, 가공, 필터링 하기 위한 ItemProcessor 구현체 설정
		.stream(itemStream()) // 재시작 데이터를 관리하는 롤백에 대한 스트림 등록
		.readerlsTransactionalQueue() // Item 이 JMS, Message Queue Server 와 같은 트랜잭션 외부에서 읽혀지고 캐시할것인지 여부, 기본값은 false
		.listener(ChunkListener) // Chunk 프로세스가 진행되는 특정시점에 콜백 제공받도록 ChunkListener 설정
		.build();
}
```
