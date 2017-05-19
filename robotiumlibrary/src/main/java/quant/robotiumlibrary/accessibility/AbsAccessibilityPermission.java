package quant.robotiumlibrary.accessibility;

import android.app.UiAutomation;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 2017/5/15.
 */

public abstract class AbsAccessibilityPermission {
    /**
     * Package Installer
     */
    protected static final String PACKAGE_INSTALLER = "android";
    protected static final String PERMISSION_ALLOW_ID = "android:id/button1";
    //-----小米----
    protected static final String PACKAGE_INSTALLER_XIAOMI = "com.lbe.security.miui";
    protected static final String PERMISSION_ALLOW_ID_XIAOMI = "android:id/button1";

    //-----oppo----
    protected static final String PACKAGE_INSTALL_OPPO="android";
    protected static final String PERMISSION_ALLOW_ID_OPPO="android:id/button1";

    //-----meizu----
    protected static final String PACKAGE_INSTALL_MEIZU="android";
    protected static final String PERMISSION_INSTALLER_MEIZU="android:id/button1";
    //-----华为-----
    protected static final String PACKAGE_INSTALL_HUAWEI1="com.huawei.systemmanager";
    protected static final String PACKAGE_INSTALL_HUAWEI2="com.android.packageinstaller";

    protected static final String PERMISSION_ALLOW_ID_HUAWEI1="com.huawei.systemmanager:id/btn_allow";
    protected static final String PERMISSION_ALLOW_ID_HUAWEI2="com.android.packageinstaller:id/permission_allow_button";

    final UiAutomation uiAutomation;

    public AbsAccessibilityPermission(UiAutomation uiAutomation) {
        this.uiAutomation = uiAutomation;
    }

    List<String> getPermissionPackageName(){
        return Arrays.asList(PACKAGE_INSTALLER);
    }

    List<String> getPermissionAllowId(){
        return Arrays.asList(PERMISSION_ALLOW_ID);
    }

    public void handlePermission(AccessibilityNodeInfo rootInActiveWindow) {
        List<String> permissionPackageName = getPermissionPackageName();
        List<String> permissionAllowId = getPermissionAllowId();
        handlePermissions(rootInActiveWindow,permissionPackageName,permissionAllowId);
    }


    boolean handlePermissions(AccessibilityNodeInfo node, List<String> packageInstaller, List<String> permissionAllowId) {
        boolean result=false;
        if (null!=node&&null!=packageInstaller&&!packageInstaller.isEmpty()&&packageInstaller.contains(node.getPackageName())) {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                for(String allowId:permissionAllowId){
                    List<AccessibilityNodeInfo> infoList = node.findAccessibilityNodeInfosByViewId(allowId);
                    if(null!=infoList&&!infoList.isEmpty()){
                        result = performClick(infoList.get(0));
                        break;
                    }
                }
            }
        }
        return result;
    }

    boolean performClick(AccessibilityNodeInfo nodeInfo) {
        boolean result=false;
        if(null!=nodeInfo){
            if(nodeInfo.isClickable()){
                result=nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                while(null!=parent){
                    if(parent.isClickable()){
                        result=parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    } else {
                        parent= nodeInfo.getParent();
                    }
                }
            }
        }
        return result;
    }

}
