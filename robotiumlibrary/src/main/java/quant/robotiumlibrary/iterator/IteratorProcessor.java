package quant.robotiumlibrary.iterator;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.runner.lifecycle.ActivityLifecycleCallback;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.EditText;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import quant.robotiumlibrary.callback.IteratorRegistry;
import quant.robotiumlibrary.checker.ActivityChecker;
import quant.robotiumlibrary.iterator.strategy.AbsListViewStrategy;
import quant.robotiumlibrary.iterator.strategy.EditTextStrategy;
import quant.robotiumlibrary.iterator.strategy.RecyclerViewStrategy;
import quant.robotiumlibrary.iterator.strategy.ViewPagerStrategy;
import quant.robotiumlibrary.iterator.strategy.ViewStrategic;
import quant.robotiumlibrary.iterator.strategy.WebViewStrategy;
import quant.robotiumlibrary.ISolo;
import quant.robotiumlibrary.NewSolo;

/**
 * Created by cz on 2017/4/6.
 * 遍历元素操作对象
 */
public class IteratorProcessor implements ActivityLifecycleCallback,IteratorCallback {
    private static final String TAG="IteratorHelper";
    private static IteratorProcessor instance;
    private final Map<Class<? extends ViewStrategic>,ViewStrategic> strategyCacheItems;
    private final Map<Class<? extends View>,Class<? extends ViewStrategic>> strategyClassItems;
    private final LinkedList<ActivityBundle> activityBundles;
    private final ActivityChecker activityChecker;
    public static IteratorProcessor getInstance(ISolo solo){
        if(null==instance){
            synchronized (IteratorProcessor.class){
                if(null==instance){
                    instance=new IteratorProcessor(solo);
                }
            }
        }
        return instance;
    }

    private IteratorProcessor(ISolo solo){
        activityChecker=new ActivityChecker(solo);
        activityBundles=new LinkedList<>();
        strategyCacheItems =new HashMap<>();
        strategyClassItems =new HashMap<>();
        //添加默认操作条目
        strategyClassItems.put(AbsListView.class, AbsListViewStrategy.class);
        strategyClassItems.put(EditText.class, EditTextStrategy.class);
        strategyClassItems.put(RecyclerView.class, RecyclerViewStrategy.class);
        strategyClassItems.put(ViewPager.class, ViewPagerStrategy.class);
        strategyClassItems.put(WebView.class, WebViewStrategy.class);
    }

    public void startIterator(NewSolo solo, Instrumentation instrumentation){
        //添加新的操作条目
        Map<Class<? extends View>,Class<? extends ViewStrategic>> strategyItems=new HashMap<>(this.strategyClassItems);
        IteratorRegistry iteratorRegistry = IteratorRegistry.getInstance();
        if(null!=iteratorRegistry.viewStrategyCallback){
            Map<Class<? extends View>, Class<? extends ViewStrategic>> items = iteratorRegistry.viewStrategyCallback.getStrategyItems();
            if(null!=items){
                strategyItems.keySet().removeAll(items.keySet());
                strategyItems.putAll(items);
            }
        }
        processActivity(solo,instrumentation, strategyItems, solo.getCurrentActivity());
    }

    /**
     * 处理activity遍历逻辑
     * @param solo
     * @param strategyItems
     * @param currentActivity
     */
    private void processActivity(NewSolo solo, Instrumentation instrumentation, Map<Class<? extends View>, Class<? extends ViewStrategic>> strategyItems, final Activity currentActivity) {
        //记录activity变化
        ActivityLifecycleMonitorRegistry.getInstance().removeLifecycleCallback(this);
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(this);
        //纵深遍历所有控件
        if(null!=currentActivity){
            View decorView = currentActivity.getWindow().getDecorView();
            processView(solo,strategyItems,decorView);
        }
        loopNextActivity(solo,instrumentation,strategyItems,currentActivity);
    }

    /**
     * 轮询下一个界面
     * @param solo
     * @param instrumentation
     * @param strategyItems
     * @param currentActivity
     */
    private void loopNextActivity(NewSolo solo, Instrumentation instrumentation, Map<Class<? extends View>, Class<? extends ViewStrategic>> strategyItems, final Activity currentActivity) {
        //遍历完,取下一步需要遍历界面
        ActivityBundle activityBundle = activityBundles.pollFirst();
        Intent activityIntent = activityBundle.getActivityIntent(currentActivity);
        currentActivity.startActivity(activityIntent);

        //等待当前界面显示
        solo.waitForActivity(activityBundle.clazz);
        solo.sleep(solo.getConfig().sleepDuration);
        //此处检测未设备迭代activity监听,或者返回false才回调事件,否则跳过当前界面
        IteratorRegistry iteratorRegistry = IteratorRegistry.getInstance();
        if(null==iteratorRegistry.iteratorActivityListener||!iteratorRegistry.iteratorActivityListener.onIteratorActivity(solo,solo.getCurrentActivity())){
            processActivity(solo,instrumentation, strategyItems, solo.getCurrentActivity());
        } else {
            //遍历下一个界面
            loopNextActivity(solo,instrumentation,strategyItems,solo.getCurrentActivity());
        }
    }

    @Override
    public void onActivityLifecycleChanged(Activity activity, Stage stage) {
        //界面己打开
        if(Stage.CREATED==stage){
            //记录activity数据
            ActivityBundle activityBundle = ActivityBundle.create(activity);
            if(!activityBundles.contains(activityBundle)){
                activityBundles.offerFirst(activityBundle);
            }
        }
    }

    private void processView(NewSolo solo, Map<Class<? extends View>, Class<? extends ViewStrategic>> strategyItems, View view){
        Set<Class<? extends View>> classes = strategyItems.keySet();
        Class<? extends View> instanceClass;
        if(null!=(instanceClass=findInstanceClass(classes,view))){
            processView(solo,strategyItems,view,instanceClass);
        } else if(view instanceof ViewGroup){
            ViewGroup viewGroup= (ViewGroup) view;
            for(int i=0;i<viewGroup.getChildCount();i++){
                View childView = viewGroup.getChildAt(i);
                processView(solo,strategyItems,childView);
            }
        } else if(view.isClickable()){
            activityChecker.record();
            solo.clickOnView(view);
            if(activityChecker.changed()) {
                if(activityChecker.isFinished()){
                    solo.sleep(solo.getConfig().sleepDuration);
                } else {
                    solo.goBack();
                }
            }
        }
        //处理突然弹出对话框
        if(solo.waitForDialogToOpen(solo.getConfig().sleepMiniDuration)){
            solo.goBack();
        }
    }

    /**
     * 处理指定控件
     * @param solo
     * @param strategyItems
     * @param view
     * @param clazz
     */
    private void processView(NewSolo solo, Map<Class<? extends View>, Class<? extends ViewStrategic>> strategyItems, View view, Class<? extends View> clazz) {
        Class<? extends ViewStrategic> strategyClass = strategyItems.get(clazz);
        ViewStrategic viewStrategic = strategyCacheItems.get(strategyClass);
        if(null==viewStrategic){
            try {
                strategyCacheItems.put(strategyClass,viewStrategic = strategyClass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //回调监听
        Activity currentActivity = solo.getCurrentActivity();
        IteratorRegistry iteratorRegistry = IteratorRegistry.getInstance();
        if(null==iteratorRegistry.iteratorViewListener||!iteratorRegistry.iteratorViewListener.onIteratorView(currentActivity,view)){
            viewStrategic.process(this,solo,view);
        }
    }

    private Class<? extends View> findInstanceClass(Set<Class<? extends View>> classes, View view) {
        Class<? extends View> instanceClass=null;
        for(Class<? extends View> clazz:classes){
            if(clazz.isInstance(view)){
                instanceClass=clazz;
                break;
            }
        }
        return instanceClass;
    }

    public ActivityChecker getActivityChecker() {
        return activityChecker;
    }
}
