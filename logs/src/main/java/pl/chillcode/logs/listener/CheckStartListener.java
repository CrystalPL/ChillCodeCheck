package pl.chillcode.logs.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.chillcode.check.event.CheckStartEvent;
import pl.chillcode.logs.log.LogCache;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class CheckStartListener implements Listener {
    LogCache logCache;

    @EventHandler
    public void onCheckStart(final CheckStartEvent event) {
        logCache.startLog(event.getPlayer(), event.getAdmin());
    }
}
