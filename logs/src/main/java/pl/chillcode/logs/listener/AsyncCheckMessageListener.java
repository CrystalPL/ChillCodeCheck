package pl.chillcode.logs.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.chillcode.check.event.AsyncCheckMessageEvent;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.log.MessageLog;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class AsyncCheckMessageListener implements Listener {
    LogCache logCache;

    @EventHandler
    public void onMessage(final AsyncCheckMessageEvent event) {
        final MessageLog messageLog = new MessageLog(event.getMessage(), event.getSenderUUID(), System.currentTimeMillis(), event.isAdminMessage());
        logCache.addMessage(event.getCheck().getPlayer(), messageLog);
    }
}
