package pl.chillcode.check.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.event.AsyncCheckMessageEvent;
import pl.chillcode.check.model.Check;
import pl.chillcode.check.model.CheckCache;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class AsyncPlayerChatListener implements Listener {
    CheckCache checkCache;
    Config config;

    private static void removeRecipients(final Set<Player> recipients, final Check log) {
        final Iterator<Player> iterator = recipients.iterator();
        final UUID playerUUID = log.getPlayer().getUniqueId();
        while (iterator.hasNext()) {
            final Player recipient = iterator.next();
            final UUID adminUUID = log.getAdminUUID();

            if (recipient.getUniqueId().equals(playerUUID)) {
                continue;
            }

            if (recipient.getUniqueId().equals(adminUUID)) {
                continue;
            }

            iterator.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage();

        Check check = checkCache.getCheck(player);
        boolean adminMessage = false;

        if (check != null) {
            removeRecipients(event.getRecipients(), check);
            setFormat(config.getPlayerChatFormat(), event);
            event.setCancelled(false);
        } else {
            final UUID playerUUID = player.getUniqueId();

            for (final Check adminCheck : checkCache.getPlayerCheckList()) {
                if (adminCheck.getAdminUUID().equals(playerUUID)) {
                    if (message.charAt(0) == config.getAdminChatChar() && message.trim().length() > 1) {
                        check = adminCheck;
                        adminMessage = true;

                        removeRecipients(event.getRecipients(), adminCheck);
                        final String messageWithoutStartChar = message.substring(1);
                        event.setMessage(messageWithoutStartChar);
                        message = messageWithoutStartChar;

                        setFormat(config.getAdminChatFormat(), event);
                    } else {
                        event.getRecipients().removeIf(player1 -> player1.equals(adminCheck.getPlayer()));
                    }

                    break;
                }

                event.getRecipients().removeIf(player1 -> player1.equals(adminCheck.getPlayer()));
            }
        }

        if (check == null) {
            return;
        }

        Bukkit.getPluginManager().callEvent(new AsyncCheckMessageEvent(check, message, player.getUniqueId(), adminMessage));
    }

    private void setFormat(final String format, final AsyncPlayerChatEvent event) {
        final String formatToSet = format
                .replace("{PLAYER_NAME}", "%1$s")
                .replace("{MESSAGE}", "%2$s");

        event.setFormat(formatToSet);
    }
}
