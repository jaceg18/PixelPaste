package github.jaceg18.pixelpaste.pixelpaste.utility.image.core;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public enum Strategy {

    /**
     * These enums hold different resizing strategies. You can add more here anytime.
     */
    RESIZE_2D {
        @Override
        public BufferedImage resize(BufferedImage image, int maxDimension) {
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = Math.min(maxDimension, originalWidth);
            int newHeight = (int) (newWidth / aspectRatio);

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
            AffineTransform transform = new AffineTransform();
            transform.scale((double) newWidth / originalWidth, (double) newHeight / originalHeight);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            op.filter(image, resizedImage);
            return resizedImage;
        }
    },
    RESIZE_3D {
        @Override
        public BufferedImage resize(BufferedImage image, int maxDimension) {
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = Math.min(maxDimension, originalWidth);
            int newHeight = (int) (newWidth / aspectRatio);

            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            return resizedImage;
        }
    };

    /**
     * Resizes the image depending on the strategy
     * @param image The image to resize
     * @param maxDimension The max image dimension
     * @return The resized image
     */
    public abstract BufferedImage resize(BufferedImage image, int maxDimension);
}
