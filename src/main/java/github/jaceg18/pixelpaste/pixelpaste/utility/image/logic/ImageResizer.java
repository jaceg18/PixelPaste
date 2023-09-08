package github.jaceg18.pixelpaste.pixelpaste.utility.image.logic;

import github.jaceg18.pixelpaste.pixelpaste.utility.image.core.Strategy;

import java.awt.image.BufferedImage;

public class ImageResizer {
    private final Strategy strategy;

    /**
     * Image resizer constructor holds strategy
     * @param strategy The resizing strategy (eg 2d, 3d)
     */
    public ImageResizer(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Resizes the image using the strategy.
     * @param image The image to resize
     * @param maxDimension The max dimension of the image
     * @return the resized image
     */
    public BufferedImage resize(BufferedImage image, int maxDimension) {
        return strategy.resize(image, maxDimension);
    }
}
