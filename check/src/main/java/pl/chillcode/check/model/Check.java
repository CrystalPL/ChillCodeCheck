package pl.chillcode.check.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class Check {
    Location previousLocation;
    UUID adminUUID;
    Player player;
    int taskId;
}
