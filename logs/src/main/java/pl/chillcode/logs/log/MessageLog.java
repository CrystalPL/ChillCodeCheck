package pl.chillcode.logs.log;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class MessageLog {
    String message;
    UUID senderUUID;
    long sentTime;
    boolean adminMessage;
}
