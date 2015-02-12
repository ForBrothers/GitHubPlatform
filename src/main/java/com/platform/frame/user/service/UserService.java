package com.platform.frame.user.service;

import com.platform.frame.user.bean.SysUser;
import com.platform.frame.user.dao.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private SysUserDao sysUserDao;

    public SysUser queryUserByName(String userName){
        return sysUserDao.queryUserByName(userName) ;
    }

    public List<SysUser> queryUserList(){
        return sysUserDao.queryUserList();
    }
}
