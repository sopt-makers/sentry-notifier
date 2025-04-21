# sentry-notifier
> Sentry Webhook을 수신하여 팀별로 알림을 분기 전송하는 AWS Lambda 서비스

## ✨ Overview

이 프로젝트는 **Sentry에서 발생한 에러 이벤트를 수신**하고, 
이를 **팀명과 서버 유형(FE/BE 등)에 따라 분기 처리**하여
슬랙 등의 외부 알림 채널로 전송해주는 Lambda 기반 서비스입니다.

본 프로젝트는 Sentry 이벤트를 Slack으로 전송하는 AWS Lambda 함수를 구현하기 위해 Spring 없이 Java만으로 구현했습니다. 
이러한 기술적 결정에는 다음과 같은 이유가 있습니다:

- **AWS Lambda 콜드 스타트 최소화**: SpringBoot와 같은 무거운 프레임워크는 초기화 시간이 길어 Lambda의 콜드 스타트 지연 문제를 악화시킬 수 있습니다. 그래서 순수 Java로만 구현하여 시작 시간을 단축했습니다.
- **리소스 효율성**: 경량화된 애플리케이션으로 Lambda의 메모리 사용량을 최소화하고, 이는 비용 효율성으로 이어집니다.
- **조직 친화적 기술 스택**: Kotlin + Ktor와 같은 대안도 고려했지만, BE 챕터원들이 메이커스 프로젝트 개발에 Java를 주로 사용하고 있다는 점을 고려했습니다. 새로운 언어 도입 시 팀 내 지식 공유와 유지보수에 추가적인 부담이 발생할 수 있어 기존 기술 스택인 Java를 선택했습니다.
- **최소 의존성**: 필요한 최소한의 라이브러리만 사용하여 배포 패키지 크기를 줄이고 시작 시간을 개선했습니다.

## 🏗️ Tech Stack
- **Java 21**
- **Sentry Webhook**
- **AWS Lambda**
- **API Gateway**

## 🏛️ System Architecture
<img src="https://github.com/user-attachments/assets/226e93b0-bd41-4a3d-8493-ffc67d86006f" alt="Sentry-Lambda 아키텍처 이미지">

## 🔧 Key Features
- Sentry Webhook 이벤트 수신
- API Gateway의 Stage 기능(dev/prod)을 활용한 환경별 요청 분리
- 팀(team: crew, app 등)과 서버 유형(type: FE, BE 등)에 따른 분기 처리
- Slack 등 외부 알림 채널로 전송 (확장 가능)

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
