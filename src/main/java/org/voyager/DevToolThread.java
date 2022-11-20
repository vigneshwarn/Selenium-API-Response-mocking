package org.voyager;

import org.openqa.selenium.devtools.DevTools;

/**
 * Created By vigneshwaranthangavelu  @ 15/11/22 - 2:04 pm
 */

public class DevToolThread {
    private static  ThreadLocal<org.openqa.selenium.devtools.DevTools> chromeDevTool = new ThreadLocal<>();


    public static void setChromeDevTools(DevTools devTools){
        chromeDevTool.set(devTools);
    }


    public static DevTools getChromeDevTools(){
        return chromeDevTool.get();
    }
}
