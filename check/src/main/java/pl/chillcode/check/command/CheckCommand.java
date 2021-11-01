package pl.chillcode.check.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.chillcode.check.ChillCodeCheck;
import pl.chillcode.check.command.sub.*;
import pl.chillcode.check.config.Config;
import pl.chillcode.check.model.CheckCache;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CheckCommand extends Command {
    Map<String, SubCommand> subCommandMap = new HashMap<>();
    Set<String> argumentList = new HashSet<>();

    public CheckCommand(final Config config, final CheckCache checkCache, final ChillCodeCheck plugin) {
        super(config.getPlayerCheckCommand());
        setAliases(config.getPlayerCheckCommandAliases());
        System.out.println("siema test");

        registerSubCommand("help", new HelpSubCommand(), true);
        registerSubCommand("setspawn", new SetSpawnSubCommand(config), true);
        registerSubCommand("spawn", new SpawnSubCommand(config), true);
        registerSubCommand("sprawdz", new CheckSubCommand(checkCache, config), true);
        registerSubCommand("przyznanie", new AdmittingSubCommand(checkCache, config), true);
        registerSubCommand("czysty", new ClearSubCommand(checkCache, config), true);
        registerSubCommand("cheater", new CheaterSubCommand(checkCache, config), true);
        registerSubCommand("reload", new ReloadSubCommand(plugin), true);
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!sender.hasPermission("chillcode.check.base")) {
            MessageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", "chillcode.check.base"));
            return true;
        }

        final int argLength = args.length;
        if (argLength == 0) {
            MessageAPI.sendMessage("usage", sender);
            return true;
        }

        final String firstArgument = args[0].toLowerCase();
        if (!subCommandMap.containsKey(firstArgument)) {
            MessageAPI.sendMessage("usage", sender);
            return true;
        }
        final SubCommand subCommand = subCommandMap.get(firstArgument);

        final String permission = subCommand.getPermission();
        if (!sender.hasPermission(permission)) {
            MessageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", permission));
            return true;
        }

        if (argLength < subCommand.minArgumentLength() || argLength > subCommand.maxArgumentLength()) {
            MessageAPI.sendMessage(subCommand.usagePathMessage(), sender);
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return argumentList.stream().filter(argument -> argument.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2) {
            final String firstArgument = args[0].toLowerCase();
            if (!subCommandMap.containsKey(firstArgument)) {
                return new ArrayList<>();
            }

            return subCommandMap.get(firstArgument).tabComplete(sender, args);
        }

        return new ArrayList<>();
    }

    public void registerSubCommand(final String argumentName, final SubCommand subCommand, final boolean addToArgumentList) {
        subCommandMap.put(argumentName, subCommand);

        if (addToArgumentList) {
            argumentList.add(argumentName);
        }
    }
}
