package org.sopt.makers.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SentryEventDetail(
	String issueId,
	String webUrl,
	String message,
	String datetime,
	String level,
	String title
) {
	public static SentryEventDetail from(JsonNode eventNode) {
		return SentryEventDetail.builder()
			.issueId(eventNode.path("issue_id").asText())
			.webUrl(eventNode.path("web_url").asText())
			.message(eventNode.path("message").asText())
			.datetime(eventNode.path("datetime").asText())
			.level(eventNode.path("level").asText())
			.title(eventNode.path("title").asText())
			.build();
	}
}
