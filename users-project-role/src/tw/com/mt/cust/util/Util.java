package tw.com.mt.cust.util;

import org.webmacro.Context;

public class Util {

    private static String CTU_MAIN_PAGE = "MainPage";
    private static String CTU_MENU_TAG = "MenuTag";
    private static String CTU_SUB_PAGE = "SubPage";

    public static void setDisplay(Context cont, String mainPage,
            String menuTag, String subPage) {
        if (mainPage != null)
            cont.put(CTU_MAIN_PAGE, mainPage);
        if (menuTag != null)
            cont.put(CTU_MENU_TAG, menuTag);
        if (subPage != null)
            cont.put(CTU_SUB_PAGE, subPage);
    }
}
