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
    public static BufferedImage resizeImage2D(BufferedImage image) {
        double aspectRatio = (double) image.getWidth() / image.getHeight();
        int newWidth, newHeight;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

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

        return resizedImage;
    }
    /**
     * Resizes a 3D image to be less than 100x100 pixels.
     * @param image The image to resize
     * @return the resized image
     */
    public static BufferedImage resizeImage3D(BufferedImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Resize if larger than 100x100
        if (originalWidth > 100 || originalHeight > 100) {
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = Math.min(100, originalWidth);
            int newHeight = (int) (newWidth / aspectRatio);

            if (newHeight > 100) {
                newHeight = 100;
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
