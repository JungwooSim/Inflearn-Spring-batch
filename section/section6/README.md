# 6. 스프링 배치 반복 및 오류 제어 
## 1. Repeat

- 개념
    - Spring Batch 는 얼마나 작업을 반복해야 하는지 알려줄 수 있는 기능을 제공한다
    - 특정 조건이 충족 될때까지 (또는 특정 조건이 아직 충족되지 않을 때까지) Job 또는 Step 을 반복하도록 배치 어플리케이션을 구성할 수 있다
    - 스프링 배치에서는 Step의 반복과 Chunk 반복을 RepeatOperation 을 사용해서 처리하고 있다
    - 기본 구현체로 RepeatTemplate 를 제공한다


<img src="/img/20.png" width="1000px;">

- 반복 종료 여부를 결정하는 세가지 항목
    - RepeatStatus
        - 스프링 배치의 처리가 끝났는지 판별하기 위한 열거형(ENUM)
            - CONTINUABLE - 작업이 남아 있음
            - FINISHED - 더 이상의 반복 없음
    - CompletionPolicy
        - RepeatTemplate 의 iterate 메소드 안에서 반복을 중단할지 결정
        - 실행 횟수 또는 완료시기, 오류 발생시 수행할 작업에 대한 반복여부 결정
        - 정상 종료를 알리는데 사용된다
    - ExceptionHandler
        - RepeatCallback 안에서 예외가 발생하면 RepeatTemplate 가 ExceptionHandler 를 참조해서 예외를 다시 던질지 여부 결정
        - 예외를 받아서 다시 던지게 되면 반복 종료
        - 비정상 종료를 알리는데 사용됨

<img src="/img/21.png" width="1000px;">

<img src="/img/22.png" width="1000px;">

## 2. FaultTolerant

- 개념
    - 스프링 배치는 Job 실행 중에 오류가 발생할 경우 장애를 처리하기 위한 기능을 제공하며 이를 통해 복원력을 향상시킬 수 있다
    - 오류가 발생해도 Step 이 즉시 종료되지 않고 Retry 혹은 Skip 기능을 활성화 함으로써 내결함성 서비스가 가능하도록 한다
    - 프로그램의 내결함성을 위해 Skip 과 Retry 기능을 제공한다
        - Skip
            - ItemReader / ItemProcessor / ItemWriter 에 적용할 수 있다
        - Retry
            - ItemProcessor / ItemWriter 에 적용할 수 있다
    - FaultTolerant 구조는 청크 기반의 프로세스 기반위에 Skip 과 Retry 기능이 추가되어 재정의 되어 있다

```
public Step batchStep() {
	return new stepBuilderFactory.get("batchStep")
		.<I, O>chunk(10)
		.reader()
		.writer()
		.falutTolerant() // 기능 활성화
		.skip() // 예외 발생시 Skip 할 예외 타입 설정
		.skipLimit() // Skip 제한 횟수 설정
		.skipPolicy() // Skip 을 어떤 조건과 기준으로 적용할 것인지 정책 설정
		.noSkip() // 예외 발생 시 Skip 하지 않을 예외 타입 설정
		.retry() // 예외 발생 시 Retry 할 예외 타입 설정
		.retryLimit() // Retry 제한 횟수 설정
		.retryPolicy() // Retry 를 어떤 조건과 기준으로 적용할 것인지 정책 설정
		.backOffPolicy() // 다시 Retry 하기 까지의 지연시간(단위:ms)을 설정
		.noRetry() // 예외 발생 시 Retry 하지 않을 예외 타입 설정
		.noRollback() // 예외 발생시 Rollback 하지 않을 예외 타입 설정
		.build();
	}
}
```

<img src="/img/23.png" width="1000px;">

## 3. Skip

- 개념
    - Skip 은 데이터를 처리하는 동안 설정된 Exception 이 발생했을 경우, 해당 데이터 처리를 건너뛰는 기능이다
    - 데이터의 사소한 오류에 대해 Step 의 실패처리 대신 Skip 함으로써, 배치수행의 빈번한 실패를 줄일 수 있게 한다
    - Skip 기능은 내부적으로 SkipPolicy 를 통해서 구현되어 있다
    - Skip 가능 여부를 판별하는 기준은 다음과 같다
        - 스킵 대상에 포함된 예외인지 여부
        - 스킵 카운터를 초과 했는지 여부


<img src="/img/24.png" width="1000px;">

- 오류 발생시 스킵 설정에 의해서 Item2 번은 건너뛰고 Item3번 부터 다시 처리한다
- ItemReader 는 예외가 발생하면 해당 아이템만 스킵하고 계속 진행한다
- ItemProcessor 와 ItemWriter 는 예외가 발생하면 Chunk 의 처음으로 돌아가서 스킵된 아이템을 제외한 나머지 아이템들을 가지고 처리하게 된다

<img src="/img/25.png" width="1000px;">

## 4. Retry

- 개념
    - ItemProcessor, ItemWriter 에서 설정된 Exception 이 발생했을 경우, 지정한 정책에 따라 데이터 처리를 재시도 하는 기능
    - Skip 과 마찬가지로 Retry 를 함으로써, 배치수행의 빈번한 실패를 줄일 수 있다

<img src="/img/26.png" width="1000px;">

- Retry 기능은 내부적으로 RetryPolicy 를 통해서 구현되어 있다
- Retry 가능 여부를 판별하는 기준은 다음과 같다
    - 재시도 대상에 포함된 예외인지 여부
    - 재시도 카운터를 초과 했는지 여부

<img src="/img/27.png" width="1000px;">

```
public Step batchStep() {
	return stepBuilderFactory.get("")
		.<I, O>chunk(10)
		.reader()
		.writer()
		.falutTolerant()
		.retry() // 예외 발생 시 Retry 할 예외 타입 설정
		.retryLimit() // Retry 제한 횟수 설정
		.retryPolicy() // Retry 를 어떤 조건과 기준으로 적용할 것인지 정책 설정
		.noRetry() // 다시 Retry 까지의 지연시간을 설정
		.backOffPolicy() // 예외 발생 시 Retry 하지 않을 예외 타입 설정
		.noRollback() // 예외 발생 시 Rollback 하지 않을 예외 타입 설정
		.build();
}
```
## 5. Skip & Retry 아키텍처

### ItemReader

<img src="/img/28.png" width="1000px;">

### ItemProcessor

<img src="/img/29.png" width="1000px;">

### ItemWriter

<img src="/img/30.png" width="1000px;">
