package com.huanshare.tools.mvc.advice;

import com.huanshare.tools.mvc.constant.ErrorCodeConstant;
import com.huanshare.tools.mvc.constant.OperatorConstant;
import com.huanshare.tools.mvc.dto.ResponseBaseDto;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.exception.BizTokenException;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.CommonUtils;
import com.huanshare.tools.utils.DateUtils;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.exception.BizTokenException;
import com.huanshare.tools.mvc.constant.ErrorCodeConstant;
import com.huanshare.tools.mvc.constant.OperatorConstant;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 *
 */
@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseBaseDto errorResponse(HttpServletRequest request, HandlerMethod handlerMethod, Exception ex) {
        String fromSourceId = "";
        ParamsBean params = paramsThreadLocalManager.getParams();
        Map<String, Object> paramValueMap = params != null ? params.getAllRequestParamsMap() : null;
        if (paramValueMap != null) {
            fromSourceId = CommonUtils.getString(paramValueMap.get(RestBaseConstant.FROMSOURCEID));
        }

        String success = OperatorConstant.RETURN_FAILURE;
        String errorMessage;
        String errorCode;
        if (ex instanceof BizErrorBusinessException) {
            errorMessage = ex.getMessage();
            errorCode = ((BizErrorBusinessException) ex).getErrorCode();
        } else if (ex instanceof BizTokenException) {
            errorMessage = ex.getMessage();
            success = OperatorConstant.RETURN_FAILURE_TOKEN;
            errorCode = ((BizTokenException) ex).getErrorCode();
        } else {
            errorMessage = RestBaseConstant.OPERATOR_FAILURE;
            errorCode = ErrorCodeConstant.ERROR_CODE_10002;
            logger.error("URI : " + request.getRequestURI() + " , fromSourceId : " + fromSourceId + " , error message : " + ex.getMessage(), ex);
        }

        ResponseBaseDto dto = new ResponseBaseDto();
        dto.setSuccess(success);
        dto.setMessage(errorMessage);
        dto.setResponseTime(DateUtils.dateToString(new Date()));
        if (StringUtils.isNotBlank(errorCode)) {
            dto.setErrorCode(errorCode);
        }
        return dto;
    }
}
