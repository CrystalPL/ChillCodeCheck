package pl.chillcode.check.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import pl.chillcode.check.model.Check;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.message.api.MessageAPI;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerDropItemListener implements Listener {
    CheckCache checkCache;
    MessageAPI messageAPI;

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();

        final Check check = checkCache.getCheck(player);
        if (check == null) {
            return;
        }

        messageAPI.sendMessage("dropItemWhileChecking", player);
        event.setCancelled(true);
    }
}
