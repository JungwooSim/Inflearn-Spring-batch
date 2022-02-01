# 4-2. 스프링 배치 실행 - Step
## 1. StepBuilderFactory / StepBuilder

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

## 2. TaskletStep - 개념 및 API 소개

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

### API 설정

`tasklet()`
- Tasklet 타입의 클래스를 설정한다
    - Tasklet
        - Step 내에서 구성되고 실행되는 도메인 객체로서 주로 단일 task 를 수행하기 위한 것
        - TaskletStep 에 의해 반복적으로 수행되며 반환값에 따라 계속 수행 하거나 종료한다
        - RepeatStatus - Tasklet 의 반복 여부 상태 값
            - RepeatStatus.FINISHED - Tasklet 종료, RepeatStatus 을 null 로 반환하면 RepeatStatus.FINISHED 로 해석된다
            - RepeatStatus.CONTINUABLE - Tasklet 반복
            - RepeatStatus.FINISHED 가 리턴되거나 실패 예외가 던져지기 전까지 TaskletStep 에 의해 while 문 안에서 반복적으로 호출된다 (무한 루프 조심)
    - 익명 클래스 혹은 구현 클래스를 만들어서 사용한다
    - 이 메소드를 실행하게 되면 TaskletStepBuilder 가 반환되어 관련 API 를 설정할 수 있다.
    - Step 에 오직 하나의 Tasklet 설정이 가능하며 두개 이상의 설정 했을 경우 마지막에 설정한 객체가 실행된다


`startLimit()`
- 기본개념
    - Step 의 실행 횟수를 조정할 수 있다
    - Step 마다 실행할 수 있다
    - 설정 값을 초과해서 다시 실행하려고 하면 startLimitExceededException 이 발생한다
    - start-limit 의 default 값은 Integer.MAX_VALUE 이다

`allowStartIfComplate()`
- 기본개념
    - 재시작 가능한 job 에서 Step 의 이전 성공 여부와 상관없이 항상 step 을 실행하기 위한 설정
    - 실행마다 유효성을 검증하는 Step 이나 사전 작업이 꼭 필요한 Step 등
    - 기본적으로 COMPLETED 상태를 가진 Step 은 Job 재시작시 실행하지 않고 스킵한다
    - allow-start-if-complete 가 “true” 로 설정된 step 은 항상 실행한다.

<img src="/img/4.png" width="500px;">

## 3. JobStep

- 기본개념
    - Job 에 속하는 Step 중 외부의 Job 을 포함하고 있는 Step
    - 외부의 Job 이 실패하면 해당 Step 이 실패하므로 결국 최종 기본 Job 도 실패한다
    - 모든 메타데이터는 기본 Job 과 외부 Job 별로 각각 저장된다
    - 커다란 시스템을 작은 모듈로 쪼개고 job 의 흐름을 관리하고자 할 때 사용할 수 있다
