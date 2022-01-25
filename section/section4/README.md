# 4. 스프링 배치 실행 - Job
## 1. 배치 초기화 설정

- JobLauncherApplicationRunner
    - Spring Batch 작업을 시작하는 ApplicatiionRunner 로서 BatchAutoConfiguration 에서 생성된다
    - 스프링 부트에서 제공하는 ApplicationRunner 의 구현체로 어플리케이션이 정상적으로 구동되자마자 실행된다
    - 기본적으로 빈으로 등록된 모든 job 을 실행시킨다
- BatchProperties
    - Spring Batch 의 환경 설정 클래스
    - Job 이름, 스키마 초기화 설정, 테이플 Prefix 등의 값을 설정할 수 있다
    - [application.properties](http://application.properties) OR application.yml 파일에 설정
- Job 실행 옵션
    - 지정한 Batch Job 만 실행하도록 할 수 있다

    ```
    spring.batch.job.names: ${job.name.NONE}
    --job.name=helloJob
    --job.name=helloJob,simpleJob (하나 이상의 Job 을 실행할 경우 쉼표로 구분)
    ```


application.yml

```
spring:
  profiles:
    active: local
  batch:
    job:
      names: ${job.name:NONE} # --job.name={batchName}
      #names: batchJob1 # 설정을 통해 실행할 batch name 선정 가능
      #enabled: false # Spring boot 가 batch 를 자동 실행 여부
    #jdbc:
    #  initialize-schema: always
    #  table-prefix: SYSTEM_
```

## 2. Job and Step

### JobBuilderFactory & JobBuilder

스프링 배치는 Job 과 Step 을 쉽게 생성 및 설정할 수 있도록 util 성격의 빌더 클래스들을 제공

- JobBuilderFactory
    - JobBuilder 를 생성하는 팩토리 클래스로서 get(String name) 메서드 제공
    - jobBuilderFactory.get(”jobName”) - “jobName” 은 스프링 배치가 Job 을 실행시킬 때 참조하는 Job 의 이름
- JobBuilder
    - Job 을 구성하는 설정 조건에 따라 두 개의 하위 빌더 클래스를 생성하고 실제 Job 생성을 위임
    - SimpleJobBuilder
        - SimpleJob 을 생성하는 Builder 클래스
        - Job 실행과 관련된 여러 설정 API 를 제공
    - FlowJobBuilder
        - FlowJob 을 생성하는 Builder 클래스
        - 내부적으로 FlowBuilder 를 반환함으로써 Flow 실행과 관련된 여러 설정 API 를 제공

### SimpleJob

- 기본개념
    - SimpleJob 은 Step 을 실행시키는 Job 구현체로서 SimpleJobBuilder 에 의해 생성
    - 여러 단계의 Step 으로 구성할 수 있으며 Step 을 순차적으로 실행시킨다
    - 모든 Step 의 실행이 성공적으로 완료되어야 Job 이 성공적으로 완료 된다
    - 가장 마지막에 실행한 Step 의 BatchStatus 가 Job 의 최종 BatchStatus 가 된다

```
public Job BatchJob() {
      return jobBuilderFactory.get("batchJob") // JobBuilder 를 생성하는 팩토리, Job 의 이름을 매게변수로 받는다.
              .start() // 처음 실행할 Step 설정, 최초 한번 설정 가능하고 이 메서드 실행 시 SimpleJobBuilder 을 반환한다.
              .next() // 다음에 실행할 Step 설정
              .incrementer() // JobParameter 의 값을 자동증가해주는 JobParametersIncrementer 설정 할 수 있다.
              .preventRestart() // Job 의 재시작 가능 여부 설정. 기본값을 true
              .validator() // JobParameter 를 실행전에 올바른 구성이 되었는지 검증하는 JobParametersValidator 를 설정할 수 있다.
              .listener() // Job 라이프 사이클의 특정 시점에 콜백 제공받도록 JobExecutionListener을 설정할 수 있다.
              .build();
  }
```
### StepBuilderFactory & StepBuilder
`validator()`
- Job 실행에 필요한 파라미터를 검증하는 용도
- DefaultJobParametersValidator 구현체를 지원하며 좀 더 복잡한 제약이 있다면 인터페이스를 직접 구현할 수 도 있다
