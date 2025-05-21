package org.sopt.makers.vo.discord.message;

import java.util.List;

import org.sopt.makers.vo.discord.embed.DiscordEmbed;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiscordMessage(
	String username,
	@JsonProperty("avatar_url")
	String avatarUrl,
	List<DiscordEmbed> embeds
) {
	public static DiscordMessage newInstance(String username, String avatarUrl, List<DiscordEmbed> embeds) {
		return new DiscordMessage(username, avatarUrl, embeds);
	}
}
