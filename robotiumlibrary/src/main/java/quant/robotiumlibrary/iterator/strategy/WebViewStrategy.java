package quant.robotiumlibrary.iterator.strategy;

import android.webkit.WebView;

import com.robotium.solo.By;
import com.robotium.solo.WebElement;

import java.util.ArrayList;

import quant.robotiumlibrary.data.DataProvider;
import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.solo.SoloConfig;
import quant.robotiumlibrary.solo.SoloInterface;

/**
 * Created by cz on 2017/4/7.
 */

public class WebViewStrategy implements ViewStrategic<WebView> {
    @Override
    public void process(IteratorCallback callback, SoloInterface solo, WebView source) {
        SoloConfig config = solo.getConfig();
        while(100>source.getProgress()){
            solo.sleep(config.sleepMiniDuration);
        }
        ArrayList<WebElement> webElements = solo.getCurrentWebElements();
        for (WebElement element: webElements) {
            if (element.getTagName().toUpperCase().contains("INPUT")) {
                solo.typeTextInWebElement(By.tagName(element.getTagName()), DataProvider.getRandomText(10));
                solo.sleep(config.sleepMiniDuration);
            } else {
                solo.takeScreenshot();
                solo.clickOnWebElement(element);
                solo.sleep(config.sleepMiniDuration);
            }
        }
    }
}
