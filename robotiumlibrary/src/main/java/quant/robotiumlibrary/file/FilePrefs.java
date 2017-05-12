package quant.robotiumlibrary.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by cz on 9/18/16.
 */
public class FilePrefs {
    public static final File APP_FILE;// 程序SD卡目录
    public static final File REPORT_FOLDER;// 缓存
    public static final File PROP_FILE;// 属性文件
    public static final File SCREEN_SHOT;//屏幕截图

    static {
        APP_FILE = new File(Environment.getExternalStorageDirectory(), "/TestClient/");
        REPORT_FOLDER = new File(APP_FILE, "/report/");
        SCREEN_SHOT=new File(REPORT_FOLDER,"/screenShot/");
        PROP_FILE=new File(REPORT_FOLDER,"properties.xml");
        ensureFolder();
    }

    public static void ensureFolder(){
        mkdirs(APP_FILE, REPORT_FOLDER,SCREEN_SHOT);
    }

    private static void mkdirs(File... files) {
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].exists()) {
                    files[i].mkdirs();
                }
            }
        }
    }

    public static File getReportFile(){
        ensureFolder();
        return new File(REPORT_FOLDER,System.currentTimeMillis()+".xml");
    }

}
