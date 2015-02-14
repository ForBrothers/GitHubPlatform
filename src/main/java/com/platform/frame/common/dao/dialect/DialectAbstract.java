package com.platform.frame.common.dao.dialect;

public abstract class DialectAbstract {
    public static enum Type {
        MYSQL
    }

    /**
     * 获取分页sql - 如果要支持其他数据库，修改这里就可以
     *
     * @param sql 原查询sql
     * @return 返回分页sql
     */
    public abstract String getPageSql(String sql);

    /**
     * 获取分页前置sql
     *
     * @return
     */
    public abstract String getPageSqlBefore();

    /**
     * 获取分页后置sql
     *
     * @return
     */
    public abstract String getPageSqlAfter();

    /**
     * 获取统计sql
     *
     * @return
     */
    public abstract String getCountSql(String sql);

    /**
     * 获取统计前置sql
     *
     * @return
     */
    public abstract String getCountSqlBefore();

    /**
     * 获取统计后置sql
     *
     * @return
     */
    public abstract String getCountSqlAfter();
}