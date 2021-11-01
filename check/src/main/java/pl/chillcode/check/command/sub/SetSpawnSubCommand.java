package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.config.Config;
import pl.crystalek.crcapi.config.FileHelper;
import pl.crystalek.crcapi.message.MessageAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class SetSpawnSubCommand implements SubCommand {
    Config config;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            MessageAPI.sendMessage("noConsole", sender);
            return;
        }

        final Player player = (Player) sender;
        final Location location = player.getLocation();

        final FileHelper spawnLocationFileHelper = config.getSpawnLocationFileHelper();
        final FileConfiguration spawnConfiguration = spawnLocationFileHelper.getConfiguration();
        spawnConfiguration.set("world", location.getWorld().getName());
        spawnConfiguration.set("x", location.getX());
        spawnConfiguration.set("y", location.getY());
        spawnConfiguration.set("z", location.getZ());
        spawnConfiguration.set("yaw", location.getYaw());
        spawnConfiguration.set("pitch", location.getPitch());
        try {
            spawnLocationFileHelper.save();
        } catch (final IOException exception) {
            MessageAPI.sendMessage("setspawn.saveError", player);
            exception.printStackTrace();
            return;
        }

        config.setSpawnLocation(location);
        MessageAPI.sendMessage("setspawn.setspawn", player);
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "chillcode.check.setspawn";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "setspawn.usage";
    }
}
