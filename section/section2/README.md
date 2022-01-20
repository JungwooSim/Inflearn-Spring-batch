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

## 3. DB 스키마 생성 및 이해
