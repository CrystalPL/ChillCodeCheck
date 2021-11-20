package pl.chillcode.check.task;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class NotifyTask implements Runnable {
    Player player;
    String adminName;
    MessageAPI messageAPI;

    @Override
    public void run() {
        messageAPI.sendMessage("check.checkingMessage", player, ImmutableMap.of("{ADMIN_NAME}", adminName));
    }
}
