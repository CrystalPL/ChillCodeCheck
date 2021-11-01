package pl.chillcode.check.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.chillcode.check.model.Check;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class AsyncCheckMessageEvent extends Event {
    static HandlerList HANDLERS = new HandlerList();
    Check check;
    String message;
    UUID senderUUID;
    boolean adminMessage;

    public AsyncCheckMessageEvent(final Check check, final String message, final UUID senderUUID, final boolean adminMessage) {
        super(true);
        this.check = check;
        this.message = message;
        this.senderUUID = senderUUID;
        this.adminMessage = adminMessage;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
