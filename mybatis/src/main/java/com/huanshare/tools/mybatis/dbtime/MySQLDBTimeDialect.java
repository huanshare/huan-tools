package com.huanshare.tools.mybatis.dbtime;

/**
 *
 */
public class MySQLDBTimeDialect extends DBTimeDialect {

    @Override
    public String getSql() {
        return "select now()";
    }
}
