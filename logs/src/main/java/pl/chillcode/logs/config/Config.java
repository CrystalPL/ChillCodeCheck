package pl.chillcode.logs.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.config.DatabaseConfigLoader;
import pl.crystalek.crcapi.util.ColorUtil;

import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config {
    final FileConfiguration config;
    final JavaPlugin plugin;
    DatabaseConfig databaseConfig;
    DateTimeFormatter dateTimeFormatter;
    String playerChatFormat;
    String adminChatFormat;

    public boolean load() {
        try {
            this.databaseConfig = DatabaseConfigLoader.getDatabaseConfig(config.getConfigurationSection("database"), plugin);
        } catch (final ConfigLoadException exception) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas próby załadowania bazy danych");
            Bukkit.getLogger().severe(exception.getMessage());
            return false;
        }

        this.dateTimeFormatter = DateTimeFormatter.ofPattern(config.getString("dateTimeFormatter"));
        this.playerChatFormat = ColorUtil.color(config.getString("chatFormat.playerFormat"));
        this.adminChatFormat = ColorUtil.color(config.getString("chatFormat.adminFormat"));
        return true;
    }
}
