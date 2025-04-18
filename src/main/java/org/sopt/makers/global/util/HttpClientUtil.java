package org.sopt.makers.global.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientUtil {
	private static final int TIMEOUT_SECONDS = 10;
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
		.build();

	/**
	 * POST 요청 보내기
	 */
	public static HttpResponse<String> sendPost(String url, String contentType, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Content-Type", contentType)
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
			.build();

		return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
