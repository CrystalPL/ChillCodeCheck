package pl.chillcode.logs.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import pl.chillcode.check.command.SubCommand;
import pl.chillcode.logs.config.Config;
import pl.chillcode.logs.exception.LogNotExistException;
import pl.chillcode.logs.exception.PlayerNotFoundException;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.log.MessageLog;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.lib.adventure.text.Component;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.message.loader.MessageUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class DetailsSubCommand implements SubCommand {
    ZoneId zoneId = ZoneId.of("Poland");
    Config config;
    LogCache logCache;
    Component detailsComponent;
    Component messageLogComponent;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final List<Log> logs;
        try {
            logs = logCache.getLogs(args[1]);
        } catch (final LogNotExistException exception) {
            MessageAPI.sendMessage("notChecked", sender, ImmutableMap.of("{PLAYER_NAME}", args[1]));
            return;
        } catch (final PlayerNotFoundException exception) {
            MessageAPI.sendMessage("playerNotFound", sender);
            return;
        }

        final int logIndex;
        try {
            logIndex = Integer.parseInt(args[2]);
        } catch (final NumberFormatException exception) {
            MessageAPI.sendMessage("showDetailsError", sender);
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
            final Map<String, Object> messageLogReplacements = ImmutableMap.of(
                    "{CHAT_FORMAT}", message.isAdminMessage() ? config.getAdminChatFormat() : config.getPlayerChatFormat(),
                    "{MESSAGE}", message.getMessage(),
                    "{PLAYER_NAME}", playerNicknameCache.getPlayerNickname(message.getSenderUUID()),
                    "{MESSAGE_SENT_TIME}", messageSentTime
            );

            final Component messageComponent = MessageUtil.replace(messageLogComponent, messageLogReplacements);
            messageListComponent = messageListComponent.append(messageComponent);
            if (i < messageLogListSize - 1) {
                messageListComponent = messageListComponent.append(Component.newline());
            }
        }

        final Map<String, Component> detailsComponentReplacements = ImmutableMap.of(
                "{RESULT}", ResultUtil.getResultComponent(log.getCheckResult()),
                "{MESSAGE_LIST}", messageListComponent);

        final Component component = MessageUtil.replace(detailsComponent, detailsReplacements);
        MessageAPI.sendMessage(component, sender, detailsComponentReplacements);
    }

    @Override
    public int maxArgumentLength() {
        return 3;
    }

    @Override
    public int minArgumentLength() {
        return 3;
    }

    @Override
    public String getPermission() {
        return "chillcode.check.base";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "usage";
    }
}
