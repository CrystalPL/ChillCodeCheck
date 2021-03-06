package pl.chillcode.logs.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import pl.chillcode.logs.config.Config;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.log.MessageLog;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.util.MessageUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class DetailsSubCommand extends Command {
    ZoneId zoneId = ZoneId.of("Poland");
    Config config;
    LogCache logCache;
    Component detailsComponent;
    Component messageLogComponent;
    ResultUtil resultUtil;

    public DetailsSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config, final LogCache logCache, final Component detailsComponent, final Component messageLogComponent, final ResultUtil resultUtil) {
        super(messageAPI, commandDataMap);

        this.config = config;
        this.logCache = logCache;
        this.detailsComponent = detailsComponent;
        this.messageLogComponent = messageLogComponent;
        this.resultUtil = resultUtil;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final List<Log> logs;
        try {
            logs = logCache.getLogs(args[1]);
        } catch (final LogNotExistException exception) {
            messageAPI.sendMessage("notChecked", sender, ImmutableMap.of("{PLAYER_NAME}", args[1]));
            return;
        } catch (final PlayerNotFoundException exception) {
            messageAPI.sendMessage("playerNotFound", sender);
            return;
        }

        final int logIndex;
        try {
            logIndex = Integer.parseInt(args[2]);
        } catch (final NumberFormatException exception) {
            messageAPI.sendMessage("showDetailsError", sender);
            return;
        }

        final Log log = logs.get(logIndex);

        final DateTimeFormatter dateTimeFormatter = config.getDateTimeFormatter();
        final String checkStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(log.getCheckStartTime()), zoneId).format(dateTimeFormatter);
        final String endStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(log.getCheckEndTime()), zoneId).format(dateTimeFormatter);
        final PlayerNicknameCache playerNicknameCache = logCache.getPlayerNicknameCache();

        final Map<String, Object> detailsReplacements = ImmutableMap.of(
                "{PLAYER_NAME}", args[1],
                "{ADMIN_NAME}", playerNicknameCache.getPlayerNickname(log.getAdminUUID()),
                "{CHECKSTART_TIME}", checkStartTime,
                "{CHECKEND_TIME}", endStartTime
        );

        final LinkedList<MessageLog> messageLogList = log.getMessageLogList();
        final int messageLogListSize = messageLogList.size();
        Component messageListComponent = Component.empty();
        for (int i = 0; i < messageLogListSize; i++) {
            final MessageLog message = messageLogList.get(i);

            final String messageSentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getSentTime()), zoneId).format(dateTimeFormatter);
            final String chatFormat = (message.isAdminMessage() ? config.getAdminChatFormat() : config.getPlayerChatFormat())
                    .replace("{MESSAGE}", message.getMessage())
                    .replace("{PLAYER_NAME}", playerNicknameCache.getPlayerNickname(message.getSenderUUID()));

            final Map<String, Object> messageLogReplacements = ImmutableMap.of(
                    "{CHAT_FORMAT}", chatFormat,
                    "{MESSAGE_SENT_TIME}", messageSentTime
            );

            final Component messageComponent = MessageUtil.replace(messageLogComponent, messageLogReplacements);
            messageListComponent = messageListComponent.append(messageComponent);
            if (i < messageLogListSize - 1) {
                messageListComponent = messageListComponent.append(Component.newline());
            }
        }

        final Map<String, Component> detailsComponentReplacements = ImmutableMap.of(
                "{RESULT}", resultUtil.getResultComponent(log.getCheckResult()),
                "{MESSAGE_LIST}", messageListComponent);

        final Component component = MessageUtil.replace(detailsComponent, detailsReplacements);
        messageAPI.sendMessage(component, sender, detailsComponentReplacements);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.check.base";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "showDetailsError";
    }

    @Override
    public int maxArgumentLength() {
        return 3;
    }

    @Override
    public int minArgumentLength() {
        return 3;
    }
}
