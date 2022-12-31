package us.teaminceptus.divisions.api.division;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents Social Media Platforms for a {@link Division}.
 * @since 1.0.0
 */
public enum SocialMedia {

    /**
     * Represents the GitHub platform.
     * @since 1.0.0
     */
    GITHUB("github.dev"),

    /**
     * Represents the Discord Platform.
     * @since 1.0.0
     */
    DISCORD("discord.gg", "discordapp.com"),

    /**
     * Represents the YouTube Platform.
     * @since 1.0.0
     */
    YOUTUBE("youtu.be", "youtube.co"),

    /**
     * Represents the Instagram Platform.
     * @since 1.0.0
     */
    INSTAGRAM("instagr.am"),

    /**
     * Represents the TikTok Platform.
     * @since 1.0.0
     */
    TIKTOK,

    /**
     * Represents the Twitter Platform.
     * @since 1.0.0
     */
    TWITTER,

    /**
     * Represents the Facebook (Meta) Platform.
     * @since 1.0.0
     */
    FACEBOOK("meta.com"),

    ;

    final List<String> urls = new ArrayList<>();

    SocialMedia(String... urls) {
        this.urls.addAll(Arrays.asList(urls));
        this.urls.add(this.name().toLowerCase() + ".com");
    }

    /**
     * Determines whether the link inputted matches the platform.
     * @param link The link to check.
     * @return true if link matches platform, false otherwise.
     * @since 1.0.0
     */
    public boolean isValidLink(@Nullable String link) {
        if (link == null) return false;

        String tLink = link.startsWith("https://") ? link.substring(8) : link.startsWith("http://") ? link.substring(7) : link;
        String domain = tLink.contains("/") ? tLink.split("/")[0] : tLink;

        String[] domainSplit = domain.split("\\.");
        String rootDomain = domainSplit[domainSplit.length - 2] + "." + domainSplit[domainSplit.length - 1];

        return urls.contains(rootDomain);
    }

}
