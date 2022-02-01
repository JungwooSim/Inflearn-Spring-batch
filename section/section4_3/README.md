# 4-3. 스프링 배치 실행 - Flow
## 1. FlowJob

- 기본개념
    - Step 을 순차적으로만 구성하는 것이 아닌 특정한 상태에 따라 흐름을 전환하도록 구성할 수 있으며 FlowJobBuilder 에 의해 생성된다
        - Step 이 실패하더라도 Job 은 실패로 끝나지 않도록 해야 하는 경우
        - Step 이 성공했을 때 다음에 실행해야 할 Step 을 구분해서 실행해야 하는 경우
        - 특정 Step 은 전혀 실행되지 않게 구성해야 하는 경우
    - Flow 와 Job 의 흐름을 구성하는데만 관여하고 실제 비즈니스 로직은 Step 에서 이루어진다
    - 내부적으로 SimpleFlow 객체를 포함하고 있으며 Job 실행 시 호출

<img src="/img/5.png" width="500px;">

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
