package github.jaceg18.pixelpaste.pixelpaste.commands.core;

import github.jaceg18.pixelpaste.pixelpaste.PixelPaste;
import github.jaceg18.pixelpaste.pixelpaste.logic.core.BlockManager;
import github.jaceg18.pixelpaste.pixelpaste.logic.core.PixelBuilder;
import github.jaceg18.pixelpaste.pixelpaste.logic.processing.core.ImageProcessor;
import github.jaceg18.pixelpaste.pixelpaste.logic.processing.logic.ImageTool;
import github.jaceg18.pixelpaste.pixelpaste.utility.image.core.Strategy;
import github.jaceg18.pixelpaste.pixelpaste.utility.image.logic.ImageResizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class BaseCommand implements Command {

    protected final ImageProcessor imageProcessor = new ImageTool();
    protected final PixelBuilder pixelBuilder = new PixelBuilder(imageProcessor);

    protected int parseDimension(String[] args, int index, int defaultValue) {
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            return defaultValue;
        }
    }

    protected void processImageAsync(Player player, String filename, int maxDimension, int maxDepth, String orientation, boolean is2D) {
        Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
            BufferedImage image;
            try {
                image = ImageIO.read(new File(PixelPaste.getInstance().getDataFolder() + "/pixelart/" + filename));

                Strategy imageStrategy = is2D ? Strategy.RESIZE_2D : Strategy.RESIZE_3D;
                ImageResizer resizer = new ImageResizer(imageStrategy);
                image = resizer.resize(image, maxDimension);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // Use the new PixelBuilder class to build the image
            pixelBuilder.buildImage(player, image, orientation, maxDepth, is2D);

            BufferedImage finalImage = image;
            Bukkit.getScheduler().runTaskAsynchronously(PixelPaste.getInstance(), () -> {
                BlockManager.highlightArea(player, finalImage, player.getLocation().clone(), orientation);
                player.sendMessage("Area highlighted. Type 'ppconfirm' to proceed with building, 'ppcancel' to cancel.");
            });
        });
    }
}
