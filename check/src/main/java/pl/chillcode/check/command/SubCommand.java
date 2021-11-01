package pl.chillcode.check.command;

import pl.crystalek.crcapi.command.BaseSubCommand;

public interface SubCommand extends BaseSubCommand {
    String usagePathMessage();
}
