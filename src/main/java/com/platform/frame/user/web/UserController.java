package com.platform.frame.user.web;

import com.platform.frame.common.bean.TableDataBean;
import com.platform.frame.user.bean.SysUser;
import com.platform.frame.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/userManagePage", method = {RequestMethod.GET,RequestMethod.POST})
     public ModelAndView userManagePage(){
        ModelAndView view = new ModelAndView();
        view.setViewName("frame/user/userManagePage");
        return view;
    }

    @RequestMapping(value = "/queryUserList", method = {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody TableDataBean queryUserList(){
        TableDataBean tableDataBean = new TableDataBean();
        List<SysUser> list = userService.queryUserList();
        if(list != null) {
            tableDataBean.setTotal(list.size());
            tableDataBean.setRows(list);
        }
        return tableDataBean;
    }


}
