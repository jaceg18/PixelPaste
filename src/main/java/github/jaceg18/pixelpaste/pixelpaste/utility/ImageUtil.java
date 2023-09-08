package github.jaceg18.pixelpaste.pixelpaste.utility;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtil {
    /**
     * Resizes a 2D image to be less than 100x100 pixels.
     * @param image The image to resize
     * @return the resized image
     */
    public static BufferedImage resizeImage2D(BufferedImage image, int maxDimension) {
        double aspectRatio = (double) image.getWidth() / image.getHeight();
        int newWidth, newHeight;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (imageWidth > imageHeight) {
            newWidth = maxDimension;
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newHeight = maxDimension;
            newWidth = (int) (newHeight * aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        AffineTransform transform = new AffineTransform();
        transform.scale((double) newWidth / imageWidth, (double) newHeight / imageHeight);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, resizedImage);

        return resizedImage;
    }
    /**
     * Resizes a 3D image to be less than 100x100 pixels.
     * @param image The image to resize
     * @return the resized image
     */
    public static BufferedImage resizeImage3D(BufferedImage image, int maxDimension) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        if (originalWidth > maxDimension || originalHeight > maxDimension) {
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = Math.min(maxDimension, originalWidth);
            int newHeight = (int) (newWidth / aspectRatio);

            if (newHeight > maxDimension) {
                newHeight = maxDimension;
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
        return image;
    }

}
