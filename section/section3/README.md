# 3. 스프링 배치 도메인 이해
## 1. JOB

### Job

1. 기본 개념
    1. 배치 계층 구조에서 가장 상위에 있는 개념으로서 하나의 배치작업 자체를 의미
    2. JobConfiguration 을 통해 생성되는 객체 단위로서 배치작업을 어떻게 구성하고 실행할 것인지 전체적으로 설정하고 명세해놓은 객체
    3. 배치 Job 을 구성하기 위한 최상위 인터페이스이며 스프링 배치가 기본 구현체를 제공
    4. 여러 Step 을 포함하고 있는 컨테이너로서 반드시 한개 이상의 Step 으로 구성해야 한다
2. 기본 구현체
    1. SimpleJob
        1. 순차적으로 Step 을 실행시키는 Job
        2. 모든 Job 에서 유용하게 사용할 수 있는 표준 기능을 갖고 있다
    2. FlowJob
        1. 특정한 조건과 흐름에 따라 Step 을 구성하여 실행시키는 Job
        2. Flow 객체를 실행시켜서 작업을 진행

### JobInstance

1. 기본개념
    1. Job 이 실행될 때 생성되는 Job 의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업 실행을 나타낸다
    2. Job 의 설정과 구성은 동일하지만 Job 이 실행되는 시점에 처리하는 내용은 다르기 때문에 Job 의 실행을 구분해야 한다
    3. JobInstance 생성 및 실행
        1. 처음 시작하는 Job + JobParameter 일 경우 새로운 JobInstance 생성
        2. 이전과 동일한 Job + JobParameter 으로 실행 할 경우 이미 존재하는 JobInstance 리턴
            1. 내부적으로 JobName + JobKey(jobParametes 의 해시값) 를 가지고 JobInstance 객체를 얻는다
    4. Job 과는 1:N 관계

### JobParameters

1. 기본개념
    1. Job 을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체
    2. 하나의 Job 에 존재할 수 있는 여러개의 JobInstance 를 구분하기 위한 용도
    3. JobParameters 와 JobInstance 는 1:1 관계
2. 생성 및 바인딩
    1. 애플리케이션 실행 시 주입
        1. Java -jar LogBatch.jar requestDate=20210101
    2. 코드로 생성
        1. JobParameterBuilder(주로 많이 사용), DefaultJobParametersConverter
    3. SpEL 이용
        1. @Value(”#{jobparameter[requestDate]}”), @JobScope, @StepScope 선언 필수
