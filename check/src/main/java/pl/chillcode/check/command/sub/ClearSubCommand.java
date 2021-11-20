package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.Check;
import pl.chillcode.check.model.CheckCache;
import pl.chillcode.check.model.CheckResult;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class ClearSubCommand implements SubCommand {
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
            messageAPI.sendMessage("clear.playerNotFound", sender);
            return;
        }

        final Check check = checkCache.clear(player, CheckResult.CLEAR);
        if (check == null) {
            messageAPI.sendMessage("clear.playerNotChecked", sender);
            return;
        }

        messageAPI.sendMessage("clear.clear", player);
        player.teleport(config.isBackPlayerToPreviousLocation() ? check.getPreviousLocation() : config.getTeleportPlayerAfterCheck());
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
        return "chillcode.check.clear";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return checkCache.getPlayerCheckList().stream().map(check -> check.getPlayer().getName()).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String usagePathMessage() {
        return "clear.usage";
    }
}
