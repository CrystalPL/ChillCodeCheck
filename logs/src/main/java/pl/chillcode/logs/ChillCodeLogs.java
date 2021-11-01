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
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.storage.BaseStorage;
import pl.crystalek.crcapi.util.LogUtil;

import java.io.IOException;

public final class ChillCodeLogs extends JavaPlugin {
    private Storage storage;

    @Override
    public void onEnable() {
        final ConfigHelper configHelper = new ConfigHelper("config.yml");
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            LogUtil.error("Nie udało się utworzyć pliku konfiguracyjnego..");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        final Config config = new Config(configHelper.getConfiguration());
        if (!config.load()) {
            LogUtil.error("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        storage = new Storage(new BaseStorage<>(config.getDatabaseConfig()));
        if (!storage.init()) {
            LogUtil.error("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!new MessageAPI().init()) {
            return;
        }

        final Plugin chillCodeCheck = Bukkit.getPluginManager().getPlugin("ChillCodeCheck");
        if (chillCodeCheck == null) {
            LogUtil.error("Nie odnaleziono pluginu ChillCodeCheck!");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //check if ChillCodeCheck plugin is enabled
        if (!chillCodeCheck.isEnabled()) {
            LogUtil.error("Odnaleziono plugin ChillCodeCheck, lecz nie jest on uruchomiony!");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Provider provider = storage.getStorage().getProvider();
        final PlayerNicknameCache playerNicknameCache = new PlayerNicknameCache(provider);
        final LogCache logCache = new LogCache(provider, playerNicknameCache, this);

        ResultUtil.init(
                MessageAPI.getChatComponent("checkResult.cheater"),
                MessageAPI.getChatComponent("checkResult.clear"),
                MessageAPI.getChatComponent("checkResult.logout"),
                MessageAPI.getChatComponent("checkResult.admitting")
        );

        final CheckCommand checkCommand = ((ChillCodeCheck) chillCodeCheck).getCheckCommand();
        final LogSubCommand logSubCommand = new LogSubCommand(config, this, logCache, MessageAPI.getChatComponent("showLogs.log"), String.format("/%s details ", checkCommand.getName()));
        final DetailsSubCommand detailsSubCommand = new DetailsSubCommand(config, logCache, MessageAPI.getChatComponent("showLogs.details"), MessageAPI.getChatComponent("showLogs.message"));
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
