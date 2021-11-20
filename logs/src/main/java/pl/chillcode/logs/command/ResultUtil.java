package pl.chillcode.logs.command;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.check.model.CheckResult;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultUtil {
    Component cheaterResultComponent;
    Component clearResultComponent;
    Component logoutResultComponent;
    Component admittingResultComponent;

    public Component getResultComponent(final CheckResult checkResult) {
        switch (checkResult) {
            case CHEATER:
                return cheaterResultComponent;
            case CLEAR:
                return clearResultComponent;
            case LOGOUT:
                return logoutResultComponent;
            case ADMITTING:
                return admittingResultComponent;
            default:
                throw new IllegalStateException("Unexpected value: " + checkResult);
        }
    }
}
