package github.jaceg18.pixelpaste.pixelpaste.commands.handlers;

import github.jaceg18.pixelpaste.pixelpaste.commands.specific.PixelCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


/**
 * Handles commands for the PixelPaste plugin.
 */
public class CommandHandler {
    private final PixelCommand p2dCommand;
    private final PixelCommand p3dCommand;

    public CommandHandler() {
        this.p2dCommand = new PixelCommand(true);
        this.p3dCommand = new PixelCommand(false);
    }

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        String name = command.getName().toLowerCase();
        if ("p2d".equals(name)) {
            return p2dCommand.execute(sender, args);
        } else if ("p3d".equals(name)) {
            return p3dCommand.execute(sender, args);
        }
        return false;
    }
}