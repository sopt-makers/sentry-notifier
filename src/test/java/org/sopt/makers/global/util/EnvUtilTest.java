package org.sopt.makers.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.sopt.makers.global.exception.unchecked.InvalidEnvParameterException;
import org.sopt.makers.global.exception.unchecked.UnsupportedServiceTypeException;
import org.sopt.makers.global.exception.unchecked.WebhookUrlNotFoundException;

@DisplayName("EnvUtil 테스트")
class EnvUtilTest {

	private static final String ENV_FILE_PATH = "src/main/resources/.env";

	@BeforeAll
	static void setUp() throws IOException {
		String content = """
                SLACK_WEBHOOK_CREW_DEV_BE=https://hooks.slack.com/services/crew/dev/be
                SLACK_WEBHOOK_APP_PROD_FE=https://hooks.slack.com/services/app/prod/fe
                """;
		Files.createDirectories(Paths.get("src/main/resources"));
		Files.write(Paths.get(ENV_FILE_PATH), content.getBytes());
	}

	@AfterAll
	static void tearDown() throws IOException {
		Files.deleteIfExists(Paths.get(ENV_FILE_PATH));
	}

	@DisplayName("정상적인 키로부터 Webhook URL을 가져온다")
	@Test
	void testGetWebhookUrl_valid() {
		String expectedUrl = "https://hooks.slack.com/services/crew/dev/be";
		String actualUrl = EnvUtil.getWebhookUrl("slack", "crew", "dev", "be");

		assertEquals(expectedUrl, actualUrl);
	}

	@DisplayName("존재하지 않는 키로 Webhook URL 조회 시 예외를 발생시킨다")
	@Test
	void testGetWebhookUrl_notFound() {
		WebhookUrlNotFoundException exception = assertThrows(WebhookUrlNotFoundException.class, () ->
			EnvUtil.getWebhookUrl("slack", "crew", "prod", "be")
		);

		assertTrue(exception.getMessage().contains("Webhook URL을 찾을 수 없습니다."));
	}

	@DisplayName("지원하지 않는 서비스 타입 입력 시 예외를 발생시킨다")
	@Test
	void testGetWebhookUrl_unsupportedServiceType() {
		assertThrows(UnsupportedServiceTypeException.class, () ->
			EnvUtil.getWebhookUrl("telegram", "crew", "dev", "be")
		);
	}

	@DisplayName("null 값이 입력되면 InvalidEnvParameterException을 발생시킨다")
	@ParameterizedTest(name = "service={0}, team={1}, stage={2}, type={3}")
	@MethodSource("provideNullParameters")
	void testGetWebhookUrl_withNullParameters_shouldThrow(String service, String team, String stage, String type) {
		assertThrows(InvalidEnvParameterException.class, () ->
			EnvUtil.getWebhookUrl(service, team, stage, type)
		);
	}

	@DisplayName("대소문자 구분 없이 키를 인식할 수 있어야 한다")
	@ParameterizedTest(name = "service={0}, team={1}, stage={2}, type={3}")
	@CsvSource({
		"SLACK, CREW, DEV, BE",
		"slack, crew, dev, be",
		"SlAcK, CrEw, DeV, Be",
		"slack, APP, PROD, FE",
		"SLACK, app, prod, fe",
		"SlAcK, ApP, PrOd, Fe"
	})
	void testGetWebhookUrl_caseInsensitive(String service, String team, String stage, String type) {
		if (team.equalsIgnoreCase("app")) {
			String actualUrl = EnvUtil.getWebhookUrl(service, team, stage, type);
			assertEquals("https://hooks.slack.com/services/app/prod/fe", actualUrl);
			return;
		}

		String actualUrl = EnvUtil.getWebhookUrl(service, team, stage, type);
		assertEquals("https://hooks.slack.com/services/crew/dev/be", actualUrl);
	}
	private static Stream<Arguments> provideNullParameters() {
		return Stream.of(
			Arguments.of(null, "crew", "dev", "be"),
			Arguments.of("slack", null, "dev", "be"),
			Arguments.of("slack", "crew", null, "be"),
			Arguments.of("slack", "crew", "dev", null)
		);
	}
}
