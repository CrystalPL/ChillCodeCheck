package pl.chillcode.logs;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
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
import pl.chillcode.logs.storage.mongo.MongoProvider;
import pl.chillcode.logs.storage.mysql.MySQLProvider;
import pl.chillcode.logs.storage.sqlite.SQLiteProvider;
import pl.chillcode.logs.user.PlayerNicknameCache;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.impl.MultiCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.database.storage.Storage;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;
import pl.crystalek.crcapi.message.api.message.IChatMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ChillCodeLogs extends JavaPlugin {
    private Storage<Provider> storage;

    @Override
    public void onEnable() {
        final MessageAPI messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final Config config = new Config(this, "config.yml");
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

        storage = new Storage<>(config.getDatabaseConfig(), this);
        if (!storage.initDatabase()) {
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        storage.initProvider(MySQLProvider.class, SQLiteProvider.class, MongoProvider.class);
        final Provider provider = storage.getProvider();

        final ChillCodeCheck chillCodeCheck = (ChillCodeCheck) Bukkit.getPluginManager().getPlugin("ChillCodeCheck");
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

        final PlayerNicknameCache playerNicknameCache = new PlayerNicknameCache(provider);
        final LogCache logCache = new LogCache(provider, playerNicknameCache, this);

        final Optional<IChatMessage> cheaterOptional = messageAPI.getMessage("checkResult.cheater", null, IChatMessage.class);
        final Optional<IChatMessage> clearOptional = messageAPI.getMessage("checkResult.clear", null, IChatMessage.class);
        final Optional<IChatMessage> logoutOptional = messageAPI.getMessage("checkResult.logout", null, IChatMessage.class);
        final Optional<IChatMessage> admittingOptional = messageAPI.getMessage("checkResult.admitting", null, IChatMessage.class);

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

        final ResultUtil resultUtil = new ResultUtil(
                cheaterOptional.get().getChatComponent(),
                clearOptional.get().getChatComponent(),
                logoutOptional.get().getChatComponent(),
                admittingOptional.get().getChatComponent()
        );

        final Optional<IChatMessage> logOptional = messageAPI.getMessage("showLogs.log", null, IChatMessage.class);
        final Optional<IChatMessage> detailsComponent = messageAPI.getMessage("showLogs.details", null, IChatMessage.class);
        final Optional<IChatMessage> messageOptional = messageAPI.getMessage("showLogs.message", null, IChatMessage.class);

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

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CheckStartListener(logCache), this);
        pluginManager.registerEvents(new CheckEndListener(logCache), this);
        pluginManager.registerEvents(new AsyncCheckMessageListener(logCache), this);
        pluginManager.registerEvents(new PlayerJoinListener(provider, this), this);

        //inject sub commands
        final Map<Class<? extends Command>, CommandData> commandDataMap = chillCodeCheck.getCheckConfig().getCommandDataMap();
        final CommandData commandData = commandDataMap.get(CheckCommand.class);
        final Map<Class<? extends Command>, List<String>> subCommandMap = commandData.getSubCommandMap();

        subCommandMap.put(DetailsSubCommand.class, ImmutableList.of("details"));
        subCommandMap.put(LogSubCommand.class, ImmutableList.of("logs"));

        final DetailsSubCommand detailsSubCommand = new DetailsSubCommand(messageAPI, commandDataMap, config, logCache, detailsComponent.get().getChatComponent(), messageOptional.get().getChatComponent(), resultUtil);
        final LogSubCommand logSubCommand = new LogSubCommand(messageAPI, commandDataMap, config, this, logCache, logOptional.get().getChatComponent(), String.format("/%s details ", commandData.getCommandName()), resultUtil);

        final MultiCommand checkCommand = chillCodeCheck.getCheckCommand();
        checkCommand.registerSubCommand(detailsSubCommand, false);
        checkCommand.registerSubCommand(logSubCommand, true);
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.close();
        }
    }
}
