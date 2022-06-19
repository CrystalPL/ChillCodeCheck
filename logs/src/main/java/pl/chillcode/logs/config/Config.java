package pl.chillcode.logs.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.core.config.ConfigHelper;
import pl.crystalek.crcapi.core.config.ConfigParserUtil;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.core.util.ColorUtil;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.database.config.DatabaseConfigLoader;

import java.time.format.DateTimeFormatter;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config extends ConfigHelper {
    DatabaseConfig databaseConfig;
    DateTimeFormatter dateTimeFormatter;
    String playerChatFormat;
    String adminChatFormat;

    public Config(final JavaPlugin plugin, final String fileName) {
        super(plugin, fileName);
    }

    public void loadConfig() throws ConfigLoadException {
        this.databaseConfig = DatabaseConfigLoader.getDatabaseConfig(configuration.getConfigurationSection("database"), plugin);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(ConfigParserUtil.getString(configuration, "dateTimeFormatter"));
        this.playerChatFormat = ColorUtil.color(ConfigParserUtil.getString(configuration, "chatFormat.playerFormat"));
        this.adminChatFormat = ColorUtil.color(ConfigParserUtil.getString(configuration, "chatFormat.adminFormat"));
    }
}
