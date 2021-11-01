package pl.chillcode.check.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class CheckSubCommand implements SubCommand {
    CheckCache checkCache;
    Config config;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            MessageAPI.sendMessage("noConsole", sender);
            return;
        }

        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            MessageAPI.sendMessage("check.playerNotFound", sender);
            return;
        }

        if (player.getName().equalsIgnoreCase(sender.getName())) {
            MessageAPI.sendMessage("check.selfCheck", sender);
            return;
        }

        if (player.hasPermission("chillcode.check.bypass")) {
            MessageAPI.sendMessage("check.bypass", sender);
            return;
        }

        if (!checkCache.checkPlayer((Player) sender, player)) {
            MessageAPI.sendMessage("check.playerIsAlreadyChecked", sender);
            return;
        }

        MessageAPI.sendMessage("check.checkstarting", sender, ImmutableMap.of("{PLAYER_NAME}", player.getName()));
        player.teleport(config.getSpawnLocation());
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
        return "chillcode.check.check";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String usagePathMessage() {
        return "check.usage";
    }
}
