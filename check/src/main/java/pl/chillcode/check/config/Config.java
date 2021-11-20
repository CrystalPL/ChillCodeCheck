package pl.chillcode.check.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import pl.crystalek.crcapi.config.ConfigParserUtil;
import pl.crystalek.crcapi.config.FileHelper;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.util.ColorUtil;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter()
public final class Config {
    final FileConfiguration config;
    final FileHelper spawnLocationFileHelper;
    String playerCheckCommand;
    List<String> playerCheckCommandAliases;
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

    public boolean load() {
        this.playerCheckCommand = config.getString("command.check.name");
        this.playerCheckCommandAliases = Arrays.asList(config.getString("command.check.aliases").split(", "));

        this.adminChatChar = config.getString("adminChatChar").charAt(0);
        this.commandsWhenPlayerCheating = ColorUtil.color(config.getStringList("commandsWhenPlayerCheating"));
        this.commandsWhenPlayerAdmitting = ColorUtil.color(config.getStringList("commandsWhenPlayerAdmitting"));
        this.commandsWhenPlayerLogout = ColorUtil.color(config.getStringList("commandsWhenPlayerLogout"));
        this.broadcastMessage = config.getBoolean("broadcastMessage");
        this.backPlayerToPreviousLocation = config.getBoolean("backPlayerToPreviousLocation");
        try {
            this.teleportPlayerAfterCheck = ConfigParserUtil.getLocation(config.getConfigurationSection("teleportPlayerAfterCheck"));
        } catch (final ConfigLoadException exception) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas ładowania pola teleportPlayerAfterCheck");
            Bukkit.getLogger().severe(exception.getMessage());
            return false;
        }

        this.dropItem = config.getBoolean("dropItem");
        this.allowedUseCommands = config.getBoolean("allowedUseCommands");
        this.allowedCommands = config.getStringList("allowedUseCommands");

        final Optional<Integer> notifyTimeOptional = NumberUtil.getInt(config.get("notifyTime"));
        if (!notifyTimeOptional.isPresent()) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas ładowania pola notifyTime!");
            Bukkit.getLogger().severe("Czas powiadomień musi być liczbą z zakresu <1, 2_147_483_647>!");
            return false;
        }
        this.notifyTime = notifyTimeOptional.get();
        try {
            this.spawnLocation = ConfigParserUtil.getLocation(spawnLocationFileHelper.getConfiguration().getConfigurationSection("checkSpawnLocation"));
        } catch (final ConfigLoadException exception) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas ładowania pola checkSpawnLocation ze spawnLocation.yml");
            Bukkit.getLogger().severe(exception.getMessage());
            return false;
        }

        this.playerChatFormat = ColorUtil.color(config.getString("chatFormat.playerFormat"));
        this.adminChatFormat = ColorUtil.color(config.getString("chatFormat.adminFormat"));

        return true;
    }

}
