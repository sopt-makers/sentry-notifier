package org.sopt.makers.global.util;

import static org.sopt.makers.global.constant.SlackConstant.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.sopt.makers.global.exception.message.ErrorMessage;
import org.sopt.makers.global.exception.unchecked.HttpRequestException;

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
	public static HttpResponse<String> sendPost(String url, String contentType, String body) throws HttpRequestException {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(HEADER_CONTENT_TYPE, contentType)
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
				.build();

			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				log.error("[HTTP 오류 응답] URL: {}, Status: {}", url, response.statusCode());
				throw HttpRequestException.from(ErrorMessage.SLACK_NETWORK_ERROR);
			}

			return response;
		} catch (IOException e) {
			log.error("[HTTP 네트워크 오류] URL: {}, Error: {}", url, e.getMessage());
			throw HttpRequestException.from(ErrorMessage.SLACK_NETWORK_ERROR);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("[HTTP 요청 중단] URL: {}, Error: {}", url, e.getMessage());
			throw HttpRequestException.from(ErrorMessage.SLACK_SEND_INTERRUPTED);
		} catch (Exception e) {
			log.error("[HTTP 예상치 못한 오류] URL: {}, Error: {}", url, e.getMessage());
			throw HttpRequestException.from(ErrorMessage.SLACK_SEND_FAILED);
		}
	}
}
