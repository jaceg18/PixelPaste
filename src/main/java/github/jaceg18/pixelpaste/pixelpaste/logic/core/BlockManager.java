package github.jaceg18.pixelpaste.pixelpaste.logic.core;

import github.jaceg18.pixelpaste.pixelpaste.PixelPaste;
import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import github.jaceg18.pixelpaste.pixelpaste.utility.math.MathUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class BlockManager {


    private static final Material[] BLOCK_TYPES = {
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

    private static final int[][] BLOCK_COLORS = {
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


    /**
     * Finds the closest matching block Material based on RGB values.
     * This is a temporary method. Soon I will be putting all these files via JSON.
     * This method is just here for the beta stages of this plugin.
     *
     * @param pixelColor the ARGB value of the pixel.
     * @return the closest matching Material.
     */
    public static Material getColorBlock(int pixelColor) {
        if (((pixelColor >> 24) & 0xff) == 0) {
            return Material.AIR;
        }

        Color givenColor = new Color((pixelColor >> 16) & 0xFF, (pixelColor >> 8) & 0xFF, pixelColor & 0xFF);
        return findClosestBlock(givenColor);
    }


    /**
     * Returns the corresponding block type and depth for a given pixel color in 3D pixel art.
     *
     * @param pixelColor The color of the pixel.
     * @return An Object array containing the closest Material match and the depth.
     */
    public static Object[] getColorBlock3D(int pixelColor, int maxDepth) {
        if (((pixelColor >> 24) & 0xff) == 0) {
            return new Object[]{Material.AIR, 0};
        }

        int depth = MathUtil.getDepth(pixelColor, maxDepth);
        Material closestBlock = getColorBlock(pixelColor);

        return new Object[]{closestBlock, depth};
    }


    /**
     * Highlights a given selection
     * @param image The image being translated
     * @param initialLocation the initial location of the build
     */
    public static void highlightArea(Player player, BufferedImage image, Location initialLocation, String orientation) {
        int width = image.getWidth();
        int height = image.getHeight();
        World world = initialLocation.getWorld();
        HashMap<Location, Material> original = new HashMap<>();

        Bukkit.getScheduler().runTask(PixelPaste.getInstance(), () -> {
            for (int x = 0; x < width; x++) {
                for (int yz = 0; yz < height; yz++) {
                    int[] coords = ("vert".equals(orientation)) ? new int[]{x, yz, 0} : new int[]{x, 0, yz};
                    Location loc = initialLocation.clone().add(coords[0], coords[1], coords[2]);
                    original.put(loc, world.getBlockAt(loc).getType());

                    Material newType = (x == 0 || x == width - 1 || yz == 0 || yz == height - 1) ? Material.GLASS : Material.AIR;
                    world.getBlockAt(loc).setType(newType);
                }
            }
            PendingConfirmation.storeOriginalBlocks(player.getUniqueId(), original);
        });
    }




    public static void restoreOriginalBlocks(Player player) {
        UUID playerId = player.getUniqueId();
        HashMap<Location, Material> originalBlocks = PendingConfirmation.getOriginalBlocks(playerId);

        Bukkit.getScheduler().runTask(PixelPaste.getInstance(), () -> {
            originalBlocks.forEach((loc, originalMaterial) -> loc.getBlock().setType(originalMaterial));
        });

        PendingConfirmation.removeOriginalBlocks(playerId);
    }
    private static Material findClosestBlock(Color givenColor) {
        double closestDistance = Double.MAX_VALUE;
        Material closestBlock = Material.BLACK_WOOL;

        for (int i = 0; i < BLOCK_TYPES.length; i++) {
            Color blockColor = new Color(BLOCK_COLORS[i][0], BLOCK_COLORS[i][1], BLOCK_COLORS[i][2]);
            double distance = MathUtil.colorDistance(givenColor, blockColor);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBlock = BLOCK_TYPES[i];
            }
        }

        return closestBlock;
    }

    /**
     * Helper method for processing
     * @param image Image to process
     * @param player Player processing the image
     * @param x x position
     * @param z y position
     * @param startX starting x position
     * @param startY starting y position
     * @param startZ starting z position
     */
    public static void setBlockType(BufferedImage image, Player player, int x, int z, int startX, int startY, int startZ) {
        int color = image.getRGB(x, z);
        Material blockType = BlockManager.getColorBlock(color);
        Block block = player.getWorld().getBlockAt(startX + x, startY, startZ + z);
        block.setType(blockType);
    }

}
