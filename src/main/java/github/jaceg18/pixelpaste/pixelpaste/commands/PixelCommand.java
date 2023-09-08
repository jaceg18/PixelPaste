package github.jaceg18.pixelpaste.pixelpaste.commands;

import github.jaceg18.pixelpaste.pixelpaste.PixelPaste;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static github.jaceg18.pixelpaste.pixelpaste.PixelPaste.getColorBlock;

public class PixelCommand implements CommandExecutor, TabCompleter {

    private File folder;
    /**
     * Constructor to initialize the folder where image files are located.
     *
     * @param folder The directory containing image files.
     */
    public PixelCommand(File folder){
        this.folder = folder;
    }


    /**
     * Main command execution logic. Responsible for handling player input and triggering image rendering.
     *
     * @param sender The person who sent the command.
     * @param command The command object.
     * @param label The alias of the command.
     * @param args Arguments passed along with the command.
     * @return true if the command is valid, otherwise false.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        // Check for proper permissions
        if (player.isOp() || player.hasPermission("pixelpaste")) {

            // Check for filename argument
            if (args.length < 1) {
                player.sendMessage("You must provide an image filename.");
                return false;
            }

            // Load the image asynchronously
            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                // Initialize variables
                BufferedImage image;
                try {
                    image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + args[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                // Resize the image if necessary
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                if (imageWidth > 100 || imageHeight > 100) {
                    image = resizeImage(image, imageWidth, imageHeight);
                    imageWidth = image.getWidth();
                    imageHeight = image.getHeight();
                }

                // Get the player's location
                int startX = player.getLocation().getBlockX();
                int startY = player.getLocation().getBlockY();
                int startZ = player.getLocation().getBlockZ();

                // Call the method to update blocks in the game world
                updateBlocks(player, image, startX, startY, startZ);
            });

        }
        return true;
    }

    private BufferedImage resizeImage(BufferedImage image, int imageWidth, int imageHeight) {
        double aspectRatio = (double) imageWidth / imageHeight;
        int newWidth, newHeight;

        if (imageWidth > imageHeight) {
            newWidth = 100;
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newHeight = 100;
            newWidth = (int) (newHeight * aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        AffineTransform transform = new AffineTransform();
        transform.scale((double) newWidth / imageWidth, (double) newHeight / imageHeight);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, resizedImage);

        return resizedImage;
    }


    private void updateBlocks(Player player, BufferedImage image, int startX, int startY, int startZ) {
        new BukkitRunnable() {
            int x = 0;
            int z = 0;
            final int gcdValue = gcd(image.getWidth(), image.getHeight());
            final int blocksPerTick = gcdValue;

            @Override
            public void run() {
                for (int localX = 0; localX < blocksPerTick; localX++) {
                    for (int localZ = 0; localZ < blocksPerTick; localZ++) {
                        // Additional checks to ensure the loop doesn't go out of bounds
                        if (x + localX >= image.getWidth() || z + localZ >= image.getHeight()) {
                            cancel();
                            return;
                        }

                        int color = image.getRGB(x + localX, z + localZ);
                        Material blockType = getColorBlock(color);

                        Block block = player.getWorld().getBlockAt(startX + x + localX, startY, startZ + z + localZ);
                        block.setType(blockType);
                    }
                }
                x += blocksPerTick;
                if (x >= image.getWidth()) {
                    x = 0;
                    z += blocksPerTick;
                    if (z >= image.getHeight()) {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(PixelPaste.getInstance(), 0L, 1L);
    }


    /**
     * Compute the Greatest Common Divisor (GCD) between two numbers.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The greatest common divisor of a and b.
     */
    public int gcd(int a, int b) {
        return (b == 0) ? a : gcd(b, a % b);
    }


    /**
     * Provides tab-complete suggestions based on the files in the specified folder.
     *
     * @param sender The person who sent the command.
     * @param command The command object.
     * @param alias The alias of the command.
     * @param args Arguments passed along with the command.
     * @return A list of filenames as suggestions or null if no suggestions available.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
            List<String> suggestions = new ArrayList<>();

            if (files != null) {
                for (File file : files) {
                    suggestions.add(file.getName());
                }
            }

            return suggestions;
        }

        return null;
    }

}
