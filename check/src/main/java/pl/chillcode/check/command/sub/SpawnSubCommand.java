package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.config.Config;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class SpawnSubCommand implements SubCommand {
    Config config;
    MessageAPI messageAPI;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            messageAPI.sendMessage("noConsole", sender);
            return;
        }

        final Location spawnLocation = config.getSpawnLocation();
        ((Player) sender).teleport(spawnLocation);
        messageAPI.sendMessage("spawn.spawn", sender);
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
        return "chillcode.check.spawn";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "spawn.usage";
    }
}
