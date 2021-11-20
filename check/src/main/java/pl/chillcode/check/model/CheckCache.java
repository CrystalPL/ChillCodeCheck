package pl.chillcode.check.model;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.event.CheckEndEvent;
import pl.chillcode.check.event.CheckStartEvent;
import pl.chillcode.check.task.NotifyTask;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CheckCache {
    List<Check> playerCheckList = new ArrayList<>();
    Config config;
    JavaPlugin plugin;
    MessageAPI messageAPI;

    public boolean checkPlayer(final Player admin, final Player player) {
        if (playerCheckList.stream().anyMatch(check -> check.getPlayer().equals(player))) {
            return false;
        }

        final int notifyTime = config.getNotifyTime();
        final int taskId;
        if (notifyTime != 0) {
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new NotifyTask(player, admin.getName(), messageAPI), 0, notifyTime).getTaskId();
        } else {
            taskId = 0;
        }

        playerCheckList.add(new Check(player.getLocation(), admin.getUniqueId(), player, taskId));
        Bukkit.getPluginManager().callEvent(new CheckStartEvent(player, admin));
        if (config.isBroadcastMessage()) {
            messageAPI.broadcast("broadcast.checking", ImmutableMap.of("{PLAYER_NAME}", player.getName(), "{ADMIN_NAME}", admin.getName()));
        }
        return true;
    }

    public Check clear(final Player player, final CheckResult checkResult) {
        final Iterator<Check> iterator = playerCheckList.iterator();
        while (iterator.hasNext()) {
            final Check check = iterator.next();
            if (!check.getPlayer().equals(player)) {
                continue;
            }

            Bukkit.getScheduler().cancelTask(check.getTaskId());
            Bukkit.getPluginManager().callEvent(new CheckEndEvent(player, checkResult));

            if (config.isBroadcastMessage()) {
                messageAPI.broadcast("broadcast." + checkResult.name().toLowerCase(), ImmutableMap.of("{PLAYER_NAME}", player.getName()));
            }

            iterator.remove();

            return check;
        }

        return null;
    }

    public Check getCheck(final Player player) {
        for (final Check check : playerCheckList) {
            if (!check.getPlayer().equals(player)) {
                continue;
            }

            return check;
        }

        return null;
    }
}
