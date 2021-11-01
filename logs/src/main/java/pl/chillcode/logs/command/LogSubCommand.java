package pl.chillcode.logs.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.check.model.CheckResult;
import pl.chillcode.logs.config.Config;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.lib.adventure.text.Component;
import pl.crystalek.crcapi.lib.adventure.text.event.ClickEvent;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.message.loader.MessageUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class LogSubCommand implements SubCommand {
    ZoneId zoneId = ZoneId.of("Poland");
    Config config;
    JavaPlugin plugin;
    LogCache logCache;
    Component showLogsComponent;
    String runningCommand;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final List<Log> logs;
            try {
                logs = logCache.getLogs(args[1]);
            } catch (final PlayerNotFoundException exception) {
                MessageAPI.sendMessage("playerNotFound", sender);
                return;
            } catch (final LogNotExistException exception) {
                MessageAPI.sendMessage("notChecked", sender, ImmutableMap.of("{PLAYER_NAME}", args[1]));
                return;
            }

            final DateTimeFormatter dateTimeFormatter = config.getDateTimeFormatter();
            final PlayerNicknameCache playerNicknameCache = logCache.getPlayerNicknameCache();

            for (int i = 0; i < logs.size(); i++) {
                final Log log = logs.get(i);

                final String checkStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(log.getCheckStartTime()), zoneId).format(dateTimeFormatter);
                final Component showLogsComponent = this.showLogsComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, runningCommand + args[1] + " " + i));

                final Map<String, Object> replacements = ImmutableMap.of(
                        "{ADMIN_NAME}", playerNicknameCache.getPlayerNickname(log.getAdminUUID()),
                        "{DATE}", checkStartTime
                );
                final Component component = MessageUtil.replace(showLogsComponent, replacements);
                MessageAPI.sendMessage(component, sender, ImmutableMap.of("{RESULT}", ResultUtil.getResultComponent(log.getCheckResult())));
            }
        });
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "chillcode.check.logs";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "log.usage";
    }
}
