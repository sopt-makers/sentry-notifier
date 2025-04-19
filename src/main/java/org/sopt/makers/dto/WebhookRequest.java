package org.sopt.makers.dto;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record WebhookRequest(
	String stage,
	String team,
	String type,
	String serviceType
) {
	public static WebhookRequest from(APIGatewayProxyRequestEvent input) {
		String stage = input.getRequestContext().getStage();
		Map<String, String> pathParameters = input.getPathParameters();
		if (pathParameters == null) {
			pathParameters = Map.of();
		}

		String team = pathParameters.get("team");
		String type = pathParameters.get("type");
		String serviceType = pathParameters.getOrDefault("service", "slack");

		return WebhookRequest.builder()
			.stage(stage)
			.team(team)
			.type(type)
			.serviceType(serviceType)
			.build();
	}
}
