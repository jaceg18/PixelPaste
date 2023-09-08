package github.jaceg18.pixelpaste.pixelpaste.utility;

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
        int red1 = color1.getRed();
        int green1 = color1.getGreen();
        int blue1 = color1.getBlue();

        int red2 = color2.getRed();
        int green2 = color2.getGreen();
        int blue2 = color2.getBlue();

        int deltaRed = red1 - red2;
        int deltaGreen = green1 - green2;
        int deltaBlue = blue1 - blue2;

        return Math.sqrt(deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue);
    }


    /**
     * Calculates the depth of a specific pixel color using its luminance.
     *
     * @param pixelColor The color of the pixel.
     * @return The depth of the color based on its luminance.
     */
    public static int getDepth(int pixelColor, int max_depth) {
        int r = (pixelColor >> 16) & 0xFF;
        int g = (pixelColor >> 8) & 0xFF;
        int b = pixelColor & 0xFF;

        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return (int)(luminance / 256.0 * max_depth); // 5 blocks max
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
     * @param imageWidth The width of the image.
     * @param imageHeight The height of the image.
     * @param image The BufferedImage object.
     * @return The optimal number of blocks to be rendered per tick.
     */
    public static int calculateBlocksPerTick(int imageWidth, int imageHeight, BufferedImage image) {
        // Calculate the Greatest Common Divisor (GCD) of imageWidth and imageHeight.
        int gcd = 1;
        for (int i = 1; i <= imageWidth && i <= imageHeight; i++) {
            if (imageWidth % i == 0 && imageHeight % i == 0) {
                gcd = i;
            }
        }

        // Initialize blocksPerTick with the GCD value.
        int blocksPerTick = gcd;

        // Cap blocksPerTick at a maximum of 50.
        blocksPerTick = Math.min(blocksPerTick, 50);

        // Calculate the average brightness of the image.
        int averageBrightness = calculateAverageBrightness(image);

        // Adjust blocksPerTick based on image brightness.
        blocksPerTick = (averageBrightness > 200)
                ? Math.min(blocksPerTick, 30)
                : (int) Math.min(blocksPerTick * 1.1, 50);

        return blocksPerTick;
    }

    /**
     * Calculates the average brightness of the given image.
     *
     * @param image The BufferedImage object.
     * @return The average brightness of the image.
     */
    private static int calculateAverageBrightness(BufferedImage image) {
        long totalBrightness = 0;
        int pixelCount = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                totalBrightness += getBrightness(image.getRGB(x, y));
                pixelCount++;
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
    static int getBrightness(int pixelColor) {
        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;

        return (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
    }
}
