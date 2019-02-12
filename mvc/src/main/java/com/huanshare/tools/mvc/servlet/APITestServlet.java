package com.huanshare.tools.mvc.servlet;

import com.huanshare.tools.mvc.cache.RestAspectPropertiesManager;
import com.huanshare.tools.mvc.model.APITestServiceInfo;
import com.huanshare.tools.mvc.model.RestAspectProperties;
import com.huanshare.tools.enums.TrueFalseEnum;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.utils.*;
import com.huanshare.tools.enums.TrueFalseEnum;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

/**
 *  2015/12/3.
 */
public class APITestServlet extends HttpServlet {

    //日志
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(APITestServlet.class);

    //JSON处理类
    private static ObjectMapper objectMapper = JacksonUtils.getInstance();

    public static final String PARAM_NAME_USERNAME = "username";
    public static final String PARAM_NAME_PASSWORD = "password";
    public static final String PARAM_NAME_RESTPATH = "restPath";
    public static final String PARAM_NAME_BODYCONTENT = "bodyContent";
    public static final String PARAM_NAME_SECRETLEVEL = "secretLevel";
    public static final String PARAM_NAME_TOKEN = "token";
    public static final String PARAM_NAME_PATH = "methodPath";
    public static final String PARAM_NAME_REST_TEST_FILEPATH = "restTestServicePath";

    public static final String IS_LOGIN_SUCCESS = "IS_LOGIN_SUCCESS";

    public static final String CONTENT_TYPE_JS = "application/x-javascript";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_CSS = "text/css";

    public static final String FILE_RESOURCE_PATH = "rest/test";

    public static final String REQUEST_LOGIN_SUBMIT = "/submitLogin";
    public static final String REQUEST_LOAD_METHOD_SUBMIT = "/loadMethodSelect";
    public static final String REQUEST_REQUEST_SUBMIT = "/submitRequest";
    public static final String HTML_FILE_LOGIN = "/login.html";
    public static final String HTML_FILE_INDEX = "/index.html";

    private static final Map<String, String> FILE_MAP = new HashMap<>();
    private static final Map<String, APITestServiceInfo> REST_TEST_SERVICE_MAP = new LinkedHashMap<>();


    /**
     * web.xml中配置的用户名
     */
    private String username = "";
    /**
     * web.xml中配置的密码
     */
    private String password = "";
    /**
     * web.xml中配置的rest路径
     */
    private String restPath = "";

    @Override
    public void init() throws ServletException {
        super.init();
        String userName = getInitParameter(PARAM_NAME_USERNAME);
        if (StringUtils.isNotBlank(userName)) {
            this.username = userName;
        }

        String password = getInitParameter(PARAM_NAME_PASSWORD);
        if (StringUtils.isNotBlank(password)) {
            this.password = password;
        }

        String restPath = getInitParameter(PARAM_NAME_RESTPATH);
        if (StringUtils.isNotBlank(restPath)) {
            this.restPath = restPath;
        }

        String restTestServicePath = getInitParameter(PARAM_NAME_REST_TEST_FILEPATH);
        if (StringUtils.isNotBlank(restTestServicePath)) {
            reloadProperties(restTestServicePath);
        }

        FILE_MAP.put(".js", CONTENT_TYPE_JS);
        FILE_MAP.put(".css", CONTENT_TYPE_CSS);
        FILE_MAP.put(".html", CONTENT_TYPE_HTML);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }

        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        //默认页面
        if (StringUtils.isBlank(path) || "/".equals(path)) {
            response.sendRedirect(contextPath + servletPath + HTML_FILE_LOGIN);
            return;
        }
        //不是登录和主页(不包含资源文件js css html)
        else if (!HTML_FILE_LOGIN.equals(path) && !HTML_FILE_INDEX.equals(path)
                && !REQUEST_LOGIN_SUBMIT.equals(path) && !REQUEST_LOAD_METHOD_SUBMIT.equals(path) && !REQUEST_REQUEST_SUBMIT.equals(path)) {
            Boolean isFile = false;
            Set<String> stringSet = FILE_MAP.keySet();
            for (Iterator<String> iterator = stringSet.iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                if (path.indexOf(key) >= 0) {
                    isFile = true;
                    break;
                }
            }

            if (!isFile) {
                response.sendRedirect(contextPath + servletPath + HTML_FILE_LOGIN);
                return;
            }
        }

        //资源输出
        if (processFile(path, response)) {
            return;
        }

        //登录
        if (REQUEST_LOGIN_SUBMIT.equals(path)) {
            PrintWriter printWriter = response.getWriter();
            String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
            String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
            if (username.equals(usernameParam) && password.equals(passwordParam)) {
                CookieUtils.addCookie(response, IS_LOGIN_SUCCESS, username, 86400 * 15);
                printWriter.print("success");
            } else {
                printWriter.print("error");
            }
            printWriter.flush();
            printWriter.close();
            return;
        }
        //加载接口信息
        else if (REQUEST_LOAD_METHOD_SUBMIT.equals(path)) {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(JacksonUtils.getInstance().writeValueAsString(REST_TEST_SERVICE_MAP));
            printWriter.flush();
            printWriter.close();
        }
        //请求接口
        else if (REQUEST_REQUEST_SUBMIT.equals(path)) {
            processRequestSubmit(request, response, contextPath);
        }
        //默认页面
        else {
            write(getFilePath(HTML_FILE_LOGIN), CONTENT_TYPE_HTML, response);
            return;
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected String getFilePath(String fileName) {
        return FILE_RESOURCE_PATH + fileName;
    }

    private Boolean processFile(String path, HttpServletResponse response) throws IOException {
        Set<Map.Entry<String, String>> entries = FILE_MAP.entrySet();
        for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> entry = iterator.next();
            if (path.indexOf(entry.getKey()) >= 0) {
                write(getFilePath(path), entry.getValue(), response);
                return true;
            }
        }
        return false;
    }

    private void write(String path, String contentType, HttpServletResponse response) throws IOException {
        InputStreamReader configStream = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), HttpUtils.UTF8);
        response.setCharacterEncoding("utf-8");
        response.setContentType(contentType);
        PrintWriter writer = response.getWriter();
        IOUtils.copy(configStream, writer);
        writer.flush();
        writer.close();
    }

    private void reloadProperties(String filePath) {
        InputStreamReader configStream = null;
        try {
            configStream = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath), HttpUtils.UTF8);
            SAXReader reader = new SAXReader();
            Document document = reader.read(configStream);
            Element servicesElement = document.getRootElement();
            List elements = servicesElement.elements("service");
            if (!CollectionUtils.isEmpty(elements)) {
                Element element;
                for (Iterator it = elements.iterator(); it.hasNext(); ) {
                    element = (Element) it.next();
                    String name = element.attributeValue("name");
                    String value = element.attributeValue("value");
                    String path = element.attributeValue("path");
                    if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
                        continue;
                    }
                    REST_TEST_SERVICE_MAP.put(name, new APITestServiceInfo(name, path, value));
                }
            }
        } catch (IOException e) {
            logger.info("reload rest test xml error.", e);
        } catch (DocumentException e) {
            logger.info("reload rest test xml error.", e);
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("close configStream error.", e);
                }
            }
        }
    }

    private void processRequestSubmit(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException {
        Map<String, String> responseMap = new HashMap<>();

        String userName = CookieUtils.getValueByName(request, IS_LOGIN_SUCCESS);
        if (StringUtils.isBlank(userName)) {
            responseMap.put("isLoginSuccess", String.valueOf(TrueFalseEnum.FALSE.getKey()));
        } else {
            responseMap.put("isLoginSuccess", String.valueOf(TrueFalseEnum.TRUE.getKey()));
            String bodyContent = request.getParameter(PARAM_NAME_BODYCONTENT);
            String secretLevel = request.getParameter(PARAM_NAME_SECRETLEVEL);
//            String token = request.getParameter(PARAM_NAME_TOKEN);
            String path = request.getParameter(PARAM_NAME_PATH);

            String requestContent = "";
            String resultContent = "";
            Map<String, Object> requestMap;
            RestAspectProperties properties = RestAspectPropertiesManager.getInstance().getProperties();
            long startTime = System.currentTimeMillis();

            //create address
            StringBuffer requestURL = request.getRequestURL();
            String requestAddress = requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length()).toString();
            String address = requestAddress + contextPath;
            if (StringUtils.isNotBlank(path)) {
                address = address + "/" + path;
            } else {
                address = address + this.restPath;
            }

            try {
                requestMap = objectMapper.readValue(bodyContent, Map.class);

                // 插入参数
                /*if (RestBaseConstant.SECRET_LEVEL_3.equals(secretLevel)) {
                    // 拿取key，token
                    CrmCenterManager crmCenterManager = CrmCenterManager.getInstance(properties.getSsoCenterUrl(), properties.getSsoCenterChannel(), properties.getSsoCenterType());
                    String key = crmCenterManager.getSK(token);
                    if (StringUtils.isBlank(key)) {
                        throw new BizErrorBusinessException("token 不正确");
                    }

                    bodyContent = jackson.writeValueAsString(requestMap);
                    // AES加密
                    bodyContent = token + AES256Utils.AES_Encode(bodyContent, key);
                    requestMap = new HashMap<String, Object>();
                    requestMap.put(RestBaseConstant.L3_KEY_D, bodyContent);
                } else */
                if (RestBaseConstant.SECRET_LEVEL_2.equals(secretLevel)) {
                    requestMap.put(RestBaseConstant.PARAMS_APPID, properties.getAppId());
                    requestMap.put(RestBaseConstant.PARAMS_EVENT_TIME, String.valueOf(new Date().getTime()));
                    // 加密参数
                    String md5 = SecurityUtil.getMd5Sign(requestMap, properties.getSecretKey());
                    requestMap.put(RestBaseConstant.PARAMS_SIGN, md5);
                }

                requestContent = getObjectContent(requestMap);
                resultContent = HttpUtils.doPost(address, requestContent);

            } catch (BizErrorBusinessException e) {
                resultContent = e.getMessage();
            } catch (Exception e) {
                resultContent = "异常" + e.getMessage();
            } finally {
                long endTime = System.currentTimeMillis();
                //输出LOG
                String responseContent = "\n , response result : " + getObjectContent(resultContent);
                logger.info("test page request url : " + address + " , use time : " + (endTime - startTime) + "\n, request content " + requestContent + responseContent);
            }

            responseMap.put("requestContent", requestContent);
            responseMap.put("resultContent", resultContent);
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(getObjectContent(responseMap));
        writer.flush();
        writer.close();
    }

    private String getObjectContent(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("getObjectContent writeValueAsString error." + e.getMessage(), e);
        }
        return "";
    }
}
