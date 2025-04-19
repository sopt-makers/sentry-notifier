package org.sopt.makers.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sopt.makers.global.exception.unchecked.WebhookUrlNotFoundException;

class EnvUtilTest {

	private static final String ENV_FILE_PATH = "src/main/resources/.env";

	@BeforeAll
	static void setUp() throws IOException {
		// .env 파일을 테스트 전에 생성
		String content = """
                SLACK_WEBHOOK_CREW_DEV_BE=https://hooks.slack.com/services/crew/dev/be
                SLACK_WEBHOOK_APP_PROD_FE=https://hooks.slack.com/services/app/frod/fe
                """;
		Files.createDirectories(Paths.get("src/main/resources"));
		Files.write(Paths.get(ENV_FILE_PATH), content.getBytes());
	}

	@AfterAll
	static void tearDown() throws IOException {
		// 테스트 후 .env 파일 삭제
		Files.deleteIfExists(Paths.get(ENV_FILE_PATH));
	}

	@Test
	void testGetWebhookUrl_valid() {
		String expectedUrl = "https://hooks.slack.com/services/crew/dev/be";
		String actualUrl = EnvUtil.getWebhookUrl("slack", "crew", "dev", "be");

		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	void testGetWebhookUrl_notFound() {
		assertThrows(WebhookUrlNotFoundException.class, () ->
			EnvUtil.getWebhookUrl("slack", "crew", "prod", "be")
		);
	}
}
