package pl.chillcode.logs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.check.ChillCodeCheck;
import pl.chillcode.check.command.CheckCommand;
import pl.chillcode.logs.command.DetailsSubCommand;
import pl.chillcode.logs.command.LogSubCommand;
import pl.chillcode.logs.command.ResultUtil;
import pl.chillcode.logs.config.Config;
import pl.chillcode.logs.listener.AsyncCheckMessageListener;
import pl.chillcode.logs.listener.CheckEndListener;
import pl.chillcode.logs.listener.CheckStartListener;
import pl.chillcode.logs.listener.PlayerJoinListener;
import pl.chillcode.logs.log.LogCache;
import pl.chillcode.logs.storage.Provider;
import pl.chillcode.logs.storage.Storage;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.message.impl.ChatMessage;
import pl.crystalek.crcapi.singlemessage.SingleMessageAPI;
import pl.crystalek.crcapi.storage.BaseStorage;

import java.io.IOException;
import java.util.Optional;

public final class ChillCodeLogs extends JavaPlugin {
    private Storage storage;

    @Override
    public void onEnable() {
        final ConfigHelper configHelper = new ConfigHelper("config.yml", this);
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        final Config config = new Config(configHelper.getConfiguration(), this);
        if (!config.load()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        storage = new Storage(new BaseStorage<>(config.getDatabaseConfig(), this));
        if (!storage.init()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final MessageAPI messageAPI = new SingleMessageAPI(this);
        if (!messageAPI.init()) {
            return;
        }

        final Plugin chillCodeCheck = Bukkit.getPluginManager().getPlugin("ChillCodeCheck");
        if (chillCodeCheck == null) {
            getLogger().severe("Nie odnaleziono pluginu ChillCodeCheck!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //check if ChillCodeCheck plugin is enabled
        if (!chillCodeCheck.isEnabled()) {
            getLogger().severe("Odnaleziono plugin ChillCodeCheck, lecz nie jest on uruchomiony!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Provider provider = storage.getStorage().getProvider();
        final PlayerNicknameCache playerNicknameCache = new PlayerNicknameCache(provider);
        final LogCache logCache = new LogCache(provider, playerNicknameCache, this);

        final Optional<Component> cheaterOptional = messageAPI.getComponent("checkResult.cheater", null, ChatMessage.class);
        final Optional<Component> clearOptional = messageAPI.getComponent("checkResult.clear", null, ChatMessage.class);
        final Optional<Component> logoutOptional = messageAPI.getComponent("checkResult.logout", null, ChatMessage.class);
        final Optional<Component> admittingOptional = messageAPI.getComponent("checkResult.admitting", null, ChatMessage.class);

        if (!cheaterOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola checkResult.cheater w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!clearOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola checkResult.clear w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!logoutOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola checkResult.logout w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!admittingOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola checkResult.admitting w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final ResultUtil resultUtil = new ResultUtil(cheaterOptional.get(), clearOptional.get(), logoutOptional.get(), admittingOptional.get());

        final Optional<Component> logOptional = messageAPI.getComponent("showLogs.log", null, ChatMessage.class);
        final Optional<Component> detailsComponent = messageAPI.getComponent("showLogs.details", null, ChatMessage.class);
        final Optional<Component> messageOptional = messageAPI.getComponent("showLogs.message", null, ChatMessage.class);

        if (!logOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola showLogs.log w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!detailsComponent.isPresent()) {
            getLogger().severe("Nie odnaleziono pola showLogs.details w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!messageOptional.isPresent()) {
            getLogger().severe("Nie odnaleziono pola showLogs.message w pliku messages.yml");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final CheckCommand checkCommand = ((ChillCodeCheck) chillCodeCheck).getCheckCommand();
        final LogSubCommand logSubCommand = new LogSubCommand(config, this, logCache, logOptional.get(), String.format("/%s details ", checkCommand.getName()), messageAPI, resultUtil);
        final DetailsSubCommand detailsSubCommand = new DetailsSubCommand(config, logCache, detailsComponent.get(), messageOptional.get(), messageAPI, resultUtil);
        checkCommand.registerSubCommand("logs", logSubCommand, true);
        checkCommand.registerSubCommand("details", detailsSubCommand, false);

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CheckStartListener(logCache), this);
        pluginManager.registerEvents(new CheckEndListener(logCache), this);
        pluginManager.registerEvents(new AsyncCheckMessageListener(logCache), this);
        pluginManager.registerEvents(new PlayerJoinListener(provider, this), this);
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.getStorage().getDatabase().close();
        }
    }
}
