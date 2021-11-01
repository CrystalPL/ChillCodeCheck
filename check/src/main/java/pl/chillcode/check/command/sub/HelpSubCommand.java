package pl.chillcode.check.command.sub;

import org.bukkit.command.CommandSender;
import pl.chillcode.check.command.SubCommand;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;

public final class HelpSubCommand implements SubCommand {
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        MessageAPI.sendMessage("usage", sender);
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "chillcode.check.help";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "usage";
    }
}
