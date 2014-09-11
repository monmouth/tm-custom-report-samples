package tw.com.mt.cust.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

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

    public static String getValue(HttpServletRequest req, String filedName,
            String defaultValue) {
        Enumeration names = req.getParameterNames();
        String[] values = req.getParameterValues(filedName);
        while (names.hasMoreElements() && values != null) {
            return joinTokens(Arrays.asList(values), ",");
        }
        return "";
    }

    public static List<String> getStrings(HttpServletRequest req,
            String filedName) {
        return makeTokens(getValue(req, filedName, ""), ",");
    }

    /**
     * Parse things into tokens.
     *
     * @param str
     *            string to separate
     * @param delimiters
     *            delimiters, ex:","
     * @return list of strings token from given string
     */
    public static List<String> makeTokens(String str, String delimiters) {
        List<String> list = new LinkedList<String>();
        if (str == null) {
            return list;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    /**
     * Join tokens into String.
     *
     * @param list
     *            list of objects
     * @param delimiters
     *            delimiters, ex:","
     * @return a String, which could be empty
     */
    public static String joinTokens(Collection<?> list, String delimiters) {
        if (list == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(delimiters);
            }
        }
        return sb.toString();
    }
}
