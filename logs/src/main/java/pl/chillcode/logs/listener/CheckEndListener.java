package pl.chillcode.logs.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.chillcode.check.event.CheckEndEvent;
import pl.chillcode.logs.log.LogCache;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CheckEndListener implements Listener {
    LogCache logCache;

    @EventHandler
    public void onCheckEnd(final CheckEndEvent event) {
        logCache.saveLog(event.getPlayer(), event.getCheckResult());
    }
}
