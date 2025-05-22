package org.sopt.makers.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.config.ObjectMapperConfig;
import org.sopt.makers.handler.FakeSentryPayload;

import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("SentryEventExtractorService 테스트")
class SentryEventExtractorServiceTest {
	private final ObjectMapper objectMapper = ObjectMapperConfig.getInstance();
	private final SentryEventExtractorService extractorService = new SentryEventExtractorService(objectMapper);

	@Test
	@DisplayName("실제 Sentry 웹훅 데이터에서 이벤트 정보를 정확히 추출하는지 테스트")
	void extractSentryEventFromRealPayload() {
		// Given
		String payload = FakeSentryPayload.REAL_SENTRY_WEBHOOK_PAYLOAD;

		// When
		SentryEventDetail result = extractorService.extractEvent(payload);

		// Then
		assertNotNull(result);
		assertEquals("0000000000", result.issueId());
		assertEquals(
			"https://sentry.io/organizations/example-org/issues/0000000000/events/93153fe001674c70b39af22c49db350b/",
			result.webUrl());
		assertEquals("Authentication failed, token expired!", result.message());
		assertEquals("2025-05-21T08:52:45.182000Z", result.datetime());
		assertEquals("error", result.level());
		assertEquals("ApiException: Authentication failed, token expired!", result.title());
	}
}

