package com.platform.frame.common.exception;


import com.platform.frame.common.bean.JsonError;
import com.platform.frame.common.util.ErrorCode;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(SysMappingExceptionResolver.class);

    @Override
        protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            // Expose ModelAndView for chosen error view.
            String viewName = determineViewName(ex, request);
            if (viewName != null) {// JSP格式返回
                if (!(request.getHeader("accept").indexOf("application/json") > -1 ||
                        (request.getHeader("X-Requested-With")!= null &&
                                request .getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
                    // 如果不是异步请求
                    // Apply HTTP status code for error views, if specified.
                    // Only apply it if we're processing a top-level request.
                    Integer statusCode = determineStatusCode(request, viewName);
                    if (statusCode != null) {
                        applyStatusCodeIfPossible(request, response, statusCode);
                    }
                    logger.info("doResolveException return jsp, error statusCode {} ",statusCode);
                    return getModelAndView(viewName, ex, request);
                }
                else {// JSON格式返回
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("text/html;charset=utf-8");
                    response.setStatus(500);
                    JsonError jsonError = new JsonError();
                    jsonError.setResult(false);
                    if(ex instanceof CommonException) {
                        jsonError.setErrorMsg(ex.getMessage());
                        jsonError.setErrorCode(ErrorCode.SYSTEM_ERROR_CODE);
                    }
                    else if(ex instanceof DataAccessException) {
                        jsonError.setErrorMsg("数据库异常");
                        jsonError.setErrorCode(ErrorCode.DATABASE_ERROR_CODE);
                    }
                    else {
                        jsonError.setErrorMsg("系统异常");
                        jsonError.setErrorCode(ErrorCode.SYSTEM_ERROR_CODE);
                    }
                    logger.error("doResolveException return ajax,error code {}, error info {}",jsonError.getErrorCode(),
                            jsonError.getErrorMsg());
                    try {
                        response.getWriter().print(JSONObject.fromObject(jsonError));
                    } catch (IOException ioException) {
                        logger.info("responseStream error", ioException);
                    }
                    return null;
                }
            } else {
                return null;
            }
        }
}
