package com.huanshare.tools.mybatis.dbtime;

/**
 *
 */
public class OracleDBTimeDialect extends DBTimeDialect {

    @Override
    public String getSql() {
        return "select sysdate from dual";
    }
}
