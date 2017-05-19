package quant.robotiumlibrary.accessibility;

import android.app.UiAutomation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 2017/5/15.
 */

public class XiaomiAccessibilityPermission extends AbsAccessibilityPermission {

    public XiaomiAccessibilityPermission(UiAutomation uiAutomation) {
        super(uiAutomation);
    }

    @Override
    List<String> getPermissionPackageName() {
        return Arrays.asList(PACKAGE_INSTALLER_XIAOMI);
    }

    @Override
    List<String> getPermissionAllowId() {
        return Arrays.asList(PERMISSION_ALLOW_ID_XIAOMI);
    }
}
