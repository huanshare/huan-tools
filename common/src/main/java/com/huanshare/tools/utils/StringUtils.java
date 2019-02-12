package com.huanshare.tools.utils;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class StringUtils extends org.apache.commons.lang3.StringUtils{

	/**
	 * 含有下划线转为驼峰字段
	 * @param column
	 * @return
	 */
	public static String columnToProperty(String column) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (column == null || column.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!column.contains("_")) {
			// 不含下划线，仅将首字母小写
			return column.substring(0, 1).toLowerCase() + column.substring(1);
		} else {
			// 用下划线将原始字符串分割
			String[] columns = column.split("_");
			for (String columnSplit : columns) {
				// 跳过原始字符串中开头、结尾的下换线或双重下划线
				if (columnSplit.isEmpty()) {
					continue;
				}
				// 处理真正的驼峰片段
				if (result.length() == 0) {
					// 第一个驼峰片段，全部字母都小写
					result.append(columnSplit.toLowerCase());
				} else {
					// 其他的驼峰片段，首字母大写
					result.append(columnSplit.substring(0, 1).toUpperCase()).append(columnSplit.substring(1).toLowerCase());
				}
			}
			return result.toString();
		}
	}

	/**
	 * 字符串驼峰转为下划线字符串
	 * @param property
	 * @return
	 */
	public static String propertyToColumn(String property){
		if (property == null || property.isEmpty()){
			return "";
		}
		StringBuilder column = new StringBuilder();
		column.append(property.substring(0,1).toLowerCase());
		for (int i = 1; i < property.length(); i++) {
			String s = property.substring(i, i + 1);
			// 在小写字母前添加下划线
			if(Character.isAlphabetic(s.charAt(0)) && s.equals(s.toUpperCase())){
				column.append("_");
			}
			// 其他字符直接转成小写
			column.append(s.toLowerCase());
		}

		return column.toString();
	}

	/**
	 * 转换第一个字母为大写
	 *
	 * @param str
	 * @return
	 */
	public static String toFirstUpperCase(String str) {
		if (isEmpty(str)) {
			return str;
		}

		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String toFirstLowerCase(String str) {
		if (isEmpty(str)) {
			return str;
		}

		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 组织成SQL中In的参数
	 * @param list
	 * @return
	 */
	public static String buildInSQLParams(List list){
		if(CollectionUtils.isEmpty(list)){
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for(Object o : list){
			if(o instanceof String){
				sb.append(",\'").append(o).append("\'");
			}else{
				sb.append(",").append(o);
			}
		}

		return sb.toString().replaceFirst(",","");
	}

	public static String defaultString(String value, String defaultValue) {
		if(StringUtils.isBlank(value)){
			return defaultValue;
		}
		return value;
	}
}
