package pl.chillcode.logs.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.storage.Provider;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerNicknameCache {
    LoadingCache<String, UUID> playerUUIDCache;
    LoadingCache<UUID, String> playerNicknameCache;

    public PlayerNicknameCache(final Provider provider) {
        playerUUIDCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new PlayerUUIDCacheLoader(provider));

        playerNicknameCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new PlayerNicknameCacheLoader(provider));
    }

    public String getPlayerNickname(final UUID playerUUID) throws PlayerNotFoundException {
        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return player.getName();
        }

        try {
            return playerNicknameCache.get(playerUUID);
        } catch (final Exception exception) {
            throw new PlayerNotFoundException("player not found");
        }
    }

    public UUID getPlayerUUID(final String nick) throws PlayerNotFoundException {
        final Player player = Bukkit.getPlayer(nick);
        if (player != null) {
            return player.getUniqueId();
        }

        try {
            return playerUUIDCache.get(nick);
        } catch (final Exception exception) {
            throw new PlayerNotFoundException("player not found");
        }
    }
}
