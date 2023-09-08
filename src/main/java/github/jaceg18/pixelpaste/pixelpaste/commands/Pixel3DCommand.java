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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static github.jaceg18.pixelpaste.pixelpaste.PixelPaste.getColorBlock;

public class Pixel3DCommand implements CommandExecutor, TabCompleter {
    private File folder;

    public Pixel3DCommand(File folder) {
        this.folder = folder;
    }

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

            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                BufferedImage image;
                try {
                    image = ImageIO.read(new File(folder, args[0]));
                } catch (IOException e) {
                    e.printStackTrace();
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

                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                int startX = player.getLocation().getBlockX();
                int startY = player.getLocation().getBlockY();
                int startZ = player.getLocation().getBlockZ();

                BufferedImage finalImage = image;
                BufferedImage finalImage1 = image;

                int blocksPerTick = calculateBlocksPerTick(imageWidth, imageHeight);
                BufferedImage finalImage2 = image;
                new BukkitRunnable() {
                    int x = 0;
                    int z = 0;

                    @Override
                    public void run() {
                        // Place blocks based on blocksPerTick
                        for (int localX = 0; localX < blocksPerTick; localX++) {
                            for (int localZ = 0; localZ < blocksPerTick; localZ++) {
                                if (x + localX >= imageWidth || z + localZ >= imageHeight) {
                                    cancel();
                                    return;
                                }

                                int color = finalImage2.getRGB(x + localX, z + localZ);
                                Object[] result = getColorBlock3D(color);
                                Material blockType = (Material) result[0];
                                int depth = (Integer) result[1];

                                // Loop through y-axis based on brightness (depth)
                                for (int localY = 0; localY <= depth; localY++) {
                                    Block block = player.getWorld().getBlockAt(startX + x + localX, startY + localY, startZ + z + localZ);
                                    block.setType(blockType);
                                }
                            }
                        }

                        x += blocksPerTick;
                        if (x >= imageWidth) {
                            x = 0;
                            z += blocksPerTick;
                            if (z >= imageHeight) {
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(PixelPaste.getInstance(), 0L, 1L);
            });
        }
        return true;
    }

    public int calculateBlocksPerTick(int imageWidth, int imageHeight) {
        // Finding the greatest common divisor (GCD)
        int gcd = 1;
        for(int i = 1; i <= imageWidth && i <= imageHeight; i++) {
            if(imageWidth % i == 0 && imageHeight % i == 0)
                gcd = i;
        }

        // You can use the gcd directly as blocksPerTick, or you can calculate it in some other way
        // For example, you might want to divide it by some value to get a smaller number
        int blocksPerTick = gcd;

        // Optional: if blocksPerTick is too large, you can set a maximum limit
        if (blocksPerTick > 50) {
            blocksPerTick = 50; // or any other max limit you prefer
        }

        return blocksPerTick;
    }
    private Object[] getColorBlock3D(int pixelColor) {
        // Extract the RGB and Alpha components from the pixel color
        int alpha = (pixelColor >> 24) & 0xff;
        if (alpha == 0) {
            return new Object[]{Material.AIR, 0};
        }

        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;

        // Calculate luminance in a way that mimics human perception
        double luminance = 0.299 * red + 0.587 * green + 0.114 * blue;
        int depth = (int) Math.round(luminance * 10 / 255);

        // Your color matching logic here (same as before)
        Color givenColor = new Color(red, green, blue);
        Material closestWool = getColorBlock(pixelColor); // Reusing your existing method

        return new Object[]{closestWool, depth};
    }

    private int getBrightness(int pixelColor) {
        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;
        return (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
    }

    private int getDepth(int pixelColor) {
        int r = (pixelColor >> 16) & 0xFF;
        int g = (pixelColor >> 8) & 0xFF;
        int b = pixelColor & 0xFF;

        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return (int)(luminance / 256.0 * 5); // assuming 5 blocks max depth
    }
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
        }
        return suggestions;
    }
}