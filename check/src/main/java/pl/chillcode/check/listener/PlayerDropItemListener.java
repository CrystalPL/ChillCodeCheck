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
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class PlayerDropItemListener implements Listener {
    CheckCache checkCache;

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();

        final Check check = checkCache.getCheck(player);
        if (check == null) {
            return;
        }

        MessageAPI.sendMessage("dropItemWhileChecking", player);
        event.setCancelled(true);
    }
}
