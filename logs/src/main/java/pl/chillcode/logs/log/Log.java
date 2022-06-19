package pl.chillcode.logs.log;

import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.chillcode.check.model.CheckResult;

import java.util.LinkedList;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Log {
    final long checkStartTime;
    final UUID playerUUID;
    final UUID adminUUID;
    CheckResult checkResult;
    long checkEndTime;
    LinkedList<MessageLog> messageLogList = new LinkedList<>();
}
