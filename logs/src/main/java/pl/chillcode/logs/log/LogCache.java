package pl.chillcode.logs.log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ExecutionError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.check.model.CheckResult;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.storage.Provider;
import pl.chillcode.logs.user.PlayerNicknameCache;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class LogCache {
    LoadingCache<UUID, List<Log>> playerLogsCache;
    Map<UUID, Log> checkingPlayerLog = new HashMap<>();
    @Getter
    PlayerNicknameCache playerNicknameCache;
    Provider provider;
    JavaPlugin plugin;

    public LogCache(final Provider provider, final PlayerNicknameCache playerNicknameCache, final JavaPlugin plugin) {
        this.playerNicknameCache = playerNicknameCache;
        this.provider = provider;

        this.playerLogsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new LogCacheLoader(provider));
        this.plugin = plugin;
    }

    //used when check player starting
    public void startLog(final Player player, final Player admin) {
        checkingPlayerLog.put(player.getUniqueId(), new Log(System.currentTimeMillis(), player.getUniqueId(), admin.getUniqueId()));
    }

    //used when check player end
    public void saveLog(final Player player, final CheckResult checkResult) {
        final UUID playerUUID = player.getUniqueId();
        final Log log = checkingPlayerLog.remove(playerUUID);

        log.setCheckResult(checkResult);
        log.setCheckEndTime(System.currentTimeMillis());

        try {
            final List<Log> logList = getLogs(playerUUID);
            logList.add(log);
        } catch (final LogNotExistException exception) {
            playerLogsCache.put(playerUUID, new ArrayList<>(ImmutableList.of(log)));
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> provider.saveLog(log));
    }

    public void addMessage(final Player player, final MessageLog messageLog) {
        checkingPlayerLog.get(player.getUniqueId()).getMessageLogList().add(messageLog);
    }

    public List<Log> getLogs(final String nick) throws LogNotExistException, PlayerNotFoundException {
        return getLogs(playerNicknameCache.getPlayerUUID(nick));
    }

    public List<Log> getLogs(final UUID playerUUID) throws LogNotExistException {
        try {
            return playerLogsCache.get(playerUUID);
        } catch (final ExecutionError | ExecutionException exception) {
            throw new LogNotExistException("player has no logs");
        }
    }
}
