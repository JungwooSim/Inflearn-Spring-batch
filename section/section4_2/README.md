# 4-2. 스프링 배치 실행 - Step
### 1. StepBuilderFactory / StepBuilder

`StepBuilderFactory`
- StepBuilder 를 생성하는 팩토리 클래스로서 get(String name) 메서드 제공
- StepBuilderFactory.get(”stepName”)
    - ”stepName” 으로 Step 생성

`StepBuilder`
- Step 을 구성하는 설정 조건에 따라 다섯 개의 하위 빌더 클래스를 생성하고 실제 step 생성을 위임
- TaskletStepBuilder
    - TaskletStep 을 생성하는 기본 빌더 클래스
- SimpleStepBuilder
    - TaskletStep 을 생성하며 내부적으로 청크기반의 작업을 처리하는 ChunkOrientedTasklet 클래스를 생성
- PartitionStepBuilder
    - PartitionStep 을 생성하며 멀티 스레드 방식으로 Job 을 실행
- JobStepBuilder
    - JobStep 을 생성하여 Step 안에서 Job 을 실행
- FlowStepBuilder
    - FlowStep 을 생성하여 Step 안에서 Flow 를 실행

### 2. TaskletStep - 개념 및 API 소개

- 기본개념
    - 스프링 배치에서 제공하는 Step 의 구현체로서 Tasklet 을 실행시키는 도메인 객체
    - RepeatTemplate 를 사용해서 Tasklet 의 구문을 트랜잭션 경계 내에서 반복해서 실행한다
    - Taks 기반과 Chunk 기반으로 나누어서 Tasklet 을 실행한다
- Task vs. Chunk
    - 스프링 배치에서 Step 의 실행 단위는 크게 2가지로 나누어진다
        - chunk 기반
            - 하나의 큰 덩어리를 n 개씩 나누어 실행한다는 의미로 대량 처리를 하는 경우 효과적으로 설계된다
            - ItemReader, ItemProcessor, ItemWriter 를 사용하며 청크 기반 전용 Tasklet 인 ChunkOrientedTasklet 구현체가 제공된다
        - Task 기반
            - ItemReader 와 ItemWriter 와 같은 청크 기반의 작업보다 단일 작업 기반으로 처리되는 것이 더 효율적인 경우에 사용한다
            - 주로 Tasklet 구현체를 만들어 사용
            - 대량 처리를 하는 경우 chunk 기반에 비해 더 복잡한 구현이 필요하다

```
@Bean
  public Step batchStep() {
      return stepBuilderFactory.get("batchStep")
              .tasklet(Tasklet) // Tasklet 클래스 설정, 이 메서드를 실행하면 TaskletStepBuilder 변환
              .startLimit(10) // Step 의 실행 횟수를 결정, 설정한 만큼 실행되며 초과시 오류 발생. 기본값은 Integer.MAX_VALUE
              .allowStartIfComplete(true) // Step 의 성공, 실패와 상관없이 항상 Step 을 실행하기 위한 설정
              .listener(StepExecutionListener) // Step 라이플 사이클의 특정 시점에 콜백 제공받도록 StepExecutionListener 설정
              .build();
  }
```
