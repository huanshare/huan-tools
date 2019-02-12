package com.huanshare.tools.mybatis.dbtime;

import com.huanshare.tools.entity.BaseEntity;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by huanshare on 2018/11/24.
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DBTimeInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(DBTimeInterceptor.class);

    private DBTimeDialect dialect;

    public void setDialect(DBTimeDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] queryArgs = invocation.getArgs();
        Object params = invocation.getArgs()[1];
        if (params instanceof BaseEntity) {
            MappedStatement mappedStatement = (MappedStatement) queryArgs[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

            BaseEntity baseEntity = (BaseEntity) params;
            Date date = queryNowDate(dialect.getSql(), mappedStatement);
            if("INSERT".equalsIgnoreCase(sqlCommandType.name())){
                baseEntity.setCreatedTime(date);
                baseEntity.setLastModifiedTime(date);
            }else if("UPDATE".equalsIgnoreCase(sqlCommandType.name())){
                baseEntity.setLastModifiedTime(date);
            }
        }
        return invocation.proceed();
    }

    private Date queryNowDate(String sql, MappedStatement mappedStatement) throws SQLException {

        Connection connection = null;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {

            connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            countStmt = connection.prepareStatement(sql);
            rs = countStmt.executeQuery();
            Date nowDate = null;
            if (rs.next()) {
                nowDate = rs.getTimestamp(1);
            }
            return nowDate;
        } catch (SQLException e) {
            logger.error("查询当前数据库时间出错", e);
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("exception happens when doing: ResultSet.close()", e);
                }
            }

            if (countStmt != null) {
                try {
                    countStmt.close();
                } catch (SQLException e) {
                    logger.error("exception happens when doing: PreparedStatement.close()", e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("exception happens when doing: Connection.close()", e);
                }
            }
        }

    }

    @Override
    public Object plugin(Object target) {
        if (StatementHandler.class.isAssignableFrom(target.getClass())
                || Executor.class.isAssignableFrom(target.getClass())) {
            return Plugin.wrap(target, this);
        }

        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        String dialectClass = properties.getProperty("dialectClass");

        try {
            setDialect((DBTimeDialect) Class.forName(dialectClass).newInstance());
        } catch (Exception e) {
            throw new RuntimeException("cannot create dialect instance by dialectClass:" + dialectClass, e);
        }
    }

}
