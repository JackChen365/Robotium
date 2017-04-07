package quant.robotiumlibrary.callback;

/**
 * Created by cz on 2017/4/7.
 */

public class IteratorRegistry {
    public final static IteratorRegistry instance=new IteratorRegistry();
    public ViewStrategyCallback viewStrategyCallback;
    public OnIteratorActivityListener iteratorActivityListener;
    public OnIteratorViewListener iteratorViewListener;

    private IteratorRegistry(){
    }

    public static IteratorRegistry getInstance(){
        return instance;
    }

    /**
     * 添加自定义控件遍历处理策略
     * @param viewStrategyCallback
     */
    public void setViewStrategyCallback(ViewStrategyCallback viewStrategyCallback) {
        this.viewStrategyCallback = viewStrategyCallback;
    }

    /**
     * 添加遍历activity事件
     * @param listener
     */
    public void setOnIteratorActivityListener(OnIteratorActivityListener listener) {
        this.iteratorActivityListener=listener;
    }

    /**
     * 添加操作view时事件
     * @param listener
     */
    public void setOnIteratorViewListener(OnIteratorViewListener listener) {
        this.iteratorViewListener=listener;
    }

}
