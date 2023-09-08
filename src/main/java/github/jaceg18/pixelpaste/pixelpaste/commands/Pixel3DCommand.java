package github.jaceg18.pixelpaste.pixelpaste.commands;

import github.jaceg18.pixelpaste.pixelpaste.PixelPaste;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static github.jaceg18.pixelpaste.pixelpaste.PixelPaste.getColorBlock;

public class Pixel3DCommand implements CommandExecutor, TabCompleter {
    private final File folder;
    /**
     * Constructor for initializing the Pixel3DCommand class.
     *
     * @param folder The folder containing the image files.
     */
    public Pixel3DCommand(File folder) {
        this.folder = folder;
    }


    /**
     * Processes the image and prepares it for pixel pasting.
     *
     * @param player      The player initiating the action.
     * @param args        The command arguments.
     * @param orientation The orientation of the image (vert/horz).
     */
    private void processImage(Player player, String[] args, String orientation) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(folder, args[0]));
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Failed to read/find file...");
            return;
        }

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Resize if larger than 100x100
        if (originalWidth > 100 || originalHeight > 100) {
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = Math.min(100, originalWidth);
            int newHeight = (int) (newWidth / aspectRatio);

            if (newHeight > 100) {
                newHeight = 100;
                newWidth = (int) (newHeight * aspectRatio);
            }

            // Perform the resize
            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            image = resizedImage;
        }

        int finalImageWidth = image.getWidth();
        int finalImageHeight = image.getHeight();

        int startX = player.getLocation().getBlockX();
        int startY = player.getLocation().getBlockY();
        int startZ = player.getLocation().getBlockZ();


        int blocksPerTick = calculateBlocksPerTick(finalImageWidth, finalImageHeight, image);

        BufferedImage finalImage = image;
        new BukkitRunnable() {
            int x = 0;
            int y = 0;

            @Override
            public void run() {
                for (int localX = 0; localX < blocksPerTick; localX++) {
                    for (int localY = 0; localY < blocksPerTick; localY++) {
                        if (x + localX >= finalImageWidth || y + localY >= finalImageHeight) {
                            cancel();
                            return;
                        }

                        int color = finalImage.getRGB(x + localX, y + localY);
                        Object[] blockInfo = getColorBlock3D(color);
                        Material blockType = (Material) blockInfo[0];
                        int depth = (int) blockInfo[1];

                        for (int localZ = 0; localZ <= depth; localZ++) {
                            Block block;
                            if ("vert".equals(orientation)) {
                                block = player.getWorld().getBlockAt(startX + x + localX, startY + finalImageHeight - 1 - (y + localY), startZ + localZ);
                            } else { // horz
                                block = player.getWorld().getBlockAt(startX + x + localX, startY - depth, startZ + y + localY);
                            }
                            block.setType(blockType);
                        }
                    }
                }

                x += blocksPerTick;
                if (x >= finalImageWidth) {
                    x = 0;
                    y += blocksPerTick;
                    if (y >= finalImageHeight) {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(PixelPaste.getInstance(), 0L, 1L);
    }


    /**
     * Handles the command logic for pixel pasting.
     *
     * @param sender  The entity that initiated the command.
     * @param command The command object.
     * @param label   The command label.
     * @param args    The command arguments.
     * @return True if the command was executed successfully, otherwise false.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (player.isOp() || player.hasPermission("pixelpaste")) {
            if (args.length < 1) {
                player.sendMessage("You must provide an image filename.");
                return false;
            }
            String orientation = args[1].toLowerCase();
            if (!orientation.equals("vert") && !orientation.equals("horz")) {
                player.sendMessage("Invalid orientation. Use 'vert' for vertical or 'horz' for horizontal.");
                return false;
            }

            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> processImage(player, args, orientation));
        }
        return true;
    }


    /**
     * Calculates the optimal number of blocks to render per tick based on the image's dimensions and brightness.
     *
     * @param imageWidth The width of the image.
     * @param imageHeight The height of the image.
     * @param image The BufferedImage object.
     * @return The optimal number of blocks to be rendered per tick.
     */
    public int calculateBlocksPerTick(int imageWidth, int imageHeight, BufferedImage image) {
        // Calculate the Greatest Common Divisor (GCD) of imageWidth and imageHeight.
        int gcd = 1;
        for (int i = 1; i <= imageWidth && i <= imageHeight; i++) {
            if (imageWidth % i == 0 && imageHeight % i == 0) {
                gcd = i;
            }
        }

        // Initialize blocksPerTick with the GCD value.
        int blocksPerTick = gcd;

        // Cap blocksPerTick at a maximum of 50.
        blocksPerTick = Math.min(blocksPerTick, 50);

        // Calculate the average brightness of the image.
        int averageBrightness = calculateAverageBrightness(image);

        // Adjust blocksPerTick based on image brightness.
        blocksPerTick = (averageBrightness > 200)
                ? Math.min(blocksPerTick, 30)
                : (int) Math.min(blocksPerTick * 1.1, 50);

        return blocksPerTick;
    }


    /**
     * Calculates the average brightness of the given image.
     *
     * @param image The BufferedImage object.
     * @return The average brightness of the image.
     */
    private int calculateAverageBrightness(BufferedImage image) {
        long totalBrightness = 0;
        int pixelCount = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                totalBrightness += getBrightness(image.getRGB(x, y));
                pixelCount++;
            }
        }

        return (int) (totalBrightness / pixelCount);
    }


    /**
     * Determines the corresponding 3D block color and depth for a given pixel color.
     *
     * @param pixelColor The color of the pixel.
     * @return An object array containing the block material and depth.
     */
    private Object[] getColorBlock3D(int pixelColor) {
        int alpha = (pixelColor >> 24) & 0xff;
        if (alpha == 0) {
            return new Object[]{Material.AIR, 0};
        }

        int depth = getDepth(pixelColor);
        Material closestWool = getColorBlock(pixelColor);

        return new Object[]{closestWool, depth};
    }


    /**
     * Calculates the brightness of a specific pixel color using the formula for luminance.
     *
     * @param pixelColor The color of the pixel.
     * @return The brightness level of the pixel.
     */
    private int getBrightness(int pixelColor) {
        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;

        return (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
    }


    /**
     * Calculates the depth of a specific pixel color using its luminance.
     *
     * @param pixelColor The color of the pixel.
     * @return The depth of the color based on its luminance.
     */
    private int getDepth(int pixelColor) {
        int r = (pixelColor >> 16) & 0xFF;
        int g = (pixelColor >> 8) & 0xFF;
        int b = pixelColor & 0xFF;

        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return (int)(luminance / 256.0 * 5); // assuming 5 blocks max depth
    }


    /**
     * Handles tab-completion for the command.
     *
     * @param sender  The entity initiating tab completion.
     * @param command The command object.
     * @param alias   The command alias.
     * @param args    The command arguments.
     * @return A list of suggested completions.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
            if (files != null) {
                for (File file : files) {
                    suggestions.add(file.getName());
                }
            }
        } else if (args.length == 2) {
            return Arrays.asList("vert", "horz");
        }
        return suggestions;
    }
}