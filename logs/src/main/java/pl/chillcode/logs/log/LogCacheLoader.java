package pl.chillcode.logs.log;

import com.google.common.cache.CacheLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.storage.Provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
final class LogCacheLoader extends CacheLoader<UUID, List<Log>> {
    Provider provider;

    @Override
    public List<Log> load(final UUID playerUUID) throws LogNotExistException {
        final Optional<List<Log>> logsOptional = provider.getPlayerLogs(playerUUID);
        if (!logsOptional.isPresent()) {
            throw new LogNotExistException("player has no logs");
        }

        return logsOptional.get();
    }
}
