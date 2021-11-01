package pl.chillcode.check.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class NotifyTask implements Runnable{
    Player player;

    @Override
    public void run() {
        MessageAPI.sendMessage("check.checkingMessage", player);
    }
}
