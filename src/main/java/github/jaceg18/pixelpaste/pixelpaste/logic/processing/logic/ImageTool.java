package github.jaceg18.pixelpaste.pixelpaste.logic.processing.logic;

import github.jaceg18.pixelpaste.pixelpaste.logic.processing.core.ImageProcessor;
import github.jaceg18.pixelpaste.pixelpaste.logic.processing.core.Process;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;

public class ImageTool implements ImageProcessor {

    /**
     * A tool for processing an image.
     * @param player The player thats processing
     * @param image The image being processed
     * @param orientation The orientation of the image
     * @param maxDepth The max depth of the image (3D)
     * @param is2D Is the image 2D?
     * @return A BukkitRunnable that builds the image on call
     */
    @Override
    public BukkitRunnable process(Player player, BufferedImage image, String orientation, int maxDepth, boolean is2D) {
        int startX = player.getLocation().getBlockX();
        int startY = player.getLocation().getBlockY();
        int startZ = player.getLocation().getBlockZ();

        ImageProcessingStrategy strategy = new Process(is2D);
        return strategy.process(player, image, orientation, maxDepth, startX, startY, startZ);
    }
}