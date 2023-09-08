package github.jaceg18.pixelpaste.pixelpaste;

import github.jaceg18.pixelpaste.pixelpaste.commands.Pixel3DCommand;
import github.jaceg18.pixelpaste.pixelpaste.commands.PixelCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public final class PixelPaste extends JavaPlugin {

    private static PixelPaste instance;


    /**
     * Executed when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        initializeFolders(new String[]{"/pixelart", "/pixelart3D"});

        PixelCommand pixelCommand = new PixelCommand(getFolder("/pixelart"));
        setCommandExecutor("pixelpaste", pixelCommand);

        Pixel3DCommand pixel3DCommand = new Pixel3DCommand(getFolder("/pixelart3D"));
        setCommandExecutor("pixelpaste3D", pixel3DCommand);

        instance = this;
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
            double distance = colorDistance(givenColor, blockColor);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBlock = blockTypes[i];
            }
        }

        return closestBlock;
    }


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

}
