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
final class PlayerNicknameCacheLoader extends CacheLoader<UUID, String> {
    Provider provider;

    @Override
    public String load(final UUID uuid) throws PlayerNotFoundException {
        final Optional<String> playerNickname = provider.getPlayerNickname(uuid);
        if (!playerNickname.isPresent()) {
            throw new PlayerNotFoundException("player not found");
        }

        return playerNickname.get();
    }
}
