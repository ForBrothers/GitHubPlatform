package com.platform.frame.user.dao;

import com.platform.frame.common.annotation.MyBatisRepository;
import com.platform.frame.user.bean.SysUser;

import java.util.List;

@MyBatisRepository
public interface SysUserDao {

    public SysUser queryUserByName(String userName);

    public int insert(SysUser sysUser);

    public List<SysUser> queryUserList();
}
