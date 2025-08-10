# Java로 구현한 블록체인 구조 시뮬레이션 (PoW)
작업증명(PoW) 방식의 간단한 블록체인을 Java로 직접 구현한 실습 프로젝트입니다.<br>
블록 → 해시 → 이전 해시 참조 → 트랜잭션 포함 → PoW 채굴 과정을 코드 레벨에서 확인할 수 있습니다.

## 핵심 기능
Block / Transaction 클래스 직접 구현

SHA-256 해시(Java MessageDigest)로 블록 해시 생성

이전 블록 해시 참조로 체인 연결 (무결성 유지)

PoW 채굴: 해시가 0000으로 시작할 때까지 nonce 증가

트랜잭션 리스트 포함 및 변경 시 해시 변화 시연

## 📚 학습/참고
유튜브: 나동빈 — 자바(Java)로 이해하는 블록체인 이론과 실습

## 프로젝트 구조
```
src/
 ├─ core/
 │   ├─ Block.java
 │   ├─ BlockChainStarter.java
 │   └─ Transaction.java
 └─ util/
     └─ Util.java   // SHA-256 해시 유틸
```

## 기술 스택
Language: Java

Crypto: SHA-256 (Java Security MessageDigest)

Data: ArrayList<>

Consensus: Proof of Work (leading zeros)

## ▶️ 실행 방법
JDK 8+ 설치

IDE에서 BlockChainStarter 실행

## 동작 개요
### Block 해시
```java
public String getBlockHash() {
    String transactionInformations = "";
    for (int i = 0; i < transactionList.size(); i++) {
        transactionInformations += transactionList.get(i).getInformation();
    }
    return Util.getHash(nonce + transactionInformations + previousBlockHash);
}
```
nonce + 트랜잭션 문자열 + 이전 해시를 합쳐 SHA-256 해시 생성.

트랜잭션이 바뀌면 블록 해시가 달라져 체인 무결성이 깨짐.

### PoW 채굴
```java
public void mine() {
    while (true) {
        if (getBlockHash().substring(0, 4).equals("0000")) {
            System.out.println(blockID + "번째 블록의 채굴에 성공하였습니다.");
            break;
        }
        nonce++;
    }
}
```
0000(난이도 4)으로 시작하는 해시가 나올 때까지 nonce 증가.

난이도를 올릴수록(예: 000000) 채굴 시간이 급격히 증가.

### 체인 생성/변조 시연 (main)
```java
Block block1 = new Block(1, null, 0, new ArrayList<>());
block1.mine();
block1.getInformation();

Block block2 = new Block(2, block1.getBlockHash(), 0, new ArrayList<>());
block2.addTransaction(new Transaction("나동빈", "박세연", 1.5));
block2.addTransaction(new Transaction("이태일", "박세연", 0.7));
block2.mine();
block2.getInformation();

Block block3 = new Block(3, block2.getBlockHash(), 0, new ArrayList<>());
block3.addTransaction(new Transaction("강종구", "이상욱", 0.8));
block3.addTransaction(new Transaction("박세연", "나동빈", 9.9)); // 악의적 변경 예시
block3.mine();
block3.getInformation();

Block block4 = new Block(4, block3.getBlockHash(), 0, new ArrayList<>());
block4.addTransaction(new Transaction("이상욱", "강종구", 0.1));
block4.mine();
block4.getInformation();
```

#### 샘플 출력 (예시)
```markdown
1번째 블록의 채굴에 성공하였습니다.
------------------------------------
블록 번호 : 1
이전 해시 값: null
채굴 변수 값 : 52310
트랜잭션 개수 : 0개
블록 해시 : 0000e3f9...
------------------------------------
2번째 블록의 채굴에 성공하였습니다.
...
3번째 블록의 채굴에 성공하였습니다.  // 트랜잭션 변경 시 해시가 완전히 달라짐
...
```

## 문제와 해결 (개발 기록)
문제 1: 단순 블록 객체를 넘어, 이전 해시를 이용한 체인 구조와 블록 해시 설계 필요
해결: previousBlockHash 필드로 체인 연결, 블록 해시는 nonce + 트랜잭션 + 이전 해시를 해싱

문제 2: Java에서 SHA-256 해시 구현 난이도
해결: MessageDigest.getInstance("SHA-256") 기반 Util.getHash() 유틸 작성

문제 3: PoW 채굴 조건(예: 0000 프리픽스) 구현 필요
해결: mine()에서 nonce를 반복 증가시키며 조건 만족 시점 탐지

문제 4: 트랜잭션 변경이 블록 해시에 반영되어 변조 감지가 가능해야 함
해결: 블록 해시 생성 시 트랜잭션 문자열을 포함, 변경 시 해시가 달라져 연쇄 무결성 검증 가능

## 학습 포인트
해시 함수의 
1. 축약성(어떤 길이의 입력이든 고정 길이로 변환)
2. 결정성(같은 입력은 항상 같은 해시값)
3. 충돌내성(서로 다른 입력이 같은 해시값을 만들 가능성이 매우 낮음)
위 3가지 성질이 블록체인 무결성의 핵심임을 체감

PoW는 “확률적 탐색” 문제이므로 난이도 ↑ → 연산량 ↑ → 채굴 시간 ↑

블록이 이전 해시에 강하게 의존하므로, 중간 블록 변조 시 이후 전부 재채굴 필요

## 개선/리팩토링 아이디어

제네릭/타입 안정성

```java
// before: new ArrayList()
new ArrayList<Transaction>()
```

난이도 파라미터화

```java
private int difficulty = 4;
private boolean meetsTarget(String hash) {
    return hash.startsWith("0".repeat(difficulty));
}
```

체인 검증 유틸 추가

```java
boolean validateChain(List<Block> chain) {
    for (int i = 1; i < chain.size(); i++) {
        Block prev = chain.get(i - 1);
        Block curr = chain.get(i);
        boolean ok = curr.getPreviousBlockHash().equals(prev.getBlockHash())
                     && curr.getBlockHash().equals(curr.recalculateHash());
        if (!ok) return false;
    }
    return true;
}
```
