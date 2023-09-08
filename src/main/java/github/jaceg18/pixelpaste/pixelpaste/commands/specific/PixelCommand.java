package github.jaceg18.pixelpaste.pixelpaste.commands.specific;

import github.jaceg18.pixelpaste.pixelpaste.commands.core.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PixelCommand extends BaseCommand {

    private final boolean is2D;

    public PixelCommand(boolean is2D){
        this.is2D = is2D;
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;
        if (!player.isOp() && !player.hasPermission("pixelpaste")) {
            sender.sendMessage("You don't have permission to use this command.");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage("You must provide an image filename.");
            return false;
        }

        int maxDimension = parseDimension(args, 1, 100);
        int maxDepth = is2D ? 0 : parseDimension(args, 3, 5);
        String orientation = is2D ? "horz" : args[1].toLowerCase();

        if (!is2D && !("vert".equals(orientation) || "horz".equals(orientation))) {
            player.sendMessage("Invalid orientation. Use 'vert' for vertical or 'horz' for horizontal.");
            return false;
        }

        processImageAsync(player, args[0], maxDimension, maxDepth, orientation, is2D);
        return true;
    }
}
