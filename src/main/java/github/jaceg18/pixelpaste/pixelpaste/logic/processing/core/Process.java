package github.jaceg18.pixelpaste.pixelpaste.logic.processing.core;

import github.jaceg18.pixelpaste.pixelpaste.logic.processing.logic.ImageProcessingStrategy;
import github.jaceg18.pixelpaste.pixelpaste.utility.math.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;

import static github.jaceg18.pixelpaste.pixelpaste.logic.core.BlockManager.getColorBlock3D;
import static github.jaceg18.pixelpaste.pixelpaste.logic.core.BlockManager.setBlockType;
import static github.jaceg18.pixelpaste.pixelpaste.utility.math.MathUtil.calculateBlocksPerTick;

public class Process implements ImageProcessingStrategy {

    private final boolean is2D;

    /**
     * Constructor or base process class
     * @param is2D is the build 2D?
     */
    public Process(boolean is2D){
        this.is2D = is2D;
    }

    /**
     * Sets the process
     * @param player The player processing the image
     * @param image The image being processed
     * @param orientation The orientation of the image
     * @param maxDepth The max depth of the image 3D
     * @param startX the starting x of the image build
     * @param startY the starting y of the image build
     * @param startZ the starting z of the image build
     * @return 2D or 3D process
     */
    @Override
    public BukkitRunnable process(Player player, BufferedImage image, String orientation, int maxDepth, int startX, int startY, int startZ) {
        return is2D ? process2D(image, player, startX, startY, startZ) : process3D(player, image, orientation, maxDepth, startX, startY, startZ);
    }


    /**
     * Processes a 2D image
     * @param image the image
     * @param player the player
     * @param startX starting x
     * @param startY starting y
     * @param startZ starting z
     * @return the 2d process
     */
    private BukkitRunnable process2D(BufferedImage image, Player player, int startX, int startY, int startZ) {
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
     * Processes a 3D image
     * @param image the image
     * @param player the player
     * @param startX starting x
     * @param startY starting y
     * @param startZ starting z
     * @return the 3d process
     */
    private BukkitRunnable process3D(Player player, BufferedImage image, String orientation, int maxDepth, int startX, int startY, int startZ) {

        int finalImageWidth = image.getWidth();
        int finalImageHeight = image.getHeight();

        int blocksPerTick = calculateBlocksPerTick(finalImageWidth, finalImageHeight, image);

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

                        int color = image.getRGB(x + localX, y + localY);
                        Object[] blockInfo = getColorBlock3D(color, maxDepth);
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
}