package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.chillcode.check.model.CheckResult;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CheaterSubCommand extends Command {
    CheckCache checkCache;
    Config config;

    public CheaterSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final CheckCache checkCache, final Config config) {
        super(messageAPI, commandDataMap);

        this.checkCache = checkCache;
        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            messageAPI.sendMessage("admitting.playerNotFound", sender);
            return;
        }

        if (checkCache.clear(player, CheckResult.CHEATER) == null) {
            messageAPI.sendMessage("cheater.playerNotChecked", sender);
            return;
        }

        config.getCommandsWhenPlayerCheating().forEach(command -> Bukkit.dispatchCommand(sender, command.replace("{PLAYER_NAME}", player.getName())));
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return checkCache.getPlayerCheckList().stream().map(check -> check.getPlayer().getName()).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String getPermission() {
        return "chillcode.check.cheater";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "cheater.usage";
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
