package github.jaceg18.pixelpaste.pixelpaste.logic.core;

import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import github.jaceg18.pixelpaste.pixelpaste.logic.processing.core.ImageProcessor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


import java.awt.image.BufferedImage;

public class PixelBuilder {
    private final ImageProcessor imageProcessor;

    /**
     * The constructor for pixel builder
     * @param imageProcessor The image process
     */
    public PixelBuilder(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }


    /**
     * Gets the image process and saves it to queue for confirmation
     * @param player the player
     * @param image the image
     * @param orientation the orientation
     * @param maxDepth the max depth
     * @param is2D is the image 2D?
     */
    public void buildImage(Player player, BufferedImage image, String orientation, int maxDepth, boolean is2D) {
        BukkitRunnable runnable = imageProcessor.process(player, image, orientation, maxDepth, is2D);
        PendingConfirmation.addConfirmation(player, runnable);
    }


}
