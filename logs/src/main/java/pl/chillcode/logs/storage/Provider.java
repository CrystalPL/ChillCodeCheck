package pl.chillcode.logs.storage;

import org.bukkit.entity.Player;
import pl.chillcode.logs.log.Log;
import pl.crystalek.crcapi.database.provider.BaseProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Provider extends BaseProvider {

    void createUser(final Player player);

    Optional<UUID> getPlayerUUID(final String nick);

    Optional<String> getPlayerNickname(final UUID uuid);

    Optional<List<Log>> getPlayerLogs(final UUID uuid);

    void saveLog(final Log log);
}
