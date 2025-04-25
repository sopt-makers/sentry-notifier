# sentry-notifier
> Sentry Webhook을 수신해, 팀명과 서버 유형에 따라 알림을 분기 전송하는 Java 기반 AWS Lambda 서비스

## ✨ Overview

이 프로젝트는 **Sentry에서 발생한 에러 이벤트를 수신**하고, **운영 환경과 팀명/서버 유형(FE/BE 등)을 기반으로 분기 처리**하여 Slack 등 외부 채널로 전달하는 경량 **Java 기반 AWS Lambda 알림 서비스**입니다.

Spring 없이 Java만으로 경량 구현된 이 Lambda 함수는 다음과 같은 이유로 설계되었습니다:

- **빠른 콜드 스타트**: SpringBoot는 초기화 시간이 길어 Lambda 환경에서 지연이 발생할 수 있습니다. 순수 Java로 구현하여 시작 시간을 최소화했습니다.
- **리소스와 비용 효율성**: 메모리 사용량을 줄여 실행 비용도 함께 절감할 수 있습니다.
- **팀 친화적 스택 선택**: Kotlin + Ktor 등도 고려했지만, 메이커스 프로젝트에서 Java 사용률이 높아 팀의 유지보수성과 온보딩 효율을 고려해 Java를 선택했습니다.
- **최소 의존성 지향**: 최소한의 라이브러리만 사용하여 배포 크기와 로딩 시간을 줄였습니다.

## 🎯 Motivation

Sentry는 **탁월한 에러 모니터링 도구**지만, Slack과의 공식 연동 기능은 **팀 플랜 이상(월 $26, 약 37,000원)의 유료 요금제**를 구독해야만 사용할 수 있습니다.

**SOPT Makers** 조직에서는 이 비용을 매달 지출하는 것이 현실적으로 부담스러워, **무료 플랜 내에서 할 수 있는 범위 안에서 Sentry를 활용**해왔습니다. 다행히 **에러 수집 및 분석 기능 자체는 무료 플랜만으로도 충분히 유용**했기 때문에, 기존에는 실시간 알림 없이도 에러를 관리해왔습니다.

하지만 시간이 지날수록, **실시간 알림이 없다는 점이 에러 대응 속도에 한계를 만든다는 문제를 체감**했습니다. **Slack과 같은 커뮤니케이션 툴과 연동해 실시간 알림을 받을 수 있어야** 에러 발생 시 더 **신속하고 효과적인 대응이 가능**하다고 판단했습니다.

조사 과정에서 **Sentry의 무료 플랜에서도 Webhook 기능은 제공**된다는 사실을 확인했고, 이를 활용하면 **별도의 유료 요금제 없이도 Slack 알림 연동을 직접 구현할 수 있다**는 가능성을 발견했습니다. 또한, 이 기능을 **AWS Lambda의 무료 실행 한도 내에서 충분히 운영 가능**하다는 점도 **비용 부담 없이 실시간 알림 시스템을 구축할 수 있는 결정적 장점**이었습니다.

이에 **Sentry Webhook 이벤트를 Slack Webhook 포맷으로 변환**해 전달하는 **경량 Lambda 서비스를 직접 구축**했습니다.

결과적으로 이 프로젝트는 **비용 절감**, **운영 효율 향상**, 그리고 **기술적 도전**이라는 세 가지 의미를 모두 담은 **작지만 실용적인 인프라 개선 사례**가 되었습니다.

## 🏗️ Tech Stack
- **Java 21** – 최신 LTS(Long-Term Support) 버전으로, 성능 개선과 안정성을 고려해 선택
- **Gradle + Shadow Plugin** – AWS Lambda 배포용 fat JAR 빌드
- **AWS Lambda Java SDK** – Lambda Core 및 Event 지원
- **Jackson** – Sentry Webhook의 JSON 직렬화/역직렬화
- **Lombok** – 보일러플레이트 코드 최소화
- **SLF4J + SimpleLogger** – 경량 로그 처리
- **java-dotenv** – .env 기반 환경 설정 관리
- **JUnit 5, Mockito** – 테스트 코드 작성 및 Mock 테스트

## 🏛️ System Architecture
<img src="https://github.com/user-attachments/assets/00820ca8-d6d9-4ecb-bd2a-a9ad6039568c" alt="Sentry-Lambda 아키텍처 이미지">

> 에러 이벤트가 발생하면, 운영 환경(dev/prod)에 따라 API Gateway의 각기 다른 Stage 엔드포인트가 호출되고, 이를 통해 Lambda가 트리거됩니다. Lambda는 전달받은 이벤트에서 팀명, 서버 유형(FE/BE), 알림 서비스(Slack/Discord 등)를 분기 처리하여, 적절한 채널로 알림을 전송합니다.

## 🔧 Key Features
### Sentry Webhook 이벤트 수신
- Sentry에서 발생한 **에러 이벤트를 실시간으로 수신하여 처리**합니다.

### 운영 환경(dev/prod)별 분리 처리
- API Gateway의 **Stage 기능을 활용하여 환경별(dev, prod 등)로 요청을 분리**합니다.
- 단일 Lambda를 호출하지만, **추후 확장성과 개발 편의성을 고려해 Stage별 엔드포인트를 분리 구성**했습니다.

### 경로 기반의 알림 대상 분기 처리
- 요청 경로의 **Path Parameter를 기반**으로 **알림 전송 대상을 유연하게 분기 처리**합니다.
- 요청 경로 형식: `/webhook/{team}/{type}/{service}`
  - `team` 예시 - `playground`, `crew`, `app`, `platform`, `admin`  
  - `type` 예시 - `fe`, `be`, `ios`, `android`
  - `service` 예시 - `slack`, `discord`
    - 해당 필드를 생략하면 기본값으로 `slack`이 사용됩니다.

### 외부 알림 채널 연동 (확장 가능)
- 알림 채널 전송 로직은 **`NotificationService` 인터페이스와 이를 구현한 서비스들로 분리**되어 있으며, **`Factory` 패턴을 사용해 유연하게 확장할 수 있도록 설계되었습니다.**
  - `NotificationService`는 공통된 `sendNotification()` 메서드를 정의하고 있으며, 각 서비스별 구현체가 해당 인터페이스를 구현합니다.
  - `NotificationServiceFactory`는 `WebhookRequest` DTO의 `serviceType(e.g., "slack", "discord")`필드를 기반으로 적절한 알림 서비스 인스턴스를 반환합니다.
    - 예: `"serviceType": "slack"` → `SlackNotificationService` 인스턴스, `"serviceType": "discord"` → `DiscordNotificationService` 인스턴스
    - 미등록 서비스 유형 요청 시 `UnsupportedServiceTypeException` 예외 발생

- 이 구조 덕분에, 새로운 알림 채널(예: `Microsoft Teams`, `Mattermost` 등)을 추가하고 싶다면 다음 두 단계만으로 확장 가능합니다:
    1. `NotificationService를` 구현하는 새 클래스 생성
    2. `NotificationServiceFactory의` static 블록에 해당 구현체를 등록
- 이런 방식은 **`OCP (Open-Closed Principle, 개방-폐쇄 원칙)`에 부합하며, 기존 코드를 변경하지 않고 기능을 확장할 수 있게 해줍니다.**

## 🌿 Branch Strategy
이 프로젝트는 **GitHub Flow** 전략을 따릅니다:
- `main`: 항상 배포 가능한 상태를 유지하는 기본 브랜치
- 기능 개발 시 `feat/#이슈번호` 형식의 브랜치 생성
- 버그 수정 시 `bug/#이슈번호` 형식의 브랜치 생성
- 작업 완료 후 `Pull Request`를 통해 코드 리뷰 진행
- 승인 후 `main` 브랜치에 병합

## 💬 Commit Message Convention
```
type: 제목 (50자 이내)
본문 (선택 사항, 72자 이내)
footer (선택 사항)
```

### Commit Types
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음)
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드 프로세스, 패키지 매니저 설정 등 변경
- `delete`: 사용하지 않는 코드, 파일, 또는 리소스 삭제


## 📸 Implementation Results
<img src="https://github.com/user-attachments/assets/0ef3eb16-3c37-4409-892f-ec5b3f04d707" alt="Sentry-Notifier FE 스크린샷">

<br>

<img src="https://github.com/user-attachments/assets/b58b9c70-2bd0-4f82-a85d-9d2bf6b9d086" alt="Sentry-Notifier BE 스크린샷">
