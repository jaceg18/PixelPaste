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
        String message = e.getMessage().toLowerCase();

        if (message.equals("ppconfirm") || message.equals("ppcancel")) {
            handlePixelPasteCommands(player, message);
            e.setCancelled(true);
        }
    }

    /**
     * Helper method for handling the paste confirmations
     * @param player The player executing the confirmation
     * @param message The confirmation
     */
    private void handlePixelPasteCommands(Player player, String message) {
        if (player.isOp() || player.hasPermission("pixelpaste")) {
            if (message.equals("ppconfirm")) {
                if (PendingConfirmation.confirmAndRun(player)) {
                    BlockManager.restoreOriginalBlocks(player);
                    player.sendMessage(ChatColor.GOLD + "PixelPaste: Building started!");
                }
            } else if (message.equals("ppcancel")) {
                if (PendingConfirmation.hasPendingConfirmation(player)) {
                    player.sendMessage(ChatColor.GOLD + "PixelPaste: You have canceled your build query.");
                    PendingConfirmation.removeConfirmation(player);
                    BlockManager.restoreOriginalBlocks(player);
                }
            }
        }
    }
}
