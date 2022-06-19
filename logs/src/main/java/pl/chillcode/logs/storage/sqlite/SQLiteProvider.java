package pl.chillcode.logs.storage.sqlite;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.storage.SQLProvider;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.lib.hikari.HikariDataSource;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SQLiteProvider extends SQLProvider {
    String insertUser;
    String selectLastInsertLogId;

    public SQLiteProvider(final DatabaseConfig databaseConfig, final HikariDataSource database) {
        super(databaseConfig, database);

        this.insertUser = String.format("INSERT OR REPLACE INTO %suser (nickname, uuid) VALUES (?, ?)", databaseConfig.getPrefix());
        this.selectLastInsertLogId = String.format("SELECT id from %slogs where check_start_time = ?;", databaseConfig.getPrefix());
        this.createTable();
    }

    @Override
    public void createUser(final Player player) {
        executeUpdateAndOpenConnection(insertUser, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public void saveLog(final Log log) {
        saveLog(selectLastInsertLogId, "id", log, log.getCheckStartTime());
    }

    @Override
    public void createTable() {
        final String logsTable = "CREATE TABLE IF NOT EXISTS %slogs\n" +
                "(\n" +
                "    id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
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
                "    id            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
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
