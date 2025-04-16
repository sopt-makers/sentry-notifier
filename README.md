# sentry-notifier
> Sentry Webhook을 수신하여 팀별로 알림을 분기 전송하는 AWS Lambda 서비스

## ✨ Overview

이 프로젝트는 **Sentry에서 발생한 에러 이벤트를 수신**하고, 
이를 **팀명과 서버 유형(FE/BE 등)에 따라 분기 처리**하여
슬랙 등의 외부 알림 채널로 전송해주는 Lambda 기반 서비스입니다.

## 🏗️ Tech Stack

- **Java 21**
- **Sentry Webhook**
- **AWS Lambda**
- **API Gateway**

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
