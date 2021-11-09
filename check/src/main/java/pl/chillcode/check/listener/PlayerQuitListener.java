package pl.chillcode.check.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.Check;
import pl.chillcode.check.model.CheckCache;
import pl.chillcode.check.model.CheckResult;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class PlayerQuitListener implements Listener {
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    CheckCache checkCache;
    Config config;

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Check clear = checkCache.clear(player, CheckResult.LOGOUT);
        if (clear == null) {
            return;
        }

        config.getCommandsWhenPlayerLogout().forEach(command -> Bukkit.dispatchCommand(console, command.replace("{PLAYER_NAME}", player.getName())));
    }
}
