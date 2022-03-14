# 7. 스프링 배치 멀티 스레드 프로세싱
## 1. 기본개념

- 단일스레드 Vs. 멀티 스레드
    - 프로세스 내 특정 작업을 처리하는 스레드가 하나일 경우 단일 스레드, 여러 개 일 경우 멀티 스레드로 정의할 수 있다
    - 작업 처리에 있어서 단일 스레드와 멀티 스레드의 선택 기준은 어떤 방식이 자원을 효율적으로 사용하고 성능처리에 유리한가 하는 점이다
    - 일반적으로 복잡한 처리나 대용량 데이터를 다루는 작업일 경우 전체 소요 시간 및 성능상의 이점을 가져오기 위해 멀티 스레드 방식을 선택한다
    - 멀티 스레드 처리 방식은 데이터 동기화 이슈가 존재하기 때문에 최대한 고려해서 결정해야 한다

<img src="/img/31.png" width="1000px;">

<img src="/img/32.png" width="1000px;">

- 스프링 배치 스레드 모델
    - 기본적으로 단일 스레드 방식으로 작업 처리
    - 성능 향상과 대규모 데이터 작업을 위한 비동기 처리 및 Scale out 기능을 제공한다
    - Local 과 Remote 처리를 지원한다
        - AyncItemProcessor / AsyncItemWriter
            - ItemProcessor 에게 별도의 스레드가 할당되어 작업을 처리하는 방식
        - Mulit-threaded Step
            - Step 내 Chunk 구조인 ItemReader, ItemProcessor, ItemWriter 마다 여러 스레드가 할당되어 실행하는 방법
        - Remote Chunking
            - 분산환경처럼 Step 처리가 여러 프로세스로 분할되어 외부의 다른 서버로 전송되어 처리하는 방식
        - Parallel Steps
            - Step 마다 스레드가 할당되어 여러개의 Step 을 병렬로 실행하는 방법
        - Partitioning
            - Master/Slave 방식으로서 Master 가 데이터를 파티셔닝 한 다음 각 파티션에게 스레드를 할당하여 Slave 가 독립적으로 작동하는 방식

## 2. AsyncItemProcessor / AsyncItemWriter

- 개념
    - Step 안에서 ItemProcessor 가 비동기적으로 동작하는 구조
    - AsyncItemProcessor 와 AsncItemWriter 가 함께 구성이 되어야 한다
    - AsyncItemProcessor 로 부터 AsyncItemWriter 가 받는 최종 결과값은 List <Future<T>> 타입이며 비동기 실행이 완료될 때까지 대기한다
    - Spring-batch-integration 의존성이 필요하다

<img src="/img/33.png" width="1000px;">

<img src="/img/34.png" width="1000px;">

## 3. Multi-threaded Step

- 개념
    - Step 내에서 멀리 스레드로 Chunk 기반 처리가 이루어지는 구조
    - TaskExecutorRepeatTemplate 이 반복자로 사용되며 설정한 개수 (throttleLimit) 만큼의 스레드를 생성하여 수행

<img src="/img/35.png" width="1000px;">

<img src="/img/36.png" width="1000px;">
