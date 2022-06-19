package pl.chillcode.check.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.message.api.MessageAPI;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerCommandListener implements Listener {
    CheckCache checkCache;
    Config config;
    MessageAPI messageAPI;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (checkCache.getPlayerCheckList().stream().noneMatch(check -> check.getPlayer().equals(player))) {
            return;
        }

        final String command = event.getMessage().toLowerCase().split(" ")[0].substring(1);
        if (config.getAllowedCommands().stream().anyMatch(allowedCommand -> allowedCommand.equalsIgnoreCase(command))) {
            return;
        }

        messageAPI.sendMessage("useCommandWhileChecking", player);
        event.setCancelled(true);
    }
}
