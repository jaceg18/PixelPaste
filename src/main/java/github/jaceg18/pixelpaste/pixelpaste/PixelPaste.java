package github.jaceg18.pixelpaste.pixelpaste;

import github.jaceg18.pixelpaste.pixelpaste.commands.Pixel3DCommand;
import github.jaceg18.pixelpaste.pixelpaste.commands.PixelCommand;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
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

        File folder2 = new File(getDataFolder() + "/pixelart3D");
        if (!folder2.exists()) {
            folder2.mkdirs();
        }

        PixelCommand pixelArtCommand = new PixelCommand(folder);
        Objects.requireNonNull(this.getCommand("pixelpaste")).setExecutor(pixelArtCommand);
        Objects.requireNonNull(this.getCommand("pixelpaste")).setTabCompleter(pixelArtCommand);

        Pixel3DCommand pixel3DCommand = new Pixel3DCommand(folder2);
        Objects.requireNonNull(this.getCommand("pixelpaste3D")).setExecutor(pixel3DCommand);
        Objects.requireNonNull(this.getCommand("pixelpaste3D")).setTabCompleter(pixel3DCommand);

        instance = this;

    }

    public static PixelPaste getInstance(){
        return instance;
    }

    public static Material getColorBlock(int pixelColor) {
        int alpha = (pixelColor >> 24) & 0xff;
        if (alpha == 0) {
            return Material.AIR;
        }

        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;
        Color givenColor = new Color(red, green, blue);

        // Define wool colors
        Material[] woolTypes = {
                Material.WHITE_WOOL,
                Material.ORANGE_WOOL,
                Material.MAGENTA_WOOL,
                Material.LIGHT_BLUE_WOOL,
                Material.YELLOW_WOOL,
                Material.LIME_WOOL,
                Material.PINK_WOOL,
                Material.GRAY_WOOL,
                Material.LIGHT_GRAY_WOOL,
                Material.CYAN_WOOL,
                Material.PURPLE_WOOL,
                Material.BLUE_WOOL,
                Material.BROWN_WOOL,
                Material.GREEN_WOOL,
                Material.RED_WOOL,
                Material.BLACK_WOOL
        };

        int[][] woolColors = {
                {233, 236, 236}, // WHITE
                {241, 118, 19},  // ORANGE
                {189, 68, 179},  // MAGENTA
                {58, 175, 217},  // LIGHT BLUE
                {248, 197, 39},  // YELLOW
                {112, 185, 26},  // LIME
                {237, 141, 172}, // PINK
                {63, 68, 72},    // GRAY
                {142, 142, 134}, // LIGHT GRAY
                {21, 137, 145},  // CYAN
                {120, 71, 171},  // PURPLE
                {53, 57, 157},   // BLUE
                {114, 71, 40},   // BROWN
                {85, 106, 27},   // GREEN
                {162, 38, 35},   // RED
                {20, 21, 26}     // BLACK
        };
        double closestDistance = Double.MAX_VALUE;
        Material closestWool = Material.BLACK_WOOL;

        // Loop through wool colors to find the closest match
        for (int i = 0; i < woolTypes.length; i++) {
            Color woolColor = new Color(woolColors[i][0], woolColors[i][1], woolColors[i][2]);
            double distance = colorDistance(givenColor, woolColor);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestWool = woolTypes[i];
            }
        }

        return closestWool;
    }

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
