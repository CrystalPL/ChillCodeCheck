package pl.chillcode.check.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CheckSubCommand extends Command {
    CheckCache checkCache;
    Config config;

    public CheckSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final CheckCache checkCache, final Config config) {
        super(messageAPI, commandDataMap);

        this.checkCache = checkCache;
        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            messageAPI.sendMessage("check.playerNotFound", sender);
            return;
        }

        if (player.getName().equalsIgnoreCase(sender.getName())) {
            messageAPI.sendMessage("check.selfCheck", sender);
            return;
        }

        if (player.hasPermission("chillcode.check.bypass")) {
            messageAPI.sendMessage("check.bypass", sender);
            return;
        }

        if (!checkCache.checkPlayer((Player) sender, player)) {
            messageAPI.sendMessage("check.playerIsAlreadyChecked", sender);
            return;
        }

        messageAPI.sendMessage("check.checkstarting", sender, ImmutableMap.of("{PLAYER_NAME}", player.getName()));
        player.teleport(config.getSpawnLocation());
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String getPermission() {
        return "chillcode.check.check";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "check.usage";
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }
}
