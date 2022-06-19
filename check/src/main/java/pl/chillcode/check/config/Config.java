package pl.chillcode.check.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.loader.CommandLoader;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.ConfigHelper;
import pl.crystalek.crcapi.core.config.ConfigParserUtil;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.core.util.ColorUtil;

import java.util.List;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config extends ConfigHelper {
    final FileHelper spawnLocationFileHelper;
    Map<Class<? extends Command>, CommandData> commandDataMap;

    char adminChatChar;
    List<String> commandsWhenPlayerCheating;
    List<String> commandsWhenPlayerAdmitting;
    List<String> commandsWhenPlayerLogout;
    boolean broadcastMessage;
    boolean backPlayerToPreviousLocation;
    Location teleportPlayerAfterCheck;
    boolean dropItem;
    boolean allowedUseCommands;
    List<String> allowedCommands;
    int notifyTime;
    @Setter
    Location spawnLocation;
    String playerChatFormat;
    String adminChatFormat;

    public Config(final JavaPlugin plugin, final String fileName, final FileHelper spawnLocationFileHelper) {
        super(plugin, fileName);

        this.spawnLocationFileHelper = spawnLocationFileHelper;
    }

    public void loadConfig() throws ConfigLoadException {
        this.commandDataMap = CommandLoader.loadCommands(configuration.getConfigurationSection("command"), plugin.getClass().getClassLoader());
        this.adminChatChar = ConfigParserUtil.getString(configuration, "adminChatChar").charAt(0);
        this.commandsWhenPlayerCheating = ColorUtil.color(ConfigParserUtil.getStringList(configuration, "commandsWhenPlayerCheating"));
        this.commandsWhenPlayerAdmitting = ColorUtil.color(ConfigParserUtil.getStringList(configuration, "commandsWhenPlayerAdmitting"));
        this.commandsWhenPlayerLogout = ColorUtil.color(ConfigParserUtil.getStringList(configuration, "commandsWhenPlayerLogout"));
        this.broadcastMessage = ConfigParserUtil.getBoolean(configuration, "broadcastMessage");
        this.backPlayerToPreviousLocation = ConfigParserUtil.getBoolean(configuration, "backPlayerToPreviousLocation");
        this.teleportPlayerAfterCheck = ConfigParserUtil.getLocation(configuration.getConfigurationSection("teleportPlayerAfterCheck"));
        this.dropItem = ConfigParserUtil.getBoolean(configuration, "dropItem");
        this.allowedUseCommands = ConfigParserUtil.getBoolean(configuration, "allowedUseCommands");
        this.allowedCommands = ConfigParserUtil.getStringList(configuration, "allowedCommands");
        this.notifyTime = ConfigParserUtil.getInt(configuration, "notifyTime");
        this.spawnLocation = ConfigParserUtil.getLocation(spawnLocationFileHelper.getConfiguration().getConfigurationSection("checkSpawnLocation"));
        this.playerChatFormat = ColorUtil.color(ConfigParserUtil.getString(configuration, "chatFormat.playerFormat"));
        this.adminChatFormat = ColorUtil.color(ConfigParserUtil.getString(configuration, "chatFormat.adminFormat"));
    }
}
