package quant.robotiumlibrary.accessibility;

import android.app.UiAutomation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 2017/5/18.
 */

public class HuaweiAccessiblityPermission extends AbsAccessibilityPermission {

    public HuaweiAccessiblityPermission(UiAutomation uiAutomation) {
        super(uiAutomation);
    }

    @Override
    List<String> getPermissionPackageName() {
        return Arrays.asList(PACKAGE_INSTALL_HUAWEI1,PACKAGE_INSTALL_HUAWEI2);
    }

    @Override
    List<String> getPermissionAllowId() {
        return Arrays.asList(PERMISSION_ALLOW_ID_HUAWEI1,PERMISSION_ALLOW_ID_HUAWEI2);
    }
}
