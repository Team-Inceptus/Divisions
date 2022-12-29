package us.teaminceptus.divisions;

import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class DivCommands {

    private final Divisions plugin;

    private static BukkitCommandHandler handler;

    DivCommands(Divisions plugin) {
        this.plugin = plugin;
        if (handler != null) return;

        handler = BukkitCommandHandler.create(plugin);

        handler.register(this);
        handler.registerBrigadier();
        
        handler.setLocale(plugin.getLocale());
    }

}
