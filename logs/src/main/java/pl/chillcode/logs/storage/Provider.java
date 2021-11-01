package pl.chillcode.logs.storage;

import org.bukkit.entity.Player;
import pl.chillcode.logs.log.Log;
import pl.crystalek.crcapi.storage.BaseProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class Provider extends BaseProvider {

    public abstract void createUser(final Player player);

    public abstract Optional<UUID> getPlayerUUID(final String nick);

    public abstract Optional<String> getPlayerNickname(final UUID uuid);

    public abstract Optional<List<Log>> getPlayerLogs(final UUID uuid);

    public abstract void saveLog(final Log log);
}
