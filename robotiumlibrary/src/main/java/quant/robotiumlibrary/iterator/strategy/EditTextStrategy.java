package quant.robotiumlibrary.iterator.strategy;

import android.text.InputType;
import android.widget.EditText;

import quant.robotiumlibrary.data.DataProvider;
import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */

public class EditTextStrategy implements ViewStrategic<EditText> {
    private static final int DEFAULT_NUMBER_SIZE=6;
    private static final int DEFAULT_TEXT_SIZE=6;
    private static final int DEFAULT_EMAIL_SIZE=8;
    private static final int DEFAULT_URI_SIZE=10;

    @Override
    public void process(IteratorCallback callback, ISolo solo, EditText source) {
        int inputType = source.getInputType();
        switch (inputType){
            case InputType.TYPE_CLASS_PHONE:
                //电话号码
                solo.enterText(source, DataProvider.getRandomPhone());
                break;
            case InputType.TYPE_CLASS_NUMBER:
                //数字
                solo.enterText(source, DataProvider.getRandomNumber(DEFAULT_NUMBER_SIZE));
                break;
            case InputType.TYPE_CLASS_DATETIME:
                //日期格式
                break;
            case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                //邮箱
                solo.enterText(source, DataProvider.getRandomEmail(DEFAULT_EMAIL_SIZE));
                break;
            case InputType.TYPE_TEXT_VARIATION_URI:
                //网页
                solo.enterText(source, DataProvider.getRandomUrl(DEFAULT_URI_SIZE));
                break;
            case InputType.TYPE_CLASS_TEXT:
                default:
                    solo.enterText(source, DataProvider.getRandomText(DEFAULT_TEXT_SIZE));
                break;
        }
    }
}
