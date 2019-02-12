# Project Information

增强的Robotium框架
* 增加robotium行为记为(每一步操作)，及文档生成功能
* 增加了跳过运行时权限问题
* 提供了其他行为扩展

## Getting Started
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{ url "http://www.momoda.pro:8081/repository/maven/"}
    }
}
dependencies {
    androidTestImplementation 'cz.record:record-library:1.0.0'
}

defaultConfig {
    applicationId "com.example.android.notepad"
    minSdkVersion rootProject.ext.minSdkVersion
    targetSdkVersion rootProject.ext.targetSdkVersion
    versionCode 1
    versionName "1.0"
    
    //配置Runner对象，定制用以生成文档
    testInstrumentationRunner "quant.robotiumlibrary.report.ReportTestRunner"
}
```
> 具体配置可见Sample/AndroidTest/Java/NotePadTest

## Built With
* com.jayway.android.robotium:robotium-solo:5.6.3
* com.android.support.test:rules:0.4.1
* com.android.support:appcompat-v7

## output
* 文档生成位置为:/storage/emulated/0/TestClient/report/xx.xml (需要考虑当前app权限问题)
* 配置位置为:FilePrefs.APP_FILE 因需求不同，所以将output生成到手机内。 


## Authors

* **CZ** - [MyWebsite](http://www.momoda.pro)
