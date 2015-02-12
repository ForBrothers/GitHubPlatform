package com.platform.frame.login.web;

import com.platform.frame.common.bean.JsonResult;
import com.platform.frame.common.util.CommonConstants;
import com.platform.frame.user.bean.SysUser;
import com.platform.frame.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping(value = "/frame")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView goLoginPage() {
        ModelAndView view = new ModelAndView();
        view.setViewName("frame/login/login");
        return view;
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public @ResponseBody JsonResult doLogin(SysUser loginUser,HttpSession session){
        JsonResult jsonResult = new JsonResult();
        SysUser sysUser = userService.queryUserByName(loginUser.getUserName());
        if(sysUser == null) {
            jsonResult.setResult(false);
            jsonResult.setMsg("用户不存在");
        }
        else
        {
            if(!sysUser.getUserPassword().equals(loginUser.getUserPassword())) {
                jsonResult.setResult(false);
                jsonResult.setMsg("密码错误");
            }
            else
            {
                jsonResult.setResult(true);
                session.setAttribute(CommonConstants.USER_SESSION_ATTR,sysUser);
            }
        }
        return jsonResult;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView goIndexPage(){
        ModelAndView view = new ModelAndView();
        view.setViewName("frame/index");
        return view;
    }
    @RequestMapping(value = "/loginOut", method = { RequestMethod.GET, RequestMethod.POST})
    public ModelAndView loginOut(HttpSession session){
        session.removeAttribute(CommonConstants.USER_SESSION_ATTR);
        session.invalidate();
        ModelAndView view = new ModelAndView();
        view.setViewName("frame/login/login");
        return view;
    }


}
