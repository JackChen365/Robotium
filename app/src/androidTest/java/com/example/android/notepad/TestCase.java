package com.example.android.notepad;

import android.test.AndroidTestCase;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import quant.robotiumlibrary.ISolo;
import quant.robotiumlibrary.Param;

/**
 * Created by cz on 2017/5/17.
 */

public class TestCase extends AndroidTestCase {


    /**
     * 生成Solo所有方法信息
     * @param
     * @return
     */
    public void AndroidTestCase() throws Exception {
        File newXmlFile = new File("solo.xml");
        FileOutputStream fos=null;
        if (newXmlFile.createNewFile()) {
            fos = new FileOutputStream(newXmlFile);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.startTag(null, "solo");
            Method[] methods = ISolo.class.getMethods();
            for(Method method:methods){
                serializer.startTag(null, "method");
                serializer.attribute(null, "name", method.getName());
                //返回值
                serializer.attribute(null, "return", method.getReturnType().getName());
                //参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(0<parameterTypes.length){
                    for(Class clazz:parameterTypes){
                        Annotation annotation = clazz.getAnnotation(Param.class);
                        serializer.startTag(null, "parameter");
                        serializer.attribute(null, "name",clazz.getName());
                        if(null!=annotation){
                            serializer.attribute(null, "text",((Param)annotation).value());
                        }
                        serializer.endTag(null, "parameter");
                    }
                }
                serializer.endTag(null, "method");
            }
            serializer.endTag(null, "solo");
            serializer.endDocument();

            serializer.flush();
        }
        fos.close();
    }
}
