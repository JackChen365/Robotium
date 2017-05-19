/*
 * This is an example test project created in Eclipse to test NotePad which is a sample 
 * project located in AndroidSDK/samples/android-11/NotePad
 * 
 * 
 * You can run these test cases either on the emulator or on device. Right click
 * the test project and select Run As --> Run As Android JUnit Test
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

package com.example.android.notepad;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Xml;

import org.junit.Test;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quant.robotiumlibrary.ISolo;
import quant.robotiumlibrary.NewSolo;
import quant.robotiumlibrary.Param;


public class ApplicationTest extends ActivityInstrumentationTestCase2<NotesList> {
    public static final String TAG="NotePadTest";
    private static final String NOTE_1 = "Note 1";
    private static final String NOTE_2 = "Note 2";


    private ISolo solo;

    public ApplicationTest() {
        super(NotesList.class);
    }
    final List<Runnable> actions=new ArrayList<>();

    @Override
    public void setUp() throws Exception {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        super.setUp();
        solo= NewSolo.create(getInstrumentation(),getActivity());
//        IteratorRegistry iteratorRegistry = IteratorRegistry.getInstance();
//        iteratorRegistry.setViewStrategyCallback(new ViewStrategyCallback() {
//            @Override
//            public Map<Class<? extends View>, Class<? extends ViewStrategic>> getStrategyItems() {
//                Map<Class<? extends View>, Class<? extends ViewStrategic>> items=new HashMap<>();
//                items.put(EditText.class, EditTextStrategy.class);
//                return items;
//            }
//        });

        actions.add(new Runnable() {
            @Override
            public void run() {
                int result=1/0;
            }
        });
        actions.add(new Runnable() {
            @Override
            public void run() {
                int[] array={1,2,3};
                System.out.println(array[4]);
            }
        });
        actions.add(new Runnable() {
            @Override
            public void run() {
                String value=null;
                value.equals("a");
            }
        });
    }

    @Test
    public void testWriteSoloMethodItems() throws Exception {
        File newXmlFile = new File(Environment.getExternalStorageDirectory(),"solo.xml");
        FileOutputStream fos;
        newXmlFile.createNewFile();
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
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for(int i=0;i<parameterAnnotations.length;i++){
                Class<?> clazz = parameterTypes[i];
                Annotation[] annotations = parameterAnnotations[i];
                for(Annotation annotation:annotations){
                    if(null!=annotation&&annotation instanceof Param){
                        serializer.startTag(null, "parameter");
                        serializer.attribute(null, "class",clazz.getName());
                        serializer.attribute(null, "name",clazz.getSimpleName());
                        serializer.attribute(null, "text",((Param)annotation).value());
                        serializer.endTag(null, "parameter");
                    }
                }
            }
            serializer.endTag(null, "method");
        }
        serializer.endTag(null, "solo");
        serializer.endDocument();

        serializer.flush();
        fos.close();
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
        super.tearDown();
    }
    @Test
    public void testAddNote() throws Exception {
        solo.unlockScreen();
        solo.acrossForPermission(getInstrumentation());
//        solo.autoIterator(getInstrumentation());
//        NotificationHelper.sendNotification(getInstrumentation(), getActivity().getClass(),"提示您!", "发送通知!");
        //Click on action menu item add
        solo.clickOnView(solo.getView(com.example.android.notepad.R.id.menu_add));
        solo.takeScreenshot();
        //Assert that NoteEditor activity is opened
        solo.assertCurrentActivity("Expected NoteEditor Activity", NoteEditor.class);
        solo.takeScreenshot();
        //In text field 0, enter Note 1
        solo.enterText(0, NOTE_1);
        solo.takeScreenshot();
        //Click on action menu item Save
        solo.clickOnView(solo.getView(com.example.android.notepad.R.id.menu_save));
        solo.takeScreenshot();
        //Click on action menu item Add
        solo.clickOnView(solo.getView(com.example.android.notepad.R.id.menu_add));
        solo.takeScreenshot();
        //In text field 0, type Note 2
        solo.typeText(0, NOTE_2);
        solo.takeScreenshot();
        //Click on action menu item Save
        solo.clickOnView(solo.getView(com.example.android.notepad.R.id.menu_save));
        //Takes a screenshot and saves it in "/sdcard/Robotium-Screenshots/".
        solo.takeScreenshot();
        int size = actions.size();
        int index=new Random().nextInt(size*2);
        if(index<size){
            actions.get(index).run();
        }
        //Search for Note 1 and Note 2
        boolean notesFound = solo.searchText(NOTE_1) && solo.searchText(NOTE_2);
        solo.takeScreenshot();
        //To clean up after the test case
        deleteNotes();
        solo.takeScreenshot();
        //Assert that Note 1 & Note 2 are found
        assertTrue("Note 1 and/or Note 2 are not found", notesFound);
    }

    @Test
    public void testEditNoteTitle() throws Exception {
        //Click on add action menu item
        solo.clickOnView(solo.getView(com.example.android.notepad.R.id.menu_add));
        solo.takeScreenshot();
        //In text field 0, enter Note 1
        solo.enterText(0, NOTE_1);
        solo.takeScreenshot();
        //Press hard key back button
        solo.goBack();
        solo.clickOnText(NOTE_1);
        solo.takeScreenshot();
        //Click on menu item "Edit title"
        solo.clickOnMenuItem("Edit title");
        solo.takeScreenshot();
        //Clear the edit text field
        solo.clearEditText(0);
        //In the text field enter Note 2
        solo.enterText(0, NOTE_2);
        solo.takeScreenshot();
        //Click on button "OK"
        solo.clickOnButton("OK");
        //Click on action menu item Save
        solo.clickOnView(solo.getView(R.id.menu_save));
        solo.takeScreenshot();
        //Long click Note 2
        solo.clickLongOnText(NOTE_2);
        //Click on Delete
        solo.clickOnText("Delete");

        //Assert that Note 2 is deleted
        assertFalse("Note 2 is found", solo.searchText(NOTE_2));
    }

    private void deleteNotes() {
        //Click on first item in List
        solo.clickInList(1);
        //Click on delete action menu item
        solo.clickOnView(solo.getView(R.id.menu_delete));
        //Long click first item in List
        solo.clickLongInList(1);
        //Click delete
        solo.clickOnText(solo.getString(R.string.menu_delete));
    }
}
