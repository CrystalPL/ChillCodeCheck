package pl.chillcode.logs.storage;


import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import pl.chillcode.check.model.CheckResult;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.MessageLog;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.util.SQLFunction;
import pl.crystalek.crcapi.storage.util.SQLUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RequiredArgsConstructor
public abstract class SQLProvider extends Provider {
    protected final SQLUtil sqlUtil;
    private final DatabaseConfig databaseConfig;
    private String selectPlayerUUID;
    private String selectPlayerNickname;
    private String selectMessages;
    private String selectLogs;
    private String insertLogs;
    private String insertMessages;

    @Override
    public Optional<UUID> getPlayerUUID(final String nick) {
        final SQLFunction<ResultSet, Optional<UUID>> function = resultSet -> {
            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(resultSet.getString("uuid")));
        };

        return sqlUtil.executeQueryAndOpenConnection(selectPlayerUUID, function, nick);
    }

    @Override
    public Optional<String> getPlayerNickname(final UUID uuid) {
        final SQLFunction<ResultSet, Optional<String>> function = resultSet -> {
            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(resultSet.getString("nickname"));
        };

        return sqlUtil.executeQueryAndOpenConnection(selectPlayerNickname, function, uuid.toString());
    }

    @Override
    public Optional<List<Log>> getPlayerLogs(final UUID uuid) {
        return sqlUtil.openConnection(connection -> {
            final SQLFunction<ResultSet, Optional<List<Log>>> logFunction = resultSet -> {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                final List<Log> logList = new ArrayList<>();
                final CheckResult[] checkResults = CheckResult.values();

                do {
                    final long checkStartTime = resultSet.getLong("check_start_time");
                    final UUID playerUuid = UUID.fromString(resultSet.getString("player_uuid"));
                    final UUID adminUuid = UUID.fromString((resultSet.getString("admin_uuid")));
                    final CheckResult checkResult = checkResults[resultSet.getByte("check_result")];
                    final long checkEndTime = resultSet.getLong("check_end_time");
                    final int logId = resultSet.getInt("id");

                    final SQLFunction<ResultSet, LinkedList<MessageLog>> messagesFunction = resultSet1 -> {
                        final LinkedList<MessageLog> messageLogList = new LinkedList<>();
                        while (resultSet1.next()) {
                            final String message = resultSet1.getString("message");
                            final UUID senderUuid = UUID.fromString(resultSet1.getString("sender_uuid"));
                            final long sentTime = resultSet1.getLong("sent_time");
                            final boolean adminMessage = resultSet1.getBoolean("admin_message");

                            messageLogList.add(new MessageLog(message, senderUuid, sentTime, adminMessage));
                        }

                        return messageLogList;
                    };

                    final LinkedList<MessageLog> messageLogList = sqlUtil.executeQuery(connection, selectMessages, messagesFunction, logId);
                    logList.add(new Log(checkStartTime, playerUuid, adminUuid, checkResult, checkEndTime, messageLogList));

                } while (resultSet.next());

                return Optional.of(logList);
            };

            return sqlUtil.executeQuery(connection, selectLogs, logFunction, uuid);
        });
    }

    public void saveLog(final String logIdSql, final String logIdColumn, final Log log, final Object... logIdParams) {
        sqlUtil.openConnection(connection -> {
            final String playerUUID = log.getPlayerUUID().toString();
            final String adminUUID = log.getAdminUUID().toString();

            sqlUtil.executeUpdate(connection, insertLogs, log.getCheckStartTime(), playerUUID, adminUUID, log.getCheckResult().ordinal(), log.getCheckEndTime());

            final SQLFunction<ResultSet, Integer> logIdFunction = resultSet -> {
                resultSet.next();
                return resultSet.getInt(logIdColumn);
            };

            final int logId = sqlUtil.executeQuery(connection, logIdSql, logIdFunction, logIdParams);

            @Cleanup final PreparedStatement insertMessageStatement = connection.prepareStatement(insertMessages);
            connection.setAutoCommit(false);

            for (final MessageLog message : log.getMessageLogList()) {
                sqlUtil.completionStatement(insertMessageStatement,
                        message.getMessage(),
                        message.getSenderUUID().toString().equals(playerUUID) ? playerUUID : adminUUID,
                        message.getSentTime(),
                        message.isAdminMessage(),
                        logId);

                insertMessageStatement.addBatch();
            }

            connection.commit();
            insertMessageStatement.executeBatch();
            connection.setAutoCommit(true);
        });
    }

    public void createTable(final String logsTable, final String messagesLogTable) {
        final String prefix = databaseConfig.getPrefix();
        selectPlayerUUID = String.format("SELECT uuid FROM %suser WHERE nickname = ? LIMIT 1;", prefix);
        selectPlayerNickname = String.format("SELECT nickname FROM %suser WHERE uuid = ? LIMIT 1;", prefix);
        selectMessages = String.format("SELECT * FROM %smessages_log WHERE log_id = ? ORDER BY sent_time ASC;", prefix);
        selectLogs = String.format("SELECT * FROM %slogs WHERE player_uuid = ?;", prefix);
        insertLogs = String.format("INSERT INTO %slogs (check_start_time, player_uuid, admin_uuid, check_result, check_end_time) VALUES (?, ?, ?, ?, ?);", prefix);
        insertMessages = String.format("INSERT INTO %smessages_log (message, sender_uuid, sent_time, admin_message, log_id) VALUES (?, ?, ?, ?, ?);", prefix);

        final String userTable = "CREATE TABLE IF NOT EXISTS %suser\n" +
                "(\n" +
                "    nickname VARCHAR(16) NOT NULL,\n" +
                "    uuid     CHAR(36)    NOT NULL UNIQUE PRIMARY KEY\n" +
                ");";

        sqlUtil.openConnection(connection -> {
            @Cleanup final PreparedStatement userTableStatement = connection.prepareStatement(String.format(userTable, prefix));
            @Cleanup final PreparedStatement logsTableStatement = connection.prepareStatement(String.format(logsTable, prefix));
            @Cleanup final PreparedStatement messagesLogStatement = connection.prepareStatement(String.format(messagesLogTable, prefix));

            userTableStatement.executeUpdate();
            logsTableStatement.executeUpdate();
            messagesLogStatement.executeUpdate();
        });
    }
}
