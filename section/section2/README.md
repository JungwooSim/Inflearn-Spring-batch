# 2. 스프링 배치 시작
## 1. 프로젝트 구성 및 의존성 설정

`@EnableBatchProcessing` 을 스프링 배치가 작동하도록 선언

- 총 4개의 설정 클래스를 실행시키고 스프링 배치의 모든 초기화 및 실행 구성이 이루어진다.
    - BatchAutoConfiguration
        - 스프링 배치가 초기화 될 때 자동으로 실행되는 설정 클래스
        - Job을 수행하는 JobLauncherApplicationRunner 빈을 생성
    - SimpleBatchConfiguration
        - JobBuilderFactory 와 StepBuilderFactory 생성
        - 스프링 배치의 주요 구성 요소 생성 - 프록시 객체로 생성
    - BatchConfigurerConfiguration
        - BasicBatchConfigurer
            - SimpleBatchConfiguration 에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스
        - JpaBatchConfigurer
            - JPA 관련 객체를 생성하는 설정 클래스
- 빈으로 등록된 모든 Job 을 검색하여 초기화와 동시에 Job 을 수행

## 2. Hello Spring Batch 시작하기
- @Configuration : 하나의 배치 Job 을 정의하고 빈 설정
- JobBuilderFactory : Job 을 생성하는 빌더 팩토리
- StepBuilderFactory : Step 을 생성하는 빌더 팩토리
- Job
- Step
- tasklet : Step 안에서 단일 태스크로 수행되는 로직 구현

## 3. DB 스키마 생성 및 이해
### DB 스키마 생성

- 스프링 배치 메타 데이터
    - 스프링 배치의 실행 및 관리를 목적으로 여러 도메인들(Job, Step, JobParameters..) 의 정보들을 저장, 업데이트, 조회할 수 있는 스키마 제공
    - 과거, 현재의 실행에 대한 세세한 정보, 실행에 대한 성공과 실패 여부 등을 일목요연하게 관리함으로써 배치 운용에 리스크 발생시 빠른 대처 가능
    - DB 와 연동할 경우 필수적으로 메타 테이블이 생성 되어야 한다.
- DB 스키마 제공
    - 파일 위치 : /org/springframework/batch/core/schema-*.sql
    - DB 유형별로 제공 (h2, msyql, oracle, 등)
- 스키마 생성 설정
    - 수동 생성
        - 쿼리 복사 후 직접 실행
    - 자동 생성
        - spring.batch.jdbc.initialize-schema 설정
            - ALWAYS
                - 스크립트 항상 실행
                - RDBMS 설정이 되어 있을 경우 내장 DB 보다 우선적으로 실행
            - EMBEDDED
                - 내장 DB 일 때만 실행되며 스키마가 자동 생성된다.
                - default 값
            - NEVER
                - 스크립트 항상 실행 안함
                - 내장 DB 일 경우 스크립트가 생성되지 않으므로 오류 발생
                - 운영 환경에서는 수동으로 설정 후 NEVER 로 설정하는 것을 권장


### DB 스키마 이해

<img src="/img/2.png" width="1000px;"></br>
([https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html](https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html))

**Job 관련 테이블**

- BATCH_JOB_INSTANCE
    - Job 이 실행될 때 JobInstance 정보가 저장되며 job_name 과 job_key 를 키로 하여 하나의 데이터가 저장
    - 동일한 job_name 과 job_key  로 중복 저장될 수 없다.
- BATCH_JOB_EXECUTION
    - job 의 실행정보가 저장되며 Job 생성, 시작, 종료 시간, 실행상태, 메시지 등을 관리
- BATCH_JOB_EXECUTION_PARAMS
    - Job 과 함께 실행되는 JobParameter 정보를 저장
- BATCH_JOB_EXECUTION_CONTEXT
    - Job 의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화(Json 형태)해서 저장
    - Step 간 서로 공유 가능

**Step 관련 테이블**

- BATCH_STEP_EXECUTION
    - Step 의 실행정보가 저장되며 생성, 시작, 종료 시간, 실행상태 메시지 등을 관리
- BATCH_STEP_EXECUTION_CONTEXT
    - Step 의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화(Json 형식) 해서 저장
    - Step 별로 저장되며 Step 간 서로 공유 할 수 없다

**Table 정보**

BATCH_JOB_INSTANCE

- JOB_INSTANCE_ID : 고유하게 식별할 수 있는 기본 키
- VERSION : 업데이트 될 때마다 1씩 증가
- JOB_NAME : Job 을 구성할 때 부여하는 Job 의 이름
- JOB_KEY : job_name 과 jobParameter 를 합쳐 해싱한 값을 저장

BATCH_JOB_EXECUTION

- JOB_EXECUTION_ID : JobExecution 을 고유하게 식별할 수 있는 기본 키, Job_INSTANCE 와 일대 다 관계
- VERSION : 업데이트될 때 마다 1씩 증가
- JOB_INSTANCE_ID : JOB_INSTANCE 의 키 저장
- CREATE_TIME : 실행이 생성된 시점을 TimeStamp 형식으로 기록
- START_TIME : 실행이 시작된 시점을 TimeStamp 형식으로 기록
- END_TIME : 실행이 종료된 시점을 TimeStamp 으로 기록하며 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있다.
- STATUS : 실행 상태(BatchStatus)를 저장 (COMPLETED, FAILED, STOPPED, ...)
- EXIT_CODE : 실행 종료코드를 저장 (COMPLETED, FAILED, ...)
- EXIT_MESSAGE :  Status 가 실패일 경우 실패 원인 등의 내용을 저장
- LAST_UPDATED : 마지막 실행시점을 TimeStamp 형식으로 기록

BATCH_JOB_EXECUTION_PARAMS

- JOB_EXECUTION_ID : JobExecution 식별 키, JOB_EXECUTION 과는 일대다 관계
- TYPE_CD : STRING, LONG, DATE, DUBLE 타입 정보
- KEY_NAME : 파라미터 키 값
- STRING_VAL : 파라미터 문자 값
- DATE_VAL : 파라미터 날짜 값
- LONG_VAL : 파라미터 LONG 값
- DOUBLE_VAL : 파라미터 DOUBLE 값
- IDENTIFYING : 식별여부(TRUE, FALSE)

BATCH_JOB_EXECUTION_CONTEXT

- JOB_EXECUTION_ID : JobExecution 식별 키, JOB_EXECUTION 마다 각 생성
- SHORT_CONTEXT : JOB 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장
- SERIALIZED_CONTEXT : 직렬화된 전체 컨텍스트

BATCH_STEP_EXECUTION

- STEP_EXECUTION_ID : Step의 실행정보를 고유하게 식별할 수 있는 기본 키
- VERSION : 업데이트될 때 마다 1씩 증가
- STEP_NAME : Step 을 구성할 때 부여하는 Step 이름
- JOB_EXECUTION_ID : JobExecution 기본키, JobExecution 과는 일대 다 관계
- START_TIME : 실행이 시작된 시점을 TimeStamp 형식으로 기록
- END_TIME : 실행이 종료된 시점을 TimeStamp 으로 기록하며 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있음
- STATUS : 실행상태를 저장 (COMPLETED, FAILED, STOPPED, ...)
- COMMIT_COUNT : 트랜잭션당 커밋되는 수를 기록
- READ_COUNT : 실행시점에 Read 한 Item 수를 기록
- FILTER_COUNT : 실행도중 필터링된 Item 수를 기록
- WRITE_COUNT : 실행도중 저장되고 커밋된 Item 수를 기록
- READ_SKIP_COUNT : 실행도중 Read 가 Skip 된 Item 수를 기록
- WRITE_SKIP_COUNT : 실행도중 Write 가 Skip 된 Item 수를 기록
- PROCESS_SKIP_COUNT: 실행도중 Process 가 Skip 된 Item 수를 기록
- ROLLBACK_COUNT : 실행도중 rollback 이 일어난 수를 기록
- EXIT_CODE : 실행 종료코드를 저장 (COMPLETED, FAILED)
- EXIT_MESSAGE : Status 가 실패일 경우 실패 원인 등의 내용을 저장
- LAST_UPDATED : 마지막 실행시점을 TimeStamp 형식으로 기록

BATCH_STEP_EXECUTION_CONTEXT

- STEP_EXECUTION_ID : StepExecution 식별 키, STEP_EXECUTION 마다 각 생성
- SHORT_CONTEXT : STEP 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장
- SERIALIZED_CONTEXT : 직렬화된 전체 컨텍스트
