package pl.chillcode.logs.user;

import com.google.common.cache.CacheLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.storage.Provider;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
final class PlayerUUIDCacheLoader extends CacheLoader<String, UUID> {
    Provider provider;

    @Override
    public UUID load(final String nick) throws PlayerNotFoundException {
        final Optional<UUID> playerUUID = provider.getPlayerUUID(nick);
        if (!playerUUID.isPresent()) {
            throw new PlayerNotFoundException("player not found");
        }

        return playerUUID.get();
    }
}
