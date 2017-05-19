package quant.robotiumlibrary.permission;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;


public class RunTimePermission {

    /**
     * CALENDAR
     **/
    private static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    private static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";
    /**
     * CAMERA
     **/
    private static final String CAMERA = "android.permission.CAMERA";
    /**
     * CONTACTS
     **/
    private static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    private static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    private static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
    /**
     * LOCATION
     **/
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    /**
     * AUDIO
     **/
    private static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    /**
     * PHONE
     **/
    private static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    private static final String CALL_PHONE = "android.permission.CALL_PHONE";
    private static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    private static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    private static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    private static final String USE_SIP = "android.permission.USE_SIP";
    private static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    /**
     * SENSORS
     **/
    private static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    /**
     * SMS
     **/
    private static final String SEND_SMS = "android.permission.SEND_SMS";
    private static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    private static final String READ_SMS = "android.permission.READ_SMS";
    private static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    private static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";
    private static final String READ_CELL_BROADCASTS = "android.permission.READ_CELL_BROADCASTS";
    /**
     * SD Card
     **/
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Package Installer
     */
    private static final String PACKAGE_INSTALLER = "com.android.packageinstaller";
    private static final String PERMISSION_ALLOW_ID = "com.android.packageinstaller:id/permission_allow_button";
    private static final String PACKAGE_INSTALLER_XIAOMI = "com.lbe.security.miui";
    private static final String PERMISSION_ALLOW_ID_XIAOMI = "android:id/button1";

    private static final String[] permissionGroup = new String[]{READ_CALENDAR, WRITE_CALENDAR, CAMERA, READ_CONTACTS,
            WRITE_CONTACTS, GET_ACCOUNTS, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, RECORD_AUDIO, READ_PHONE_STATE,
            CALL_PHONE, READ_CALL_LOG, WRITE_CALL_LOG, ADD_VOICEMAIL, USE_SIP, PROCESS_OUTGOING_CALLS, BODY_SENSORS,
            SEND_SMS, RECEIVE_SMS, READ_SMS, RECEIVE_WAP_PUSH, RECEIVE_MMS, READ_CELL_BROADCASTS, READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};

    private final Context context;
    private final String packageName;
    private final Instrumentation instrumentation;

    public static RunTimePermission create(Context context, String packageName, Instrumentation instrumentation){
        return new RunTimePermission(context,packageName,instrumentation);
    }

    private RunTimePermission(Context context, String packageName, Instrumentation instrumentation){
        this.context = context;
        this.packageName = packageName;
        this.instrumentation = instrumentation;
    }

    /**
     * Requests permissions to be granted to this application.
     */
    public void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] permissions = checkPermissions();
            if (permissions == null || permissions.length == 0) return;
            ActivityCompat.requestPermissions((Activity) context, permissions, 10000);
            UiAutomation uiAutomation = instrumentation.getUiAutomation();
            uiAutomation.setOnAccessibilityEventListener(new UiAutomation.OnAccessibilityEventListener() {
                @Override
                public void onAccessibilityEvent(AccessibilityEvent event) {
                    if (Build.MANUFACTURER.toLowerCase().replaceAll("\\s","").contains("xiaomi")) {
                        handlePermissions(event, PACKAGE_INSTALLER_XIAOMI, PERMISSION_ALLOW_ID_XIAOMI);
                    } else {
                        handlePermissions(event, PACKAGE_INSTALLER, PERMISSION_ALLOW_ID);
                    }
                }
            });

        }
    }

    /**
     * Requests permissions to be granted to this application.
     * adb shell pm grant package android.permission.*
     */
    void requestPermissionsForShell() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] permissions = checkPermissions();
            if (permissions == null) return;
            UiAutomation uiAutomation = instrumentation.getUiAutomation();
            for (String permission: permissions) {
                uiAutomation.executeShellCommand("pm grant " + packageName + " " + permission);
            }
        }
    }

    private boolean handlePermissions(AccessibilityEvent event, String packageInstaller, String permissionAllowId) {
        boolean result=false;
        if (null!=event&&!TextUtils.isEmpty(packageInstaller)&&packageInstaller.contains(event.getPackageName())) {
            AccessibilityNodeInfo source = event.getSource();
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP&&null!=source) {
                List<AccessibilityNodeInfo> infoList = source.findAccessibilityNodeInfosByViewId(permissionAllowId);
                if(null!=infoList&&!infoList.isEmpty()){
                    result = performClick(infoList.get(0));
                }
            }
        }
        return result;
    }

    private boolean performClick(AccessibilityNodeInfo nodeInfo) {
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

    /**
     * Check Permissions, if permission denied add to requestPermissions.
     * @return requestPermissions
     */
    private String[] checkPermissions(){
        ArrayList<String> requestPermissions = new ArrayList<>();
        String[] permissions = filterPermissions();
        if(null!=permissions){
            for (String p: permissions) {
                if (PackageManager.PERMISSION_GRANTED!=ContextCompat.checkSelfPermission(context, p)) {
                    requestPermissions.add(p);
                }
            }
        }
        return requestPermissions.toArray(new String[requestPermissions.size()]);
    }

    /**
     * Filter Permissions
     * @return Permissions
     */
    private String[] filterPermissions(){
        ArrayList<String> filter = new ArrayList<>();
        String[] packagePermissions = getPackagePermissions(context, packageName);
        if(null!=packagePermissions){
            for (String permission: permissionGroup) {
                for (String s: packagePermissions) {
                    if (permission.contains(s)){
                        filter.add(permission);
                    }
                }
            }
        }
        return filter.toArray(new String[filter.size()]);
    }

    /**
     * Get target package requestedPermissions.
     * @param context context
     * @param packageName target package
     * @return Permissions
     */
    private String[] getPackagePermissions(Context context, String packageName){
        String[] requestedPermissions=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            requestedPermissions = info.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requestedPermissions;
    }
}
