package us.teaminceptus.divisions.api.division;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class TestSocialMedia {

    @Test
    @DisplayName("Test Link Scheme")
    public void testLinkScheme() {
        Assertions.assertTrue(SocialMedia.DISCORD.isValidLink("https://discord.com/invite/"));
        Assertions.assertTrue(SocialMedia.DISCORD.isValidLink("discord.gg/"));
        Assertions.assertTrue(SocialMedia.DISCORD.isValidLink("http://discordapp.com/invite/"));
        Assertions.assertFalse(SocialMedia.DISCORD.isValidLink("https://youtube.com"));

        Assertions.assertTrue(SocialMedia.YOUTUBE.isValidLink("https://youtu.be/"));
        Assertions.assertTrue(SocialMedia.YOUTUBE.isValidLink("http://youtube.co/"));
        Assertions.assertFalse(SocialMedia.YOUTUBE.isValidLink("discord.com/invite/"));

        Assertions.assertTrue(SocialMedia.GITHUB.isValidLink("github.com/"));
        Assertions.assertFalse(SocialMedia.GITHUB.isValidLink("http://instagra.com"));

        Assertions.assertTrue(SocialMedia.INSTAGRAM.isValidLink("https://instagr.am/"));
        Assertions.assertTrue(SocialMedia.INSTAGRAM.isValidLink("instagram.com/"));
        Assertions.assertFalse(SocialMedia.INSTAGRAM.isValidLink("youtube.co"));

        Assertions.assertTrue(SocialMedia.TIKTOK.isValidLink("https://tiktok.com/EE"));
        Assertions.assertTrue(SocialMedia.TIKTOK.isValidLink("tiktok.com"));
        Assertions.assertFalse(SocialMedia.TIKTOK.isValidLink("discord.gg"));

        Assertions.assertTrue(SocialMedia.TWITTER.isValidLink("https://twitter.com/"));
        Assertions.assertTrue(SocialMedia.TWITTER.isValidLink("twitter.com//"));
        Assertions.assertFalse(SocialMedia.TWITTER.isValidLink("github.com/ABCDEFG"));

        Assertions.assertTrue(SocialMedia.FACEBOOK.isValidLink("https://facebook.com/"));
        Assertions.assertTrue(SocialMedia.FACEBOOK.isValidLink("http://facebook.com"));
        Assertions.assertFalse(SocialMedia.FACEBOOK.isValidLink("instagram.com"));
    }

}
