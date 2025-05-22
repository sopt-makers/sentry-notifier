package org.sopt.makers.handler;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;
import static org.assertj.core.api.Assertions.*;
import static org.sopt.makers.handler.FakeSentryPayload.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.makers.global.util.EnvUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;

import io.github.cdimascio.dotenv.Dotenv;

@ExtendWith(MockitoExtension.class)
@DisplayName("SentryWebhookHandler 테스트")
class SentryWebhookHandlerTest {
	private static final String ENV_FILE_PATH = "src/test/resources/.env";

	@Mock
	private Context lambdaContext;

	private WireMockServer wireMockServer;
	private SentryWebhookHandler handler;

	@BeforeAll
	static void setUpEnvironment() throws IOException {
		String content = """
            SLACK_WEBHOOK_CREW_DEV_BE=http://localhost:8089/services/crew/dev/be
            SLACK_WEBHOOK_APP_PROD_FE=http://localhost:8089/services/app/prod/fe
            DISCORD_WEBHOOK_CREW_PROD_BE=http://localhost:8089/api/webhooks/123456789/crew-prod-be-token
            DISCORD_WEBHOOK_APP_DEV_FE=http://localhost:8089/api/webhooks/987654321/app-dev-fe-token
            """;
		Files.createDirectories(Paths.get("src/test/resources"));
		Files.write(Paths.get(ENV_FILE_PATH), content.getBytes());

		Dotenv testDotenv = Dotenv.configure().directory("src/test/resources").load();
		EnvUtil.setDotenv(testDotenv);
	}

	@AfterAll
	static void tearDownEnvironment() throws IOException {
		Files.deleteIfExists(Paths.get(ENV_FILE_PATH));
	}

	@BeforeEach
	void setUp() {
		wireMockServer = new WireMockServer(wireMockConfig().port(8089).notifier(new ConsoleNotifier(false)));
		wireMockServer.start();

		handler = new SentryWebhookHandler();
		setupDefaultApiResponses();
	}

	@AfterEach
	void tearDown() {
		if (wireMockServer != null) {
			wireMockServer.stop();
		}
	}

	@Nested
	@DisplayName("Discord 알림 테스트")
	class DiscordNotificationTests {

		@Test
		@DisplayName("CREW PROD BE 환경으로 Discord 알림 전송 성공")
		void shouldSendDiscordNotificationToCrewProdBe() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);

			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/api/webhooks/123456789/crew-prod-be-token"))
					.withHeader("Content-Type", equalTo("application/json"))
					.withRequestBody(containing("embeds")));

			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/services/.*")));
		}

		@Test
		@DisplayName("APP DEV FE 환경으로 Discord 알림 전송 성공")
		void shouldSendDiscordNotificationToAppDevFe() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("dev", "app", "fe", "discord");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);

			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/api/webhooks/987654321/app-dev-fe-token"))
					.withHeader("Content-Type", equalTo("application/json"))
					.withRequestBody(containing("embeds")));

			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/services/.*")));
		}

		@Test
		@DisplayName("Discord API 404 에러 응답 처리")
		void shouldHandleDiscordApiNotFoundError() {
			// Given
			wireMockServer.resetMappings();
			setupDefaultApiResponses();
			wireMockServer.stubFor(
				post(urlPathMatching("/api/webhooks/.*"))
					.willReturn(aResponse().withStatus(404)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"message\": \"Unknown Webhook\", \"code\": 10015}"))
			);

			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(502);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("Discord API Rate Limiting 처리")
		void shouldHandleDiscordRateLimiting() {
			// Given
			wireMockServer.resetMappings();
			setupDefaultApiResponses();
			wireMockServer.stubFor(
				post(urlPathMatching("/api/webhooks/.*"))
					.willReturn(aResponse().withStatus(429)
						.withHeader("Retry-After", "5")
						.withBody("{\"message\": \"You are being rate limited.\", \"retry_after\": 5000}"))
			);

			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(502);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("Critical 레벨 Sentry 이벤트 처리")
		void shouldHandleCriticalSentryEventWithDiscord() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "app", "fe", "discord", "fatal");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);
			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/api/webhooks/.*"))
					.withRequestBody(containing("embeds")));
		}

		@Test
		@DisplayName("Warning 레벨 Sentry 이벤트 처리")
		void shouldHandleWarningSentryEventWithDiscord() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "app", "fe", "discord", "warning");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("Info 레벨 Sentry 이벤트 처리")
		void shouldHandleInfoSentryEventWithDiscord() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "app", "fe", "discord", "info");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}
	}

	@Nested
	@DisplayName("Slack 알림 테스트")
	class SlackNotificationTests {

		@Test
		@DisplayName("CREW DEV BE 환경으로 Slack 알림 전송 성공")
		void shouldSendSlackNotificationToCrewDevBe() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("dev", "crew", "be", "slack");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);

			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/services/crew/dev/be"))
					.withHeader("Content-Type", equalTo("application/json"))
					.withRequestBody(containing("text")) // Slack은 "text" 필드 사용
			);

			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("APP PROD FE 환경으로 Slack 알림 전송 성공")
		void shouldSendSlackNotificationToAppProdFe() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "app", "fe", "slack");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);

			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/services/app/prod/fe"))
					.withHeader("Content-Type", equalTo("application/json"))
					.withRequestBody(containing("text")));

			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("Slack API 에러 응답 처리")
		void shouldHandleSlackApiError() {
			// Given - Slack API 에러 응답 설정
			wireMockServer.resetMappings();
			setupDefaultApiResponses();
			wireMockServer.stubFor(
				post(urlPathMatching("/services/.*"))
					.willReturn(aResponse().withStatus(400)
						.withHeader("Content-Type", "text/plain")
						.withBody("invalid_payload"))
			);

			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("dev", "crew", "be", "slack");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(502);

			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/services/.*")));
		}

		@Test
		@DisplayName("네트워크 타임아웃 시나리오")
		void shouldHandleNetworkTimeout() {
			// Given
			wireMockServer.resetMappings();
			setupDefaultApiResponses();
			wireMockServer.stubFor(
				post(urlPathMatching("/api/webhooks/.*"))
					.willReturn(aResponse().withStatus(200).withFixedDelay(10000)) // 10초 지연
			);

			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");

			// When & Then
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);
			assertThat(response.getStatusCode()).isEqualTo(503);
		}

		@Test
		@DisplayName("Slack API Rate Limiting 처리")
		void shouldHandleSlackRateLimiting() {
			// Given - Slack Rate Limiting 응답 설정
			wireMockServer.resetMappings();
			setupDefaultApiResponses();
			wireMockServer.stubFor(
				post(urlPathMatching("/services/.*"))
					.willReturn(aResponse().withStatus(429)
						.withHeader("Retry-After", "1")
						.withBody("rate_limited"))
			);

			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("dev", "crew", "be", "slack");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(502);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/services/.*")));
		}

		@Test
		@DisplayName("Critical 레벨 Sentry 이벤트 처리")
		void shouldHandleCriticalSentryEventWithSlack() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "crew", "be", "slack", "fatal");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);

			wireMockServer.verify(1,
				postRequestedFor(urlPathMatching("/services/.*"))
					.withRequestBody(containing("attachments")));

			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}


		@Test
		@DisplayName("Warning 레벨 Sentry 이벤트 처리")
		void shouldHandleWarningSentryEventWithSlack() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "crew", "be", "slack", "warning");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/services/.*")));
			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}

		@Test
		@DisplayName("Info 레벨 Sentry 이벤트 처리")
		void shouldHandleInfoSentryEventWithSlack() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEventWithLevel("dev", "crew", "be", "slack", "info");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(200);
			wireMockServer.verify(1, postRequestedFor(urlPathMatching("/services/.*")));
			wireMockServer.verify(0, postRequestedFor(urlPathMatching("/api/webhooks/.*")));
		}
	}

	@Nested
	@DisplayName("에러 처리 테스트")
	class ErrorHandlingTests {

		@Test
		@DisplayName("잘못된 환경 파라미터 처리")
		void shouldHandleInvalidEnvironmentParameters() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("invalid", "invalid", "invalid", "discord");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(404);
		}

		@Test
		@DisplayName("PathParameters가 없는 경우 처리")
		void shouldHandleMissingPathParameters() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");
			apiEvent.setPathParameters(null);

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(400);
		}

		@Test
		@DisplayName("잘못된 JSON 페이로드 처리")
		void shouldHandleInvalidJsonPayload() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");
			apiEvent.setBody("{invalid json}");

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(400);
		}

		@Test
		@DisplayName("빈 페이로드 처리")
		void shouldHandleEmptyPayload() {
			// Given
			APIGatewayProxyRequestEvent apiEvent = createSentryWebhookEvent("prod", "crew", "be", "discord");
			apiEvent.setBody(null);

			// When
			APIGatewayProxyResponseEvent response = handler.handleRequest(apiEvent, lambdaContext);

			// Then
			assertThat(response.getStatusCode()).isEqualTo(400);
		}
	}

	private APIGatewayProxyRequestEvent createSentryWebhookEvent(String env, String team, String type, String service) {
		APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
		event.setPath("/webhook");
		event.setHttpMethod("POST");

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		event.setHeaders(headers);

		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("team", team);
		pathParams.put("type", type);
		pathParams.put("service", service);
		event.setPathParameters(pathParams);

		event.setBody(REAL_SENTRY_WEBHOOK_PAYLOAD);

		APIGatewayProxyRequestEvent.ProxyRequestContext context = new APIGatewayProxyRequestEvent.ProxyRequestContext();
		context.setStage(env);
		event.setRequestContext(context);

		return event;
	}

	private APIGatewayProxyRequestEvent createSentryWebhookEventWithLevel(String env, String team, String type, String service, String level) {
		APIGatewayProxyRequestEvent event = createSentryWebhookEvent(env, team, type, service);

		String payload = event.getBody().replace("\"level\": \"error\"", "\"level\": \"" + level + "\"");
		event.setBody(payload);

		return event;
	}

	private void setupDefaultApiResponses() {
		wireMockServer.stubFor(
			post(urlPathMatching("/api/webhooks/[0-9]+/.*"))
				.willReturn(aResponse().withStatus(204)
					.withHeader("Content-Type", "application/json")
					.withBody("{\"id\": \"123456789\", \"type\": 1}"))
		);

		wireMockServer.stubFor(
			post(urlPathMatching("/services/.*"))
				.willReturn(aResponse().withStatus(200)
					.withHeader("Content-Type", "text/plain")
					.withBody("ok"))
		);
	}
}
