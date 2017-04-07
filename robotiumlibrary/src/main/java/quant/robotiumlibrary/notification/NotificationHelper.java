package quant.robotiumlibrary.notification;

import android.app.Instrumentation;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class NotificationHelper {

    public static void showToast(final Instrumentation instrumentation,final String message) {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(instrumentation.getTargetContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendNotification(Instrumentation instrumentation, Class clazz,String title, String message) {
        Context context = instrumentation.getTargetContext();
        Bitmap bitmap = getApplicationBitmap(context);
        if(null!=bitmap){
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, clazz), PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setContentIntent(pendingIntent);
            builder.setLargeIcon(bitmap);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
            builder.setAutoCancel(true);
            final NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            final android.app.Notification notify = builder.build();
            instrumentation.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    manager.notify(10000, notify);
                }
            });
        }
    }

    private static Bitmap getApplicationBitmap(Context context){
        Bitmap bitmap =null;
        Drawable targetApplicationIcon = getTargetApplicationIcon(context);
        if(null!=targetApplicationIcon){
            bitmap = drawableToBitmap(targetApplicationIcon);
        }
        return bitmap;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap=null;
        if(null!=drawable){
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height,
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    private static Drawable getTargetApplicationIcon(Context context) {
        Drawable drawable =null;
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
            drawable = info.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

}
