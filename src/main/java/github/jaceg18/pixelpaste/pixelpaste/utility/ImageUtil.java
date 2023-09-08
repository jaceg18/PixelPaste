package github.jaceg18.pixelpaste.pixelpaste.utility;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtil {

    /**
     * Resizes an image if over the max_dimension argument.
     * @param image The image to resize
     * @param maxDimension The max dimension size
     * @param is3D is the build 3D?
     * @return The resized image
     */
    private static BufferedImage resizeImage(BufferedImage image, int maxDimension, boolean is3D) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        double aspectRatio = (double) originalWidth / originalHeight;

        int newWidth = Math.min(maxDimension, originalWidth);
        int newHeight = (int) (newWidth / aspectRatio);

        if (newHeight > maxDimension) {
            newHeight = maxDimension;
            newWidth = (int) (newHeight * aspectRatio);
        }

        if (is3D) {
            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            return resizedImage;
        } else {
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
            AffineTransform transform = new AffineTransform();
            transform.scale((double) newWidth / originalWidth, (double) newHeight / originalHeight);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            op.filter(image, resizedImage);
            return resizedImage;
        }
    }

    /**
     * Resizes a 2D image to be less than 100x100 pixels.
     * @param image The image to resize
     * @return the resized image
     */
    public static BufferedImage resizeImage2D(BufferedImage image, int maxDimension) {
        return resizeImage(image, maxDimension, false);
    }
    /**
     * Resizes a 3D image to be less than 100x100 pixels.
     * @param image The image to resize
     * @return the resized image
     */
    public static BufferedImage resizeImage3D(BufferedImage image, int maxDimension) {
        return resizeImage(image, maxDimension, true);
    }

}
