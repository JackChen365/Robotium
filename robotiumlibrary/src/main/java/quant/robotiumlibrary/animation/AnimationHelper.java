package quant.robotiumlibrary.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import quant.robotiumlibrary.R;

import static android.content.ContentValues.TAG;

public class AnimationHelper {
    private static final int DEFAULT_COUNT=3;

    public static void startTestAnim(final Activity activity){
        startTestAnim(activity,DEFAULT_COUNT);
    }

    public static void startTestAnim(final Activity activity,final int count){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup container=addCountDownView(activity.getWindowManager(),activity);
                startCountDownAnim(activity,container,count+1);
            }
        });
        //暂停此线程,此线程为测试线程
        SystemClock.sleep(count*2*1000);
    }

    /**
     * 启动计时动画
     * @param activity
     * @param container
     * @param count
     */
    private static void startCountDownAnim(final Activity activity, final ViewGroup container, int count) {
        new CountDownTimer(count*1000,1*1000){
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG,"millisUntilFinished:"+millisUntilFinished);
                TextView textView= (TextView) container.findViewById(R.id.anim_text_view);
                textView.setText(String.valueOf(millisUntilFinished/1000));
                ViewCompat.setScaleX(textView,1.6f);
                ViewCompat.setScaleY(textView,1.6f);
                textView.animate().scaleX(1f).scaleY(1f).setDuration(1000);
            }

            @Override
            public void onFinish() {
                //移除计时面板
                container.removeAllViews();
                container.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        WindowManager windowManager = activity.getWindowManager();
                        windowManager.removeView(container);
                    }
                });
            }
        }.start();
    }

    private static ViewGroup addCountDownView(WindowManager windowManager, Activity targetActivity) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        FrameLayout container = new FrameLayout(targetActivity);
        container.setClickable(true);
        container.setId(R.id.anim_container);
        container.setBackgroundColor(0x88333333);
        TextView textView = new TextView(targetActivity);
        textView.setId(R.id.anim_text_view);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,72);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        container.addView(textView, FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        windowManager.addView(container, layoutParams);
        return container;
    }

}
