package quant.robotiumlibrary.property;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.PrintWriter;

import quant.robotiumlibrary.file.FilePrefs;

/**
 * Created by cz on 2017/3/20.
 */

public class PropertyProcessor {
    private static final String DEFAULT_PROPERTY_FILE_NAME = "properties.xml";
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY = "property";
    private static final String NAME = "name";
    private static final String VALUE = "value";

    private final Context context;
    private XmlSerializer currentXmlSerializer;
    private PrintWriter currentFileWriter;

    public PropertyProcessor(Context context){
        this.context=context;
    }

    public void writerProperties() throws IOException {
        currentFileWriter = new PrintWriter(FilePrefs.PROP_FILE, "UTF-8");
        currentXmlSerializer = Xml.newSerializer();
        currentXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        currentXmlSerializer.setOutput(currentFileWriter);
        currentXmlSerializer.startDocument("UTF-8", null);

        currentXmlSerializer.startTag(null, PROPERTIES);
        writeProperty("android.Build.BOARD", Build.BOARD);
        writeProperty("android.Build.BRAND", Build.BRAND);
        writeProperty("android.Build.CPU_ABI", Build.CPU_ABI);
        writeProperty("android.Build.DEVICE", Build.DEVICE);
        writeProperty("android.Build.DISPLAY", Build.DISPLAY);
        writeProperty("android.Build.FINGERPRINT", Build.FINGERPRINT);
        writeProperty("android.Build.HOST", Build.HOST);
        writeProperty("android.Build.ID", Build.ID);
        writeProperty("android.Build.MANUFACTURER", Build.MANUFACTURER);
        writeProperty("android.Build.MODEL", Build.MODEL);
        writeProperty("android.Build.PRODUCT", Build.PRODUCT);
        writeProperty("android.Build.TAGS", Build.TAGS);
        writeProperty("android.Build.TYPE", Build.TYPE);
        writeProperty("android.Build.USER", Build.USER);
        if (Build.VERSION.SDK_INT >= 8) {
            writeProperty("android.Build.BOOTLOADER", Build.BOOTLOADER);
            writeProperty("android.Build.CPU_ABI2", Build.CPU_ABI2);
            writeProperty("android.Build.HARDWARE", Build.HARDWARE);
        }
        if (Build.VERSION.SDK_INT >= 9) {
            writeProperty("android.Build.SERIAL", Build.SERIAL);
        }
        writeProperty("android.Build.VERSION.CODENAME", Build.VERSION.CODENAME);
        writeProperty("android.Build.VERSION.INCREMENTAL", Build.VERSION.INCREMENTAL);
        writeProperty("android.Build.VERSION.RELEASE", Build.VERSION.RELEASE);
        writeProperty("android.Build.VERSION.SDK_INT", Integer.toString(Build.VERSION.SDK_INT));
        final Configuration configuration = context.getResources().getConfiguration();
        writeProperty("android.Configuration.fontScale", Float.toString(configuration.fontScale));
        writeProperty("android.Configuration.locale", String.valueOf(configuration.locale));
        writeProperty("android.Configuration.orientation", translateOrientation(configuration.orientation));
        writeProperty("android.Configuration.screenLayout.long", translateScreenLength(configuration.screenLayout));
        writeProperty("android.Configuration.screenLayout.size", translateScreenSize(configuration.screenLayout));
        if (Build.VERSION.SDK_INT >= 13) {
            writeProperty("android.Configuration.screenHeightDp", Integer.toString(configuration.screenHeightDp));
            writeProperty("android.Configuration.screenWidthDp", Integer.toString(configuration.screenWidthDp));
            writeProperty("android.Configuration.smallestScreenWidthDp", Integer.toString(configuration.smallestScreenWidthDp));
        }
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        writeProperty("android.DisplayMetrics.density", Float.toString(metrics.density));
        writeProperty("android.DisplayMetrics.densityDpi", translateDensityDpi(metrics.densityDpi));
        writeProperty("android.DisplayMetrics.heightPixels", Integer.toString(metrics.heightPixels));
        writeProperty("android.DisplayMetrics.scaledDensity", Float.toString(metrics.scaledDensity));
        writeProperty("android.DisplayMetrics.widthPixels", Integer.toString(metrics.widthPixels));
        writeProperty("android.DisplayMetrics.xdpi", Float.toString(metrics.xdpi));
        writeProperty("android.DisplayMetrics.ydpi", Float.toString(metrics.ydpi));
//        writeProperty("java.util.Locale.default", String.valueOf(Locale.getDefault()));
        currentXmlSerializer.endTag(null, PROPERTIES);

        currentXmlSerializer.endDocument();
        currentFileWriter.flush();
        currentFileWriter.close();

    }

    private void writeProperty(final String name, final String value) throws IOException {
        currentXmlSerializer.startTag(null, PROPERTY);
        currentXmlSerializer.attribute(null, NAME, name);
        currentXmlSerializer.attribute(null, VALUE, value);
        currentXmlSerializer.endTag(null, PROPERTY);
    }

    private static String translateScreenLength(final int screenLayout) {
        switch (screenLayout & Configuration.SCREENLAYOUT_LONG_MASK) {
            case Configuration.SCREENLAYOUT_LONG_YES:
                return "long";
            case Configuration.SCREENLAYOUT_LONG_NO:
                return "notlong";
            default:
                return "undefined";
        }
    }

    private static String translateScreenSize(final int screenLayout) {
        switch (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "xlarge";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            default:
                return "undefined";
        }
    }

    private static String translateOrientation(final int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                return "portrait";
            case Configuration.ORIENTATION_LANDSCAPE:
                return "landscape";
            default:
                return "undefined";
        }
    }


    private static String translateDensityDpi(final int densityDpi) {
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
        }
        return Integer.toString(densityDpi);
    }


}
