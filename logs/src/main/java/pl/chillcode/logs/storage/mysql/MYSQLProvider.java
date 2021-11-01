package pl.chillcode.logs.storage.mysql;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.storage.SQLProvider;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.util.SQLUtil;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class MYSQLProvider extends SQLProvider {
    String selectLastInsertLogId = "SELECT LAST_INSERT_ID();";
    String insertUser;

    public MYSQLProvider(final SQLUtil sqlUtil, final DatabaseConfig databaseConfig) {
        super(sqlUtil, databaseConfig);

        this.insertUser = String.format("INSERT INTO %suser (nickname, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE nickname = ?;", databaseConfig.getPrefix());
    }

    @Override
    public void createUser(final Player player) {
        sqlUtil.executeUpdateAndOpenConnection(insertUser, player.getName(), player.getUniqueId().toString(), player.getName());
    }

    @Override
    public void saveLog(final Log log) {
        saveLog(selectLastInsertLogId, "LAST_INSERT_ID()", log);
    }

    @Override
    public void createTable() {
        final String logsTable = "CREATE TABLE IF NOT EXISTS %slogs\n" +
                "(\n" +
                "    id               INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,\n" +
                "    check_start_time LONG                               NOT NULL,\n" +
                "    player_uuid      CHAR(36)                           NOT NULL,\n" +
                "    admin_uuid       CHAR(36)                           NOT NULL,\n" +
                "    check_result     TINYINT                            NOT NULL,\n" +
                "    check_end_time   LONG                               NOT NULL,\n" +
                "    FOREIGN KEY (player_uuid) REFERENCES user (uuid),\n" +
                "    FOREIGN KEY (admin_uuid) REFERENCES user (uuid)\n" +
                ");";

        final String messagesLogTable = "CREATE TABLE IF NOT EXISTS %smessages_log\n" +
                "(\n" +
                "    id            INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,\n" +
                "    message       TEXT                               NOT NULL,\n" +
                "    sender_uuid   CHAR(36)                           NOT NULL,\n" +
                "    sent_time     LONG                               NOT NULL,\n" +
                "    admin_message BOOLEAN                            NOT NULL,\n" +
                "    log_id        INTEGER                            NOT NULL,\n" +
                "    FOREIGN KEY (sender_uuid) REFERENCES user (uuid),\n" +
                "    FOREIGN KEY (log_id) REFERENCES logs (id)\n" +
                ");";
        createTable(logsTable, messagesLogTable);
    }
}
