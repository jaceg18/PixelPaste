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

    public PixelCommand(File folder){
        this.folder = folder;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
            // Load the image
            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                BufferedImage image;
                try {
                    image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + args[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                if (imageWidth > 100 || imageHeight > 100) {
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

                    image = resizedImage;  // Replace the original image with the resized image
                    imageWidth = newWidth; // Update width and height values
                    imageHeight = newHeight;
                }

                int startX = player.getLocation().getBlockX();
                int startY = player.getLocation().getBlockY();
                int startZ = player.getLocation().getBlockZ();

                int finalImageWidth = imageWidth;
                int finalImageHeight = imageHeight;
                BufferedImage finalImage = image;

                new BukkitRunnable() {
                    int x = 0;
                    int z = 0;

                    final int gcdValue = gcd(finalImageWidth, finalImageHeight);
                    final int blocksPerTick = gcdValue;

                    @Override
                    public void run() {
                        for (int localX = 0; localX < blocksPerTick; localX++) {
                            for (int localZ = 0; localZ < blocksPerTick; localZ++) {
                                if (x + localX >= finalImageWidth || z + localZ >= finalImageHeight) {
                                    cancel();
                                    return;
                                }

                                int color = finalImage.getRGB(x + localX, z + localZ);
                                Material blockType = getColorBlock(color);

                                Block block = player.getWorld().getBlockAt(startX + x + localX, startY, startZ + z + localZ);
                                block.setType(blockType);
                            }
                        }

                        x += blocksPerTick;
                        if (x >= finalImageWidth) {
                            x = 0;
                            z += blocksPerTick;
                            if (z >= finalImageHeight) {
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(PixelPaste.getInstance(), 0L, 1L);

            });

        }
        return false;
    }


    public int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) { // Assuming the first argument is the filename
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
