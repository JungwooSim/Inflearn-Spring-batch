# 4-3. 스프링 배치 실행 - Flow
## 1. FlowJob

- 기본개념
    - Step 을 순차적으로만 구성하는 것이 아닌 특정한 상태에 따라 흐름을 전환하도록 구성할 수 있으며 FlowJobBuilder 에 의해 생성된다
        - Step 이 실패하더라도 Job 은 실패로 끝나지 않도록 해야 하는 경우
        - Step 이 성공했을 때 다음에 실행해야 할 Step 을 구분해서 실행해야 하는 경우
        - 특정 Step 은 전혀 실행되지 않게 구성해야 하는 경우
    - Flow 와 Job 의 흐름을 구성하는데만 관여하고 실제 비즈니스 로직은 Step 에서 이루어진다
    - 내부적으로 SimpleFlow 객체를 포함하고 있으며 Job 실행 시 호출

<img src="/img/5.png" width="1000px;">

- API 소개

```
public Job batchJob() {
	return jobBuilderFactory.get("batchJob")
	.start() // Flow 시작하는 Step 설정
	.on() // Step 의 실행결과로 돌려받는 종료상태(ExitStatus) 를 캐치하여 매칭하는 패턴, TransitionBuilder 반환
	.to() // 다음으로 이동할 step 지정
	.stop() / fail() / end() / stopAndRestart() // Flow 를 {중지, 실패, 종료} 하도록 설정
	.from() // 이전 단계에서 정의한 Step 의 Flow 를 추가적으로 정의
	.next() // 다음으로 이동할 Step 지정
	.end() // build 앞에 위치하면 FlowBuilder 를 종료하고 SimpleFlow 객체 생성
	.build()
}
```

## 2. API 설정
### Transition - 배치상태 유형
**BatchStatus**</br>
JobExecution 과 StepExecution 의 속성으로 Job 과 Step 의 종료 후 최종 결과 상태가 무엇인지 정의

- SimpleJob
    - 마지막 Step 의 BatchStatus 값을 Job 의 최종 BatchStatus 값으로 반영
    - Step 이 실패할 경우 해당 Step 이 마지막 Step 이 된다
- FlowJob
    - Flow 내 Step 의 ExitStatus 값을 FlowExecutionStatus 값으로 저장
    - 마지막 Flow 의 FlowExecutionStatus 값을 Job 의 최종 BatchStatus 값으로 반영
- COMPLETED, STARTING, STARTED, STOPPING, STOPPED, FAILED, ABANDONED, UNKNOWN
- ABANDONED 는 처리 완료, 성공하지 못한 단계와 재시작시 건너뛰어야 하는 단계

**ExitStatus**</br>
JobExecution 과 StepExecution 의 속성으로 Job 과 Step 의 실행 후 어떤 상태로 종료되었는지 정의</br>
기본적으로 ExitStatus 는 BatchStatus 와 동일한 값으로 설정

- SimpleJob
    - 마지막 Step 의 ExitStatus 값을 Job 의 최종 ExitStatus 값으로 반영
- FlowJob
    - Flow 내 Step 의 ExitStatus 값을 FlowExecutionStatus 값으로 저장
    - 마지막 Flow 의 FlowExecutionStatus 값을 Job 의 최종 ExitStatus 값으로 반영
- UNKNOWN, EXECUTING, COMPLETED, NOOP, FAILED, STOPPED

**FlowExecutionStatus**</br>
FlowExecution 의 속성으로 Flow 의 실행 후 최종 결과 상태가 무엇인지 정의</br>
Flow 내 Step 이 실행되고 나서 ExitStatus 값을 FlowExecutionStatus 값으로 저장</br>
FlowJob 의 배치 결과 상태에 관여

- COMPLETED, FAILED, STOPPED, UNKNOWN

### ExitStatus

- 기본개념
    - ExitStatus 에 존재하지 않는 exitCode 를 새롭게 정의해서 설정하여 사용할 수 있다
    - StepExecutionListener 의 afterStep() 메서드에서 Custom exitCode 생성 후 새로운 ExitStatus 반환
    - Step 실행 후 완료 시점에서 현재 exitCode 를 사용자 정의 exitCode 로 수정할 수 있다

### JobExecutionDecider

- 기본개념
  - ExitStatus 를 조작하거나 StepExecutionListener 를 등록할 필요 없이 Transition 처리를 위한 전용 클래스
  - Step 과 Transition 역할을 명확히 분리하여 설정할 수 있다
  - Step 의 ExitStatus 가 아닌 JobExecutionDecider 의 FlowExecutionStatus 상태값을 새롭게 설정해서 반환한다

## 4. SimpleFlow

### 개념및 API 소개

- 기본개념
  - 스프링 배치에서 제공하는 Flow 의 구현체로서 각 요소(Step, Flow, JobExecutionDecider) 들을 담고 있는 State 를 실행시키는 도메인 객체
  - FlowBuilder 를 사용하여 생성하며 Transition 과 조합하여 여러 개의 Flow 및 중첩 Flow 를 만들어 Job 을 구성할 수 있다
