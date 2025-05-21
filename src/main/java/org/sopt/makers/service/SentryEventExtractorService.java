package org.sopt.makers.service;

import org.sopt.makers.dto.SentryEventDetail;
import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.InvalidSlackPayloadException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SentryEventExtractorService {
	private final ObjectMapper objectMapper;

	public SentryEventDetail extractEvent(String requestBody) {
		JsonNode rootNode = parseRequestBody(requestBody);
		JsonNode eventNode = rootNode.path("data").path("event");

		log.info("rootNode 정보: {}", rootNode);
		log.info("eventNode 정보: {}", eventNode);

		if (eventNode.isMissingNode() || eventNode.isEmpty()) {
			log.error("[이벤트 데이터 누락] 요청 본문에 필수 이벤트 정보가 없습니다");
			throw InvalidSlackPayloadException.from(ErrorMessage.INVALID_SLACK_PAYLOAD);
		}

		SentryEventDetail sentryEvent = SentryEventDetail.from(eventNode);
		log.info("[이벤트 추출] issueId={}, level={}", sentryEvent.issueId(), sentryEvent.level());

		return sentryEvent;
	}

	private JsonNode parseRequestBody(String requestBody) {
		try {
			return objectMapper.readTree(requestBody);
		} catch (Exception e) {
			log.error("[요청 본문 파싱 실패] error={}", e.getMessage(), e);
			throw InvalidSlackPayloadException.from(ErrorMessage.INVALID_SLACK_PAYLOAD);
		}
	}
}
