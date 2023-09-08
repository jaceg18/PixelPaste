package github.jaceg18.pixelpaste.pixelpaste;

import github.jaceg18.pixelpaste.pixelpaste.commands.handlers.Handler;
import github.jaceg18.pixelpaste.pixelpaste.events.PlayerEvents;
import github.jaceg18.pixelpaste.pixelpaste.player.PendingConfirmation;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class PixelPaste extends JavaPlugin {

    private static PixelPaste instance;

    public static HashMap<UUID, PendingConfirmation> pendingConfirmations;
    /**
     * Executed when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        initializeFolders(new String[]{"/pixelart"});

        Handler handler = new Handler(getFolder("/pixelart"));
        setCommandExecutor("p2d", handler);
        setCommandExecutor("p3d", handler);

        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);


        instance = this;
        pendingConfirmations = new HashMap<>();
    }


    /**
     * Creates or loads folders
     * @param folderPaths The path to the folders
     */
    private void initializeFolders(String[] folderPaths) {
        for (String path : folderPaths) {
            File folder = getFolder(path);
            if (!folder.exists()) {
                getServer().getConsoleSender().sendMessage(folder.mkdirs() ? "Image folders created" : "(CRITICAL) Failed to create image folders");
            }
        }
    }


    /**
     * Gets the folder as a file object.
     * @param path Path to folder
     * @return Folder file
     */
    private File getFolder(String path) {
        return new File(getDataFolder() + path);
    }


    /**
     * Sets the command executors
     * @param commandName The name of the command
     * @param executor The executor to set
     */
    private void setCommandExecutor(String commandName, CommandExecutor executor) {
        Objects.requireNonNull(this.getCommand(commandName)).setExecutor(executor);
        Objects.requireNonNull(this.getCommand(commandName)).setTabCompleter((TabCompleter) executor);
    }


    /**
     * Gets the singleton instance of this class.
     *
     * @return the singleton instance.
     */
    public static PixelPaste getInstance() {
        return instance;
    }

}
