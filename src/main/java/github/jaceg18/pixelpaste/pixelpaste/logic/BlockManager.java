package github.jaceg18.pixelpaste.pixelpaste.logic;

import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import github.jaceg18.pixelpaste.pixelpaste.utility.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BlockManager {

    /**
     * Finds the closest matching block Material based on RGB values.
     * This is a temporary method. Soon I will be putting all these files via JSON.
     * This method is just here for the beta stages of this plugin.
     *
     * @param pixelColor the ARGB value of the pixel.
     * @return the closest matching Material.
     */
    public static Material getColorBlock(int pixelColor) {
        int alpha = (pixelColor >> 24) & 0xff;
        if (alpha == 0) {
            return Material.AIR;
        }

        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;
        Color givenColor = new Color(red, green, blue);

        Material[] blockTypes = {
                Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
                Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
                Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
                Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL,
                Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE,
                Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE,
                Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE,
                Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE,
                Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA,
                Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA,
                Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
                Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA

        };

        int[][] blockColors = {
                {233, 236, 236}, {241, 118, 19}, {189, 68, 179}, {58, 175, 217},
                {248, 197, 39}, {112, 185, 26}, {237, 141, 172}, {63, 68, 72},
                {142, 142, 134}, {21, 137, 145}, {120, 71, 171}, {53, 57, 157},
                {114, 71, 40}, {85, 106, 27}, {162, 38, 35}, {20, 21, 26},
                {210, 210, 210}, {225, 97, 0}, {170, 48, 159}, {35, 137, 198},
                {241, 175, 21}, {94, 169, 24}, {214, 101, 143}, {48, 48, 48},
                {125, 125, 115}, {21, 119, 136}, {101, 32, 157}, {45, 47, 143},
                {96, 60, 31}, {73, 91, 36}, {142, 33, 33}, {8, 10, 15},
                {209, 177, 161}, {162, 84, 38}, {150, 88, 109}, {113, 109, 138},
                {186, 133, 35}, {104, 118, 53}, {162, 78, 79}, {58, 42, 36},
                {135, 107, 98}, {87, 91, 91}, {118, 70, 86}, {74, 60, 91},
                {77, 51, 36}, {76, 83, 42}, {143, 61, 47}, {37, 23, 16}
        };

        double closestDistance = Double.MAX_VALUE;
        Material closestBlock = Material.BLACK_WOOL;

        for (int i = 0; i < blockTypes.length; i++) {
            Color blockColor = new Color(blockColors[i][0], blockColors[i][1], blockColors[i][2]);
            double distance = MathUtil.colorDistance(givenColor, blockColor);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBlock = blockTypes[i];
            }
        }

        return closestBlock;
    }


    /**
     * Returns the corresponding block type and depth for a given pixel color in 3D pixel art.
     *
     * @param pixelColor The color of the pixel.
     * @return An Object array containing the closest Material match and the depth.
     */
    public static Object[] getColorBlock3D(int pixelColor) {
        int alpha = (pixelColor >> 24) & 0xff;
        if (alpha == 0) {
            return new Object[]{Material.AIR, 0};
        }

        int depth = MathUtil.getDepth(pixelColor);
        Material closestWool = getColorBlock(pixelColor);

        return new Object[]{closestWool, depth};
    }


    /**
     * Highlights a given selection
     * @param image The image being translated
     * @param initialLocation the initial location of the build
     */
    public static void highlightArea(Player player, BufferedImage image, Location initialLocation, String orientation) {
        int width = image.getWidth();
        int height = image.getHeight();

        int startX = initialLocation.getBlockX();
        int startY = initialLocation.getBlockY();
        int startZ = initialLocation.getBlockZ();

        World world = initialLocation.getWorld();
        HashMap<Location, Material> original = new HashMap<>();

        if ("vert".equals(orientation)) {
            // Vertical
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Location loc = new Location(world, startX + x, startY + y, startZ);
                    original.put(loc, loc.getBlock().getType());
                    if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                        loc.getBlock().setType(Material.GLASS);
                    } else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        } else {
            // Horizontal
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < height; z++) {
                    Location loc = new Location(world, startX + x, startY, startZ + z);
                    original.put(loc, loc.getBlock().getType());
                    if (x == 0 || x == width - 1 || z == 0 || z == height - 1) {
                        loc.getBlock().setType(Material.GLASS);
                    } else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

        // Store original blocks
        PendingConfirmation.storeOriginalBlocks(player.getUniqueId(), original);

    }
}
