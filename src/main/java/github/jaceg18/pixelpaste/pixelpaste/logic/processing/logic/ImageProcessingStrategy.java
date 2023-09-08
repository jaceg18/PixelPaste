package github.jaceg18.pixelpaste.pixelpaste.logic.processing.logic;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;

public interface ImageProcessingStrategy {
    /**
     * Processes an image
     * @param player The player processing the image
     * @param image The image being processed
     * @param orientation The orientation of the image
     * @param maxDepth The max depth of the image 3D
     * @param startX the starting x of the image build
     * @param startY the starting y of the image build
     * @param startZ the starting z of the image build
     * @return The bukkit runnable to build the image
     */
    BukkitRunnable process(Player player, BufferedImage image, String orientation, int maxDepth, int startX, int startY, int startZ);
}