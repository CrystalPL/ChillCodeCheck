package pl.chillcode.check.command.sub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import pl.chillcode.check.ChillCodeCheck;
import pl.chillcode.check.command.SubCommand;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class ReloadSubCommand implements SubCommand {
    ChillCodeCheck plugin;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!plugin.loadFiles()) {
            MessageAPI.sendMessage("reload.error", sender);
            return;
        }

        if (!plugin.loadMessage()) {
            MessageAPI.sendMessage("reload.error", sender);
            return;
        }

        plugin.registerListeners();
        MessageAPI.sendMessage("reload.reload", sender);
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
        return "chillcode.check.reload";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "reload.usage";
    }
}
