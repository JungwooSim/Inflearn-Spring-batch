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

<img src="/img/2.png" width="500px;"></br>
([https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html](https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html))

**Job 관련 테이블**

- BATCH_JOB_INSTANCE
- BATCH_JOB_EXECUTION
- BATCH_JOB_EXECUTION_PARAMS
- BATCH_JOB_EXECUTION_CONTEXT
