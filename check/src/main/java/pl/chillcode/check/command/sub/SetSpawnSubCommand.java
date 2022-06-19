package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.chillcode.check.config.Config;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SetSpawnSubCommand extends Command {
    Config config;

    public SetSpawnSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config) {
        super(messageAPI, commandDataMap);

        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
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
            messageAPI.sendMessage("setspawn.saveError", player);
            exception.printStackTrace();
            return;
        }

        config.setSpawnLocation(location);
        messageAPI.sendMessage("setspawn.setspawn", player);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.check.setspawn";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "setspawn.usage";
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
