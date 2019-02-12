package com.huanshare.tools.tag;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *  2017/2/28.
 */
public class ImportTag extends BodyTagSupport {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void doInitBody() throws JspException {
        super.doInitBody();
    }

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String importJspContent = this.getImportJspContent(url, request);
        try {
            pageContext.getOut().print(importJspContent);
        } catch (IOException e) {
            return SKIP_BODY;
        }
        return EVAL_BODY_INCLUDE;
    }

    private String getImportJspContent(String importTargetUrl, HttpServletRequest request){
        try {
            Cookie myCookie = null;
            String WCSCookie = "";
            URL u = new URL(importTargetUrl);

            Cookie cookies[] = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    myCookie = cookies[i];
                    WCSCookie = WCSCookie + ";" + myCookie.getName() + "=" + myCookie.getValue();
                }
            }

            URLConnection uc = u.openConnection();
            uc.setRequestProperty("Cookie", WCSCookie);
            uc.setRequestProperty("Accept-Charset", "UTF-8");

            InputStream in = uc.getInputStream();
            return IOUtils.toString(in, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
