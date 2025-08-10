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
Block 해시
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

PoW 채굴
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
