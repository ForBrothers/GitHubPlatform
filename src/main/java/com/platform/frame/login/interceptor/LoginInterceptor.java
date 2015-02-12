package com.platform.frame.login.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.platform.frame.common.bean.JsonError;
import com.platform.frame.common.util.CommonConstants;
import com.platform.frame.common.util.ErrorCode;
import com.platform.frame.user.bean.SysUser;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Set;


public class LoginInterceptor implements HandlerInterceptor {

    private static final  Set<String> unCheckUrlSet = new HashSet<String>();

    public LoginInterceptor(){
        super();
        unCheckUrlSet.add("/frame/login");
        unCheckUrlSet.add("/frame/signIn");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
                                Exception exception) throws Exception {
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2,
                           ModelAndView modelAndView) throws Exception {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String url = request.getServletPath().toString();
        if(unCheckUrlSet.contains(url)) {
            return true;
        }
        //请求的路径
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(CommonConstants.USER_SESSION_ATTR);
        if (user == null) {
            //ajax访问，如果超时就返回
            if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))|| "true".equals(request.getParameter("isAjax")))
            {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(500);
                JsonError jsonError = new JsonError();
                jsonError.setErrorCode(ErrorCode.LOGIN_TIMEOUT_CODE);
                jsonError.setErrorMsg("登录超时");
                response.getWriter().print(JSONObject.fromObject(jsonError).toString());
                return false;
            }
            else
            {
                //普通页面跳转被拦截，重定向到login界面
                response.sendRedirect(contextPath + "/frame/login");
                return false;
            }
        }
        return true;
    }
}
