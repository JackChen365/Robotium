package quant.robotiumlibrary.report;

/**
 * Created by cz on 2017/4/6.
 */

public class JunitTestRunListenerWrapper {
    private JunitTestListener listener;
    public JunitTestRunListenerWrapper() {
    }

    public void set(JunitTestListener listener){
        this.listener=listener;
    }

    public JunitTestListener get(){
        return this.listener;
    }

}
