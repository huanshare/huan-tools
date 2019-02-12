package com.huanshare.tools.mvc.controller;

import com.huanshare.tools.mvc.constant.OperatorConstant;
import com.huanshare.tools.mvc.dto.ResponseBaseDto;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.utils.DateUtils;
import com.huanshare.tools.utils.JacksonUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/**
 *  2018/2/24.
 */
public class RestHeartControllerBean implements Controller {
    //日志
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestHeartControllerBean.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ResponseBaseDto dto = new ResponseBaseDto();
        dto.setSuccess(OperatorConstant.RETURN_SUCCESS);
        dto.setMessage(RestBaseConstant.OPERATOR_SUCCESS);
        dto.setResponseTime(DateUtils.dateToString(new Date()));
        // 返回字符串
        String result;
        try {
            result = JacksonUtils.getInstance().writeValueAsString(dto);
        } catch (Exception e) {
            result = "";
            logger.error("convert json error. " + e.getMessage(), e);
        }

        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        writer.print(result);
        writer.flush();
        writer.close();
        return null;
    }
}
