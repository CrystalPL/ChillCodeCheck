package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.chillcode.check.model.CheckResult;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class CheaterSubCommand implements SubCommand {
    CheckCache checkCache;
    Config config;
    MessageAPI messageAPI;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            messageAPI.sendMessage("noConsole", sender);
            return;
        }

        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            messageAPI.sendMessage("admitting.playerNotFound", sender);
            return;
        }

        if (checkCache.clear(player, CheckResult.CHEATER) == null) {
            messageAPI.sendMessage("cheater.playerNotChecked", sender);
            return;
        }

        config.getCommandsWhenPlayerAdmitting().forEach(command -> Bukkit.dispatchCommand(sender, command.replace("{PLAYER_NAME}", player.getName())));
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "chillcode.check.cheater";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return checkCache.getPlayerCheckList().stream().map(check -> check.getPlayer().getName()).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String usagePathMessage() {
        return "cheater.usage";
    }
}
