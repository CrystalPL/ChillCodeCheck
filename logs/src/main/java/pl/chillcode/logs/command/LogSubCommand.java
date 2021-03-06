package pl.chillcode.logs.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.logs.config.Config;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.lib.adventure.adventure.text.event.ClickEvent;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.util.MessageUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class LogSubCommand extends Command {
    ZoneId zoneId = ZoneId.of("Poland");
    Config config;
    JavaPlugin plugin;
    LogCache logCache;
    Component showLogsComponent;
    String runningCommand;
    ResultUtil resultUtil;

    public LogSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config, final JavaPlugin plugin, final LogCache logCache, final Component showLogsComponent, final String runningCommand, final ResultUtil resultUtil) {
        super(messageAPI, commandDataMap);

        this.config = config;
        this.plugin = plugin;
        this.logCache = logCache;
        this.showLogsComponent = showLogsComponent;
        this.runningCommand = runningCommand;
        this.resultUtil = resultUtil;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final List<Log> logs;
            try {
                logs = logCache.getLogs(args[1]);
            } catch (final PlayerNotFoundException exception) {
                messageAPI.sendMessage("playerNotFound", sender);
                return;
            } catch (final LogNotExistException exception) {
                messageAPI.sendMessage("notChecked", sender, ImmutableMap.of("{PLAYER_NAME}", args[1]));
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
                messageAPI.sendMessage(component, sender, ImmutableMap.of("{RESULT}", resultUtil.getResultComponent(log.getCheckResult())));
            }
        });
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.check.logs";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "logUsage";
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }
}
