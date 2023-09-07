package github.jaceg18.pixelpaste.pixelpaste;

import github.jaceg18.pixelpaste.pixelpaste.commands.PixelCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class PixelPaste extends JavaPlugin {

    private static PixelPaste instance;
    @Override
    public void onEnable() {
        // Create folder if it doesn't exist
        File folder = new File(getDataFolder() + "/pixelart");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        PixelCommand pixelArtCommand = new PixelCommand(folder);
        Objects.requireNonNull(this.getCommand("pixelpaste")).setExecutor(pixelArtCommand);
        Objects.requireNonNull(this.getCommand("pixelpaste")).setTabCompleter(pixelArtCommand);

        instance = this;

    }

    public static PixelPaste getInstance(){
        return instance;
    }

}
