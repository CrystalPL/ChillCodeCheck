package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import pl.chillcode.check.ChillCodeCheck;
import pl.chillcode.check.config.Config;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ReloadSubCommand extends Command {
    ChillCodeCheck plugin;
    Config config;

    public ReloadSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final ChillCodeCheck plugin, final Config config) {
        super(messageAPI, commandDataMap);

        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        try {
            final FileHelper spawnLocationFileHelper = config.getSpawnLocationFileHelper();
            spawnLocationFileHelper.checkExist();
            spawnLocationFileHelper.load();

            config.checkExist();
            config.load();
            config.loadConfig();

            messageAPI.init();

            plugin.registerListeners();
        } catch (final ConfigLoadException | IOException exception) {
            messageAPI.sendMessage("reload.error", sender);
            return;
        }

        messageAPI.sendMessage("reload.reload", sender);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.check.reload";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "reload.usage";
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
