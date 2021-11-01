package pl.chillcode.logs.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.config.DatabaseConfigLoader;
import pl.crystalek.crcapi.util.ColorUtil;
import pl.crystalek.crcapi.util.LogUtil;

import java.time.format.DateTimeFormatter;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class Config {
    final FileConfiguration config;
    DatabaseConfig databaseConfig;
    DateTimeFormatter dateTimeFormatter;
    String playerChatFormat;
    String adminChatFormat;

    public boolean load() {
        try {
            this.databaseConfig = DatabaseConfigLoader.getDatabaseConfig(config.getConfigurationSection("database"));
        } catch (final ConfigLoadException exception) {
            LogUtil.error("Wystąpił błąd podczas próby załadowania bazy danych");
            LogUtil.error(exception.getMessage());
            return false;
        }

        this.dateTimeFormatter = DateTimeFormatter.ofPattern(config.getString("dateTimeFormatter"));
        this.playerChatFormat = ColorUtil.color(config.getString("chatFormat.playerFormat"));
        this.adminChatFormat = ColorUtil.color(config.getString("chatFormat.adminFormat"));
        return true;
    }
}
