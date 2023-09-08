package github.jaceg18.pixelpaste.pixelpaste.player;

import github.jaceg18.pixelpaste.PixelPaste;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Manages pending confirmations for PixelPaste actions.
 */
public class PendingConfirmation {
    private static HashMap<UUID, BukkitRunnable> pendingConfirmations = new HashMap<>();

    public static HashMap<UUID, HashMap<Location, Material>> originalBlocks = new HashMap<>();
    /**
     * Stores the original blocks before an action is confirmed.
     *
     * @param playerId The UUID of the player.
     * @param blocks A HashMap of the original blocks.
     */
    public static void storeOriginalBlocks(UUID playerId, HashMap<Location, Material> blocks) {
        originalBlocks.put(playerId, blocks);
    }
    /**
     * Retrieves the original blocks for a given player.
     *
     * @param playerId The UUID of the player.
     * @return A HashMap of the original blocks.
     */
    public static HashMap<Location, Material> getOriginalBlocks(UUID playerId) {
        return originalBlocks.getOrDefault(playerId, new HashMap<>());
    }
    /**
     * Removes the stored original blocks for a given player.
     *
     * @param playerId The UUID of the player.
     */
    public static void removeOriginalBlocks(UUID playerId) {
        originalBlocks.remove(playerId);
    }
    /**
     * Adds a pending confirmation for a player.
     *
     * @param player The player object.
     * @param runnable The runnable task.
     */
    public static void addConfirmation(Player player, BukkitRunnable runnable) {
        pendingConfirmations.put(player.getUniqueId(), runnable);
    }
    /**
     * Gets the pending confirmation for a player.
     *
     * @param player The player object.
     * @return The runnable task.
     */
    public static BukkitRunnable getConfirmation(Player player) {
        return pendingConfirmations.get(player.getUniqueId());
    }
    /**
     * Checks if a player has a pending confirmation.
     *
     * @param player The player object.
     * @return true if the player has a pending confirmation; false otherwise.
     */
    public static boolean hasPendingConfirmation(Player player){
        return pendingConfirmations.containsKey(player.getUniqueId());
    }
    /**
     * Removes a pending confirmation for a player.
     *
     * @param player The player object.
     */
    public static void removeConfirmation(Player player) {
        pendingConfirmations.remove(player.getUniqueId());
    }
    /**
     * Confirms and runs the pending task for a player.
     *
     * @param player The player object.
     * @return true if the task was successfully run; false otherwise.
     */
    public static boolean confirmAndRun(Player player) {
        BukkitRunnable runnable = getConfirmation(player);
        if (runnable != null) {
            runnable.runTaskTimer(PixelPaste.getInstance(), 0L, 1L);
            removeConfirmation(player);
            return true;
        }
        return false;
    }
}