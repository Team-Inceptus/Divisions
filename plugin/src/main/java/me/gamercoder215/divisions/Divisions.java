package me.gamercoder215.divisions;

import me.gamercoder215.divisions.api.DivisionsConfig;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

public final class Divisions extends JavaPlugin implements DivisionsConfig {

    private static Logger LOGGER;

    private static boolean adventureEnabled = false;

    private void checkPaper() {
        try {
            Class.forName("net.kyori.adventure.text.Component");

            getLogger().config("Adventure API Detected! Loading...");
            Constructor<?> loader = Class.forName("me.gamercoder215.divisions.AdventureLoader").getConstructor(Plugin.class);
            loader.newInstance(this);

            adventureEnabled = true;
        } catch (ClassNotFoundException ignored) {
        } catch (ReflectiveOperationException e) {
            print(e);
        }
    }

    public static void print(@NotNull Throwable e) {
        LOGGER.info(e.getClass().getSimpleName());
        LOGGER.info("----------------------------------------");
        LOGGER.info(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) LOGGER.info(element.toString());
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();

        getLogger().info("Done!");
    }

    @Override
    public boolean isAdventureEnabled() {
        return adventureEnabled;
    }
}
