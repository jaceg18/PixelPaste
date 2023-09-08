package github.jaceg18.pixelpaste.pixelpaste.events;

import github.jaceg18.pixelpaste.pixelpaste.logic.BlockManager;
import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class PlayerEvents implements Listener {

    /**
     * Handles chat events, specifically looking for PixelPaste commands ('ppconfirm' and 'ppcancel').
     *
     * @param e The AsyncPlayerChatEvent object.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        if ("ppconfirm".equalsIgnoreCase(message)) {
            boolean success = PendingConfirmation.confirmAndRun(player);
            if (success) {
                BlockManager.restoreOriginalBlocks(player);
                PendingConfirmation.removeOriginalBlocks(player.getUniqueId());
                player.sendMessage(ChatColor.GOLD + "PixelPaste: Building started!");
                e.setCancelled(true);
            }
        } else if ("ppcancel".equalsIgnoreCase(message)) {
            if ((player.isOp() || player.hasPermission("pixelpaste")) && PendingConfirmation.hasPendingConfirmation(player)) {
                player.sendMessage(ChatColor.GOLD + "PixelPaste: You have canceled your build query.");
                PendingConfirmation.removeConfirmation(player);
                BlockManager.restoreOriginalBlocks(player);
                PendingConfirmation.removeOriginalBlocks(player.getUniqueId());
                e.setCancelled(true);
            }
        }
    }
}
