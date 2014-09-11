package tw.com.mt.cust.util;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.BCodec;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.webmacro.Context;
import org.webmacro.servlet.WebContext;

public class Util {
    private static final Logger LOG = Logger.getLogger(Util.class);

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

    public static void sendFile(WebContext cont, InputStream is,
            String filename,
            String mimeType) {
        HttpServletResponse res = cont.getResponse();
        res.reset();
        if (mimeType != null) {
            res.setContentType(mimeType);
        }
        if (filename != null) {
            String encodedName = getEncodingFileName(cont, filename);
            res.setHeader("Content-Disposition", String.format(
                    "attachment;filename=\"%s\"", encodedName));
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(res.getOutputStream());
            IOUtils.copy(is, out);
            out.flush();
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LOG.warn("Fail to send file", e);
            try {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ignore) {
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Get Encoded FileName
     */
    public static String getEncodingFileName(WebContext cont, String fileName) {
        if (fileName == null) {
            return "";
        }
        try {
            String agent = cont.getRequest().getHeader("User-Agent");
            if ((agent.indexOf("MSIE") != -1) || (agent.indexOf("Trident") != -1)) {
                fileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            } else if (agent.indexOf("Mozilla") != -1) {
                fileName = new BCodec().encode(fileName, "UTF-8");
            } else {
                fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
