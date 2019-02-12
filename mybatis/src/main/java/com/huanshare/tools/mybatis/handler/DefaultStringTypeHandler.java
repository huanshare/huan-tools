package com.huanshare.tools.mybatis.handler;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes(String.class)
public class DefaultStringTypeHandler implements TypeHandler<String> {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter);
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		if(rs.getString(columnName) != null){
			return StringEscapeUtils.escapeHtml4(rs.getString(columnName));
		}
		return null;
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		if(rs.getString(columnIndex) != null){
			return StringEscapeUtils.escapeHtml4(rs.getString(columnIndex));
		}
		return null;
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		if(cs.getString(columnIndex) != null){
			return StringEscapeUtils.escapeHtml4(cs.getString(columnIndex));
		}
		return null;
	}

}
