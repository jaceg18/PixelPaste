package github.jaceg18.pixelpaste.pixelpaste.commands.handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Handles commands and tab completion for the PixelPaste plugin.
 */
public class Handler extends CommandHandler implements CommandExecutor, TabCompleter {

    private final File imageFolder;
    /**
     * Constructs a Handler object.
     *
     * @param imageFolder The folder where image files are stored.
     */
    public Handler(File imageFolder){
        this.imageFolder = imageFolder;
    }
    /**
     * Delegates the command handling to the super class.
     *
     * @param sender The sender of the command.
     * @param command The command to be executed.
     * @param label The alias used.
     * @param args The arguments of the command.
     * @return true if the command was handled successfully; false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    /**
     * Provides tab completion suggestions.
     *
     * @param sender The sender of the command.
     * @param command The command for which to provide suggestions.
     * @param label The alias used.
     * @param args The arguments of the command.
     * @return A List of suggested completions for the command.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        String cmdName = command.getName().toLowerCase();


        if ("p3d".equals(cmdName)) {
            if (args.length == 1) {
                handleFileTabComplete(suggestions);
            } else {
                handleP3DTabComplete(args, suggestions);
            }
        } else if ("p2d".equals(cmdName)) {
            if (args.length == 1) {
                handleFileTabComplete(suggestions);
            } else if (args.length == 2) {
                suggestions.addAll(Arrays.asList("100", "max_dimension_size"));
            }
        }

        return suggestions;
    }

    /**
     * Handles the tab complete for p3d command
     * @param args The commandline args
     * @param suggestions The suggestions
     */
    private void handleP3DTabComplete(String[] args, List<String> suggestions) {
        switch (args.length) {
            case 2:
                suggestions.addAll(Arrays.asList("vert", "horz"));
                break;
            case 3:
                suggestions.addAll(Arrays.asList("100", "max_dimension_size"));
                break;
            case 4:
                suggestions.addAll(Arrays.asList("5", "max_depth"));
                break;
            default:
                break;
        }
    }

    /**
     * Completes the file tab complete
     * @param suggestions the files
     */
    private void handleFileTabComplete(List<String> suggestions) {
        File[] files = imageFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
        if (files != null) {
            for (File file : files) {
                suggestions.add(file.getName());
            }
        }
    }
}
