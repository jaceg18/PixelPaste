package github.jaceg18.pixelpaste.pixelpaste.commands;

import github.jaceg18.pixelpaste.PixelPaste;
import github.jaceg18.pixelpaste.pixelpaste.logic.BlockManager;
import github.jaceg18.pixelpaste.pixelpaste.logic.PixelBuilder;
import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import github.jaceg18.pixelpaste.pixelpaste.utility.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles commands for the PixelPaste plugin.
 */
public class CommandHandler {

    private final PixelBuilder pixelBuilder = new PixelBuilder();
    /**
     * Entry point for handling PixelPaste commands.
     *
     * @param sender The sender of the command.
     * @param command The command to be executed.
     * @param label The alias of the command.
     * @param args Arguments provided with the command.
     * @return true if the command was successfully handled; false otherwise.
     */
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args){
        String name = command.getName().toLowerCase();
        if (name.equals("p2d")){
            return handleCommand(sender, args, true);
        } else
            if (name.equals("p3d")){
            return handleCommand(sender, args, false);
        }

            return false;
    }

    /**
     * Handles the 2D/3D image processing and commands
     * @param sender The command sender
     * @param args The command line arguments
     * @param is2D Is the build 2D?
     * @return A boolean whether the command was successful
     */
    private boolean handleCommand(CommandSender sender, String[] args, boolean is2D) {
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

    /**
     * Parses dimensions while keeping error checking in mind
     * @param args The commandline arguments
     * @param index The given arg index
     * @param defaultValue The default value incase of failure
     * @return the parsed value
     */
    private int parseDimension(String[] args, int index, int defaultValue) {
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            return defaultValue;
        }
    }

    /**
     * Processes the images
     * @param player The player processing the image
     * @param filename The filename of the image
     * @param maxDimension The max dimension of the image
     * @param maxDepth The max depth of the image
     * @param orientation The orientation of the image
     * @param is2D is the image 2D?
     */
    private void processImageAsync(Player player, String filename, int maxDimension, int maxDepth, String orientation, boolean is2D) {
        Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
            BufferedImage image;
            try {
                image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + filename));
                image = is2D ? ImageUtil.resizeImage2D(image, maxDimension) : ImageUtil.resizeImage3D(image, maxDimension);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            BukkitRunnable runnable = is2D ? pixelBuilder.process2D(image, player) : pixelBuilder.process3D(player, image, orientation, maxDepth);
            PendingConfirmation.addConfirmation(player, runnable);

            BufferedImage finalImage = image;
            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                BlockManager.highlightArea(player, finalImage, player.getLocation().clone(), orientation);
                player.sendMessage("Area highlighted. Type 'ppconfirm' to proceed with building, 'ppcancel' to cancel.");
            });
        });
    }

}
