package org.sopt.makers.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.config.ObjectMapperConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

@DisplayName("SlackNotificationService 테스트")
class SlackNotificationServiceTest {
	private static final String SLACK_WEBHOOK_PATH = "/services/slack";
	private static final String DUMMY_SENTRY_WEB_URL =
		"https://sentry.example.com/organizations/example-org/issues/0000000000/events/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/";

	private final ObjectMapper objectMapper = ObjectMapperConfig.getInstance();

	private WireMockServer wireMockServer;
	private SlackNotificationService slackNotificationService;

	@BeforeEach
	void setUp() {
		wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
		wireMockServer.start();
		wireMockServer.stubFor(post(urlPathEqualTo(SLACK_WEBHOOK_PATH))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "text/plain")
				.withBody("ok")));

		slackNotificationService = new SlackNotificationService(objectMapper);
	}

	@AfterEach
	void tearDown() {
		if (wireMockServer != null) {
			wireMockServer.stop();
		}
	}

	@Test
	@DisplayName("상세 보기 링크는 Slack 버튼이 아닌 mrkdwn 링크로 전송한다")
	void sendNotification_shouldRenderIssueLinkAsMarkdownText() throws Exception {
		// Given
		SentryEventDetail eventDetail = sentryEventDetail();

		// When
		slackNotificationService.sendNotification("beat", "be", "dev", eventDetail, slackWebhookUrl());

		// Then
		wireMockServer.verify(1, postRequestedFor(urlPathEqualTo(SLACK_WEBHOOK_PATH)));

		JsonNode blocks = extractBlocksFromSlackRequest();
		JsonNode issueLinkBlock = blocks.get(blocks.size() - 1);

		assertThat(blockTypes(blocks)).doesNotContain("actions");
		assertThat(containsButtonElement(blocks)).isFalse();
		assertThat(issueLinkBlock.path("type").asText()).isEqualTo("section");
		assertThat(issueLinkBlock.path("text").path("type").asText()).isEqualTo("mrkdwn");
		assertThat(issueLinkBlock.path("text").path("text").asText())
			.isEqualTo("<%s|상세 보기>".formatted(DUMMY_SENTRY_WEB_URL));
	}

	private JsonNode extractBlocksFromSlackRequest() throws Exception {
		String requestBody = wireMockServer.getAllServeEvents().get(0).getRequest().getBodyAsString();
		return objectMapper.readTree(requestBody)
			.path("attachments")
			.path(0)
			.path("blocks");
	}

	private String slackWebhookUrl() {
		return "http://localhost:%d%s".formatted(wireMockServer.port(), SLACK_WEBHOOK_PATH);
	}

	private static SentryEventDetail sentryEventDetail() {
		return new SentryEventDetail(
			"0000000000",
			DUMMY_SENTRY_WEB_URL,
			"Unexpected server error: ",
			"2026-05-13T17:58:41.158000Z",
			"error",
			"Exception: Sentry test exception"
		);
	}

	private static List<String> blockTypes(JsonNode blocks) {
		List<String> blockTypes = new ArrayList<>();
		blocks.forEach(block -> blockTypes.add(block.path("type").asText()));
		return blockTypes;
	}

	private static boolean containsButtonElement(JsonNode node) {
		if (node.isObject() && "button".equals(node.path("type").asText())) {
			return true;
		}

		for (JsonNode child : node) {
			if (containsButtonElement(child)) {
				return true;
			}
		}

		return false;
	}
}
