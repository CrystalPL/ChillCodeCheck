package pl.chillcode.check.command;

import pl.chillcode.check.ChillCodeCheck;
import pl.chillcode.check.command.sub.*;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.impl.MultiCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.Map;

public final class CheckCommand extends MultiCommand {

    public CheckCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final CheckCache checkCache, final Config config, final ChillCodeCheck plugin) {
        super(messageAPI, commandDataMap);

        registerSubCommand(new AdmittingSubCommand(messageAPI, commandDataMap, checkCache, config));
        registerSubCommand(new CheaterSubCommand(messageAPI, commandDataMap, checkCache, config));
        registerSubCommand(new CheckSubCommand(messageAPI, commandDataMap, checkCache, config));
        registerSubCommand(new ClearSubCommand(messageAPI, commandDataMap, checkCache, config));
        registerSubCommand(new ReloadSubCommand(messageAPI, commandDataMap, plugin, config));
        registerSubCommand(new SetSpawnSubCommand(messageAPI, commandDataMap, config));
        registerSubCommand(new SpawnSubCommand(messageAPI, commandDataMap, config));
    }

    @Override
    public String getPermission() {
        return "chillcode.check.base";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "usage";
    }

    @Override
    public int maxArgumentLength() {
        return 3;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
