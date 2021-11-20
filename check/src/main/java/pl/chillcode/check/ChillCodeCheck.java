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
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.config.FileHelper;
import pl.crystalek.crcapi.message.MessageAPI;

import java.io.IOException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChillCodeCheck extends JavaPlugin {
    FileHelper spawnLocationFileHelper;
    ConfigHelper configHelper;
    Config config;
    CheckCache checkCache;
    @Getter
    CheckCommand checkCommand;
    MessageAPI messageAPI;


    @Override
    public void onEnable() {
        spawnLocationFileHelper = new FileHelper("spawnLocation.yml", this);
        configHelper = new ConfigHelper("config.yml", this);
        if (!loadFiles()) {
            return;
        }

        loadMessage();
        checkCache = new CheckCache(config, this, messageAPI);
        registerListeners();
        checkCommand = new CheckCommand(config, checkCache, this, messageAPI);
        CommandRegistry.register(checkCommand);
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

    public boolean loadMessage() {
        this.messageAPI = new MessageAPI(this);
        return messageAPI.init();
    }

    public boolean loadFiles() {
        try {
            spawnLocationFileHelper.checkExist();
            spawnLocationFileHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku z lokalizacją spawnu..");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return false;
        }

        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return false;
        }

        config = new Config(configHelper.getConfiguration(), spawnLocationFileHelper);
        if (!config.load()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
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
}
