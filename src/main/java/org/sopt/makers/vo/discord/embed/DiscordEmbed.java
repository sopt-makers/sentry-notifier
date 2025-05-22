package org.sopt.makers.vo.discord.embed;

import java.util.List;

public record DiscordEmbed(
	String title,
	String description,
	String url,
	Integer color,
	List<DiscordEmbedField> fields
) {
	public static DiscordEmbed newInstance(String title, String description, String url, Integer color,
		List<DiscordEmbedField> fields) {
		return new DiscordEmbed(title, description, url, color, fields);
	}

	public record DiscordEmbedField(
		String name,
		String value,
		boolean inline
	) {
		public static DiscordEmbedField newInstance(String name, String value, boolean inline) {
			return new DiscordEmbedField(name, value, inline);
		}
	}
}
