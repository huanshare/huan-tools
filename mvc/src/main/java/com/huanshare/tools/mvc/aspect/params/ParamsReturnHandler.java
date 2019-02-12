package com.huanshare.tools.mvc.aspect.params;

import com.huanshare.tools.mvc.constant.OperatorConstant;
import com.huanshare.tools.mvc.dto.ResponseBaseDto;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.utils.CommonUtils;
import com.huanshare.tools.utils.DateUtils;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.mvc.constant.OperatorConstant;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 *  2017/5/18.
 */
public class ParamsReturnHandler extends AbstractParamsReturnHandler {
    @Override
    public void doReturnHandler(Object returnObj, Map requestMap) throws Exception {
        if (returnObj instanceof ResponseBaseDto) {
            ResponseBaseDto responseBaseDto = (ResponseBaseDto) returnObj;
            responseBaseDto.setResponseTime(getResponseTime());
            if (StringUtils.isBlank(responseBaseDto.getSuccess())) {
                responseBaseDto.setSuccess(OperatorConstant.RETURN_SUCCESS);
            }
            if (StringUtils.isBlank(responseBaseDto.getMessage())) {
                responseBaseDto.setMessage(RestBaseConstant.OPERATOR_SUCCESS);
            }
            if (requestMap != null) {
                responseBaseDto.setRequestUUID(CommonUtils.getString(requestMap.get(RestBaseConstant.FROMSOURCEID)));
            }
        }
    }

    private String getResponseTime() throws ClassNotFoundException {
        Object obj = null;
        Object showTimeService = getShowTimeService();
        String showTimeServiceMethod = getShowTimeServiceMethod();
        if (showTimeService != null && StringUtils.isNotBlank(showTimeServiceMethod)) {
            Method method = ReflectionUtils.findMethod(showTimeService.getClass(), showTimeServiceMethod);
            obj = ReflectionUtils.invokeMethod(method, showTimeService);
        }

        if (obj != null) {
            return obj.toString();
        }
        return DateUtils.dateToString(new Date());
    }
}
