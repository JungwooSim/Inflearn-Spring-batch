# 5-4 스프링 배치 청크 프로세스 활용 - ItemProcessor

## CompositeItemProcessor

- 개념
    - ItemProcessor 들을 연결(Chaining)해서 위임하면 각 ItemProcessor 를 실행시킨다
    - 이전 ItemProcessor 반환값은 다음 ItemProcessor 값으로 연결된다

```
public ItemProcessor itemProcessor() {
	return new CompositeItemProcessorBuilder<>()
		.delegates() // chaining ItemProcessor 객체 설정
		.build();
}
```

<img src="/img/19.png" width="500px;">
