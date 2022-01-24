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
            1. 내부적으로 JobName + JobKey(jobParameters 의 해시값) 를 가지고 JobInstance 객체를 얻는다
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

### JobExecution

1. 기본개념
    1. JobInstance 에 대한 한번의 시도를 의미하는 객체로서 Job 실행 중에 발생한 정보들을 저장하고 있는 객체
        1. 시작시간, 종료시간, 상태(시작, 완료, 실패), 종료 상태의 속성을 가진다
    2. JobInstance 와의 관계
        1. JobExecution 은 “FAILED”, “COMPLETED” 등의 job 의 실행 결과 상태를 가지고 있다
        2. JobExecution 의 실행 상태 결과가 “COMPLETED” 면 JobInstance 실행이 완료된 것으로 간주하여 재실행이 불가하다
        3. JobExecution 의 실행 상태 결과가 “FAILED” 면 JobInstance 실행이 완료되지 않은 것으로 간주하여 재실행이 가능하다
            1. JobParameter 가 동일한 값으로 Job 을 실행할지라도 JobInstance 를 계속 실행할 수 있다
        4. JobExecution 의 실행 상태가 “COMPLATED” 될 때까지 하나의 JobInstance 내에서 여러 번의 시도가 생길 수 있다

JobExecution 실행 상태를 나타내는 ENUM 클래스</br>
- COMPLETED, STARTING, STARTED, STOPPING, STOPPED, FAILED, ABANDONED, UNKNOWN

## 2. Step

### Step

1. 기본개념
    1. Batch job 을 구성하는 독립적인 하나의 단계로서 실제 배치 처리를 정의하고 컨트롤하는데 필요한 모든 정보를 가지고 있는 도메인 객체
    2. 단순한 단일 테스크 뿐 아니라 입력과 처리 그리고 출력과 관련된 복잡한 비즈니스 로직을 포함하는 모든 설정들을 담고 있다
    3. 배치작업을 어떻게 구성하고 실행할 것인지 Job 의 세부 작업을 Task 기반으로 설정하고 명세해 놓은 객체
    4. 모든 Job 은 하나 이상의 Step 으로 구성
2. 기본구현체
    1. TaskletStep : 가장 기본이 되는 클래스. Tasklet 타입의 구현체들을 제어
    2. PartitionStep : 멀티 스레드 방식으로 Step 을 여러 개로 분리해서 실행
    3. JobStep : Step 내에서 Job 을 실행
    4. FlowStep : Step 내에서 Flow 를 실행하도록 한다

### StepExecution

1. 기본개념
    1. Step 에 대한 한번의 시도를 의미하는 객체로서 Step 실행 중에 발생한 정보들을 저장하고 있는 객체
        1. 시작시간, 종료시간, 상태, commit count, rollback count 등
    2. Step 이 매번 시도될 때마다 생성되며 각 Step 별로 생성
    3. Job 이 재시작 하더라도 이미 성공적으로 완료된 Step 은 재실행되지 않고 실패한 Step 만 실행
    4. 이전 단계 Step 이 실패해서 현재 Step 을 실행하지 않는다면 현재 StepExecution 을 생성하지 않는다. Step 이 실제로 시작됐을 때만 StepExecution 을 생성
    5. JobExecution 과의 관계
        1. Step 의 StepExecution 이 모두 정상적으로 완료되어야 JobExecution 이 정상적으로 완료
        2. Step 의 StepExecution 중 하나라도 실패하면 JobExecution 은 실패한다.

### StepContribution

1. 기본개념
    1. 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExecution 상태를 업데이트하는 도메인 객체
    2. 청크 커밋 직전에 StepExecution 의 apply 메서드를 호출하여 상태를 업데이트
    3. ExitStatus 의 기본 종료코드 외 사용자 정의 종료코드를 생성해서 적용할 수 있다

## 3. ExecutionContext

1. 기본개념
    1. 프레임워크에서 유지 및 관리하는 키/값으로 된 컬렉션으로 StepExecution 또는 JobExecution 객체의 상태(state) 를 저장하는 공유 객체
    2. DB 에 직렬화 한 값으로 저장 - {”key” : “value”}
    3. 공유 범위
        1. Step 범위 - 각 Step 의 StepExecution 에 저장되며 Step 간 서로 공유 안됨
        2. Job 범위 - 각 Job 의 JobExecution 에 저장되며 Job 간 서로 공유 안되며 해당 Job 의 Step 간 서로 공유됨
    4. Job  재시작시 이미 처리한 Row 데이터는 건너뛰고 이후로 수행하도록 할 때 상태 정보를 활용

## 4. JobRepository / JobLauncher

### JobRepository

1. 기본개념
    1. 배치 작업 중의 정보를 저장하는 저장소 역할
    2. Job 이 언제 수행되었고, 언제 끝났으며, 몇 번이 실행되었고 실행에 대한 결과 등의 배치 작업의 수행과 관련된 모든 meta data 를 저장
        1. JobLauncher, Job, Step 구현체 내부에서 CRUD 기능을 처리
2. 설정
    1. @EnableBatchProcessing 어노테이션만 선언하면 JobRepository 가 자동으로 빈으로 생성
    2. BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer 를 상속해서 JobRepository 설정을 커스터마이징 가능
        1. JDBC 방식으로 설정 - JobRepositoryFactoryBean
            1. 내부적으로 AOP 기술을 통해 트랜잭션 처리를 해주고 있다
            2. 트랜잭션 isolation 의 기본값은 SERIALIZEBLE 로 최고 수준, 다른 레벨로 지정 가능
            3. 메타 테이블의 Table Prefix 를 변경할 수 있다. 기본 값은 “BATCH_” 이다
        2. In Memory 방식으로 설정 - MapJobRepositoryFactoryBean
            1. 성능 등의 이슈로 도메인 오브젝트를 굳이 데이터베이스에 저장하고 싶지 않을 경우
            2. 보통 Test, 프로토타입의 빠른 개발을 필요할때 사용

### JobLauncher

1. 기본개념
    1. 배치 Job 을 실행시키는 역할
    2. Job 과 Job Parameters 를 인자로 받으며 요청된 배치 작업을 수행한 후 최종 client 에게 JobExecution 을 반환
    3. 스프링 부트 배치가 구동되면 JobLauncher 빈이 자동으로 생성된다
    4. Job 실행
        1. JobLauncher.run(Job, JobParameters)
        2. 스프링 부트 배치에서는 JobLauncherApplicationRunner 가 자동적으로 JobLauncher  을 실행한다
        3. 동기적 실행
            1. taskExecutor 를 SyncTaskExecutor 로 설정할 경우 (기본값은 SyncTaskExecutor)
            2. JobExecution 을 획득하고 배치 처리를 최종 완료한 이후 Client 에게 JobExecution 을 반환
            3. 스케줄러에 의한 배치처리에 적합 - 배치처리시간이 길어도 상관없는 경우
        4. 비동기적 실행
            1. taskExecutor 가 SimpleAsyncTaskExecutor 로 설정할 경우
            2. JobExecution 을 획득한 후 Client 에게 바로 JobExecution 을 반환하고 배치처리를 완료한다
            3. HTTP 요청에 의한 배치처리에 적합 - 배치처리 시간이 긴 경우 응답이 늦어지지 않도록 한다
