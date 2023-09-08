package github.jaceg18.pixelpaste.pixelpaste.logic.processing.core;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
    /**
     * An image processor
     * @param player the player
     * @param image the image
     * @param orientation image orientation
     * @param maxDepth image depth limit
     * @param is2D is the image 2D?
     * @return the process
     */
    BukkitRunnable process(Player player, BufferedImage image, String orientation, int maxDepth, boolean is2D);
}
