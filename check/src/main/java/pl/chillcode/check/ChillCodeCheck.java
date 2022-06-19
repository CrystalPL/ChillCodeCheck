package pl.chillcode.check;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.check.command.CheckCommand;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.listener.AsyncPlayerChatListener;
import pl.chillcode.check.listener.PlayerCommandListener;
import pl.chillcode.check.listener.PlayerDropItemListener;
import pl.chillcode.check.listener.PlayerQuitListener;
import pl.chillcode.check.model.Check;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.command.impl.MultiCommand;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;

import java.io.IOException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChillCodeCheck extends JavaPlugin {
    MessageAPI messageAPI;
    Config config;
    CheckCache checkCache;
    @Getter
    MultiCommand checkCommand;

    @Override
    public void onEnable() {
        messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final FileHelper spawnLocationFileHelper = new FileHelper(this, "spawnLocation.yml");
        try {
            spawnLocationFileHelper.checkExist();
            spawnLocationFileHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku z konfiguracją serwera..");
            getLogger().severe("Wyłączanie pluginu");
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        config = new Config(this, "config.yml", spawnLocationFileHelper);
        try {
            config.checkExist();
            config.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        try {
            config.loadConfig();
        } catch (final ConfigLoadException exception) {
            getLogger().severe(exception.getMessage());
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkCache = new CheckCache(config, this, messageAPI);
        checkCommand = new CheckCommand(messageAPI, config.getCommandDataMap(), checkCache, config, this);

        CommandRegistry.register(checkCommand);

        registerListeners();
    }

    public void registerListeners() {
        HandlerList.unregisterAll(this);

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new AsyncPlayerChatListener(checkCache, config), this);
        pluginManager.registerEvents(new PlayerQuitListener(checkCache, config), this);

        if (!config.isAllowedUseCommands()) {
            pluginManager.registerEvents(new PlayerCommandListener(checkCache, config, messageAPI), this);
        }

        if (!config.isDropItem()) {
            pluginManager.registerEvents(new PlayerDropItemListener(checkCache, messageAPI), this);
        }
    }

    @Override
    public void onDisable() {
        if (checkCache == null) {
            return;
        }

        final List<Check> playerCheckList = checkCache.getPlayerCheckList();
        if (playerCheckList != null) {
            playerCheckList.clear();
        }
    }

    public Config getCheckConfig() {
        return config;
    }
}
