package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.config.Config;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SpawnSubCommand extends Command {
    Config config;

    public SpawnSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config) {
        super(messageAPI, commandDataMap);

        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        ((Player) sender).teleport(config.getSpawnLocation());
        messageAPI.sendMessage("spawn.spawn", sender);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.check.spawn";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "spawn.usage";
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
