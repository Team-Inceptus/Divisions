package us.teaminceptus.divisions;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class DivCommands {

    private final Divisions plugin;

    private static BukkitCommandHandler handler;

    DivCommands(Divisions plugin) {
        this.plugin = plugin;
        if (handler != null) return;

        handler = BukkitCommandHandler.create(plugin);

        handler.register(this);
        handler.register(new DivisionCommands());
        handler.registerBrigadier();
        
        handler.setLocale(plugin.getLocale());
    }

    // Division Commmands

    @Command({"division", "d"})
    @Description("Base command for divisions")
    @Usage("/division <cmd>")
    public static final class DivisionCommands {

        @Subcommand("create")
        public void create(Player p) {
            // TODO
        }

    }


}
