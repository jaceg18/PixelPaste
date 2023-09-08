package github.jaceg18.pixelpaste.pixelpaste.commands.core;

import org.bukkit.command.CommandSender;

public interface Command {
    boolean execute(CommandSender sender, String[] args);
}
