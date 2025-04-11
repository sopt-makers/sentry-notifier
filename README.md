# sentry-notifier
> Sentry Webhook을 수신하여 팀별로 알림을 분기 전송하는 AWS Lambda 서비스

## ✨ 소개

이 프로젝트는 **Sentry에서 발생한 에러 이벤트를 수신**하고, 
이를 **팀명과 서버 유형(FE/BE 등)에 따라 분기 처리**하여
슬랙 등의 외부 알림 채널로 전송해주는 Lambda 기반 서비스입니다.

## 🏗️ 기술 스택

- **Java**
- **Sentry Webhook**
- **AWS Lambda**
- **API Gateway**

## 🔧 주요 기능
- Sentry Webhook 이벤트 수신
- API Gateway의 Stage 기능(dev/prod)을 활용한 환경별 요청 분리
- 팀(team: crew, app 등)과 서버 유형(type: FE, BE 등)에 따른 분기 처리
- Slack 등 외부 알림 채널로 전송 (확장 가능)
