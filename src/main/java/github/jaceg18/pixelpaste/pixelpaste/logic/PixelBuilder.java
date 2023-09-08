package github.jaceg18.pixelpaste.pixelpaste.logic;

import github.jaceg18.pixelpaste.pixelpaste.utility.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


import java.awt.image.BufferedImage;

import static github.jaceg18.pixelpaste.pixelpaste.logic.BlockManager.getColorBlock3D;
import static github.jaceg18.pixelpaste.pixelpaste.utility.MathUtil.calculateBlocksPerTick;

public class PixelBuilder {
    /**
     * The core component behind the 2D pasting
     * @param image The image to paste
     * @param player The player pasting
     * @return A runnable method that will be called on confirmation
     */
    public BukkitRunnable process2D(BufferedImage image, Player player) {
        int startX = player.getLocation().getBlockX();
        int startY = player.getLocation().getBlockY();
        int startZ = player.getLocation().getBlockZ();

        return new BukkitRunnable() {
            int x = 0, z = 0;
            final int blocksPerTick = MathUtil.gcd(image.getWidth(), image.getHeight());

            @Override
            public void run() {
                for (int localX = 0; localX < blocksPerTick; localX++) {
                    for (int localZ = 0; localZ < blocksPerTick; localZ++) {
                        if (x + localX >= image.getWidth() || z + localZ >= image.getHeight()) {
                            cancel();
                            return;
                        }
                        setBlockType(image, player, x + localX, z + localZ, startX, startY, startZ);
                    }
                }
                x += blocksPerTick;
                if (x >= image.getWidth()) {
                    x = 0;
                    z += blocksPerTick;
                    if (z >= image.getHeight()) {
                        cancel();
                    }
                }
            }
        };
    }
    /**
     * The core component behind the 3D pasting
     * @param image The image to paste
     * @param player The player pasting
     * @return A runnable method that will be called on confirmation
     */
    public BukkitRunnable process3D(Player player, BufferedImage image, String orientation, int max_depth) {

        int finalImageWidth = image.getWidth();
        int finalImageHeight = image.getHeight();

        int startX = player.getLocation().getBlockX();
        int startY = player.getLocation().getBlockY();
        int startZ = player.getLocation().getBlockZ();


        int blocksPerTick = calculateBlocksPerTick(finalImageWidth, finalImageHeight, image);

        BufferedImage finalImage = image;
        return new BukkitRunnable() {
            int x = 0;
            int y = 0;
            @Override
            public void run() {
                for (int localX = 0; localX < blocksPerTick; localX++) {
                    for (int localY = 0; localY < blocksPerTick; localY++) {
                        if (x + localX >= finalImageWidth || y + localY >= finalImageHeight) {
                            cancel();
                            return;
                        }

                        int color = finalImage.getRGB(x + localX, y + localY);
                        Object[] blockInfo = getColorBlock3D(color, max_depth);
                        Material blockType = (Material) blockInfo[0];
                        int depth = (int) blockInfo[1];

                        for (int localZ = 0; localZ <= depth; localZ++) {
                            Block block;
                            if ("vert".equals(orientation)) {
                                block = player.getWorld().getBlockAt(startX + x + localX, startY + finalImageHeight - 1 - (y + localY), startZ + localZ);
                            } else { // horz
                                block = player.getWorld().getBlockAt(startX + x + localX, startY - depth, startZ + y + localY);
                            }
                            block.setType(blockType);
                        }
                    }
                }

                x += blocksPerTick;
                if (x >= finalImageWidth) {
                    x = 0;
                    y += blocksPerTick;
                    if (y >= finalImageHeight) {
                        cancel();
                    }
                }
            }
        };
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
    private void setBlockType(BufferedImage image, Player player, int x, int z, int startX, int startY, int startZ) {
        int color = image.getRGB(x, z);
        Material blockType = BlockManager.getColorBlock(color);
        Block block = player.getWorld().getBlockAt(startX + x, startY, startZ + z);
        block.setType(blockType);
    }


}
