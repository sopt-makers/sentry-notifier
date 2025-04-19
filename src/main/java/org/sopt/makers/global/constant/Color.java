package org.sopt.makers.global.constant;

import java.util.Arrays;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
	RED("#FF0000", Set.of("fatal", "critical")),
	ORANGE("#E36209", Set.of("error")),
	YELLOW("#FFCC00", Set.of("warning")),
	GREEN("#36A64F", Set.of("info")),
	BLUE("#87CEFA", Set.of("debug")),
	GRAY("#AAAAAA", Set.of());

	private final String value;
	private final Set<String> levels;

	public static String getColorByLevel(String level) {
		if (level == null || level.trim().isEmpty()) {
			return GRAY.value;
		}

		String normalized = level.trim().toLowerCase();
		return Arrays.stream(Color.values())
			.filter(color -> color.levels.contains(normalized))
			.map(Color::getValue)
			.findFirst()
			.orElse(GRAY.value);
	}
}
