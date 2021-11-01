package pl.chillcode.logs.command;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import pl.chillcode.check.model.CheckResult;
import pl.crystalek.crcapi.lib.adventure.text.Component;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    public void init(final Component cheaterResultComponent, final Component clearResultComponent, final Component logoutResultComponent, final Component admittingResultComponent) {
        ResultUtil.cheaterResultComponent = cheaterResultComponent;
        ResultUtil.clearResultComponent = clearResultComponent;
        ResultUtil.logoutResultComponent = logoutResultComponent;
        ResultUtil.admittingResultComponent = admittingResultComponent;
    }
}
