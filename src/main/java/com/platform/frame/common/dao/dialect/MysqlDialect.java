package com.platform.frame.common.dao.dialect;

public class MysqlDialect extends DialectAbstract {

    public String getPageSql(String sql) {
        return getPageSqlBefore() + sql + getPageSqlAfter();
    }

    public String getPageSqlBefore() {
        return " ";
    }

    public String getPageSqlAfter() {
        return "  limit ?,?";
    }

    public String getCountSql(String sql) {
        return getCountSqlBefore() + sql + getCountSqlAfter();
    }

    public String getCountSqlBefore() {
        return "select count(0) from (";
    }

    public String getCountSqlAfter() {
        return " ) tmpCount";
    }

}
