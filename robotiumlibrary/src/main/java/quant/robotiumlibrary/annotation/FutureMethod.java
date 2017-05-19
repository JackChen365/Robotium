package quant.robotiumlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cz on 2017/5/17.
 * 未来待实现方法注解标记
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FutureMethod {
}
