package com.pyramidframework.dao.model.datatype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ����������ʽ����ƥ�䣬�Ӷ�ȷ����������
 * 
 * @author Mikab Peng
 * 
 */
public abstract class AbstractDatatypeFactory implements DataTypeFactory {

	protected Pattern ddlPattern;

	public AbstractDatatypeFactory(Pattern ddlPattern) {
		this.ddlPattern = ddlPattern;
	}

	public DataType fromDBSchema(String columnDescription) {
		Matcher matcher = ddlPattern.matcher(columnDescription);
		if (matcher.matches()) {
			return createDataType(matcher);
		}
		return null;
	}
	

	protected abstract DataType createDataType(Matcher matcher);

	public Pattern getDdlPattern() {
		return ddlPattern;
	}

	public void setDdlPattern(Pattern ddlPattern) {
		this.ddlPattern = ddlPattern;
	}
	
	

}
