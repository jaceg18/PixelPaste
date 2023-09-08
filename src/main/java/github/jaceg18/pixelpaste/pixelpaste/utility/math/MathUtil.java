package github.jaceg18.pixelpaste.pixelpaste.utility.math;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MathUtil {
    /**
     * Calculates the distance between two colors.
     *
     * @param color1 the first Color object.
     * @param color2 the second Color object.
     * @return the distance between the two colors.
     */
    public static double colorDistance(Color color1, Color color2) {
        return Math.sqrt(Math.pow(color1.getRed() - color2.getRed(), 2) +
                Math.pow(color1.getGreen() - color2.getGreen(), 2) +
                Math.pow(color1.getBlue() - color2.getBlue(), 2));
    }


    /**
     * Calculates the depth of a specific pixel color using its luminance.
     *
     * @param pixelColor The color of the pixel.
     * @return The depth of the color based on its luminance.
     */
    public static int getDepth(int pixelColor, int maxDepth) {
        return (int) (calculateLuminance(pixelColor) / 256.0 * maxDepth);
    }

    /**
     * Calculates the luminance of the pixel color
     * @param pixelColor The pixel color
     * @return The luminance as a double
     */
    private static double calculateLuminance(int pixelColor) {
        return 0.2126 * ((pixelColor >> 16) & 0xFF) +
                0.7152 * ((pixelColor >> 8) & 0xFF) +
                0.0722 * (pixelColor & 0xFF);
    }

    /**
     * Compute the Greatest Common Divisor (GCD) between two numbers.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The greatest common divisor of a and b.
     */
    public static int gcd(int a, int b) {
        return (b == 0) ? a : gcd(b, a % b);
    }


    /**
     * Calculates the optimal number of blocks to render per tick based on the image's dimensions and brightness.
     *
     * @param width The width of the image.
     * @param height The height of the image.
     * @param image The BufferedImage object.
     * @return The optimal number of blocks to be rendered per tick.
     */
    public static int calculateBlocksPerTick(int width, int height, BufferedImage image) {
        int gcd = gcd(width, height);
        int blocksPerTick = Math.min(gcd, 50);
        int avgBrightness = calculateAverageBrightness(image);
        return avgBrightness > 200 ? Math.min(blocksPerTick, 30) : (int) Math.min(blocksPerTick * 1.1, 50);
    }

    /**
     * Calculates the average brightness of the given image.
     *
     * @param image The BufferedImage object.
     * @return The average brightness of the image.
     */
    private static int calculateAverageBrightness(BufferedImage image) {
        long totalBrightness = 0;
        int pixelCount = image.getWidth() * image.getHeight();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                totalBrightness += getBrightness(image.getRGB(x, y));
            }
        }
        return (int) (totalBrightness / pixelCount);
    }

    /**
     * Calculates the brightness of a specific pixel color using the formula for luminance.
     *
     * @param pixelColor The color of the pixel.
     * @return The brightness level of the pixel.
     */
    private static int getBrightness(int pixelColor) {
        return (int) Math.round(0.299 * ((pixelColor >> 16) & 0xFF) +
                0.587 * ((pixelColor >> 8) & 0xFF) +
                0.114 * (pixelColor & 0xFF));
    }
}
