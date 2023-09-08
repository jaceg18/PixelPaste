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
            return on2DCommand(sender, args);
        } else
            if (name.equals("p3d")){
            return on3DCommand(sender, args);
        }

            return false;
    }
    /**
     * Handles the 2D pixel art command.
     *
     * @param sender The sender of the command.
     * @param args Arguments provided with the command.
     * @return true if the command was successfully handled; false otherwise.
     */
    private boolean on2DCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Your not a player.");
            return false;
        }

        Player player = (Player) sender;

        Location initialLocation = player.getLocation().clone();

        // Check for proper permissions
        if (player.isOp() || player.hasPermission("pixelpaste")) {

            // Check for filename argument
            if (args.length < 1) {
                player.sendMessage("You must provide an image filename.");
                return false;
            }

            int max_dimension = 100;
            try {
                max_dimension = Integer.parseInt(args[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored){
                player.sendMessage("Invalid dimension, using default value of 10.");
            }

            // Load the image asynchronously
            int finalMax_dimension = max_dimension;

            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                // Initialize variables
                BufferedImage image;
                try {
                    image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + args[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                image = ImageUtil.resizeImage2D(image, finalMax_dimension);

                BukkitRunnable runnable = pixelBuilder.process2D(image, player);

                BufferedImage finalImage = image;
                Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                    // Add the runnable to pending confirmations
                    PendingConfirmation.addConfirmation(player, runnable);

                    // Highlight the area
                    BlockManager.highlightArea(player, finalImage, initialLocation, "horz");

                    // Notify the player to confirm
                    player.sendMessage(ChatColor.GOLD + "PixelPaste: Area highlighted. Type /ppconfirm to proceed with building, /ppcancel to cancel");
                });
            });

        }
        return true;
    }
    /**
     * Handles the 3D pixel art command.
     *
     * @param sender The sender of the command.
     * @param args Arguments provided with the command.
     * @return true if the command was successfully handled; false otherwise.
     */
    private boolean on3DCommand(CommandSender sender, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        Location initialLocation = player.getLocation().clone();

        if (player.isOp() || player.hasPermission("pixelpaste")) {
            if (args.length < 1) {
                player.sendMessage("You must provide an image filename.");
                return false;
            }
            String orientation = args[1].toLowerCase();
            if (!orientation.equals("vert") && !orientation.equals("horz")) {
                player.sendMessage(ChatColor.RED + "PixelPaste: Invalid orientation. Use 'vert' for vertical or 'horz' for horizontal.");
                return false;
            }

            int max_dimension = 100;
            try {
                max_dimension = Integer.parseInt(args[2]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored){
                player.sendMessage("Invalid dimension, using default value of 10.");
            }

            int finalMax_dimension = max_dimension;

            int max_depth = 5;
            try {
                max_depth = Integer.parseInt(args[3]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored){
                player.sendMessage("Invalid depth, using default value of 10.");
            }


            int finalMax_depth = max_depth;
            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                // Initialize variables
                BufferedImage image;
                try {
                    image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + args[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }



                image = ImageUtil.resizeImage3D(image, finalMax_dimension);

                BukkitRunnable runnable = pixelBuilder.process3D(player, image, orientation, finalMax_depth);

                BufferedImage finalImage = image;
                Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                    // Add the runnable to pending confirmations
                    PendingConfirmation.addConfirmation(player, runnable);

                    // Highlight the area
                    BlockManager.highlightArea(player, finalImage, initialLocation, orientation);

                    // Notify the player to confirm
                    player.sendMessage(ChatColor.GOLD + "PixelPaste: Area highlighted. Type 'ppconfirm' to proceed with building, 'ppcancel' to cancel");
                });
            });
        }
        return true;
    }

}
