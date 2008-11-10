package com.pyramidframework.script;

import java.util.Map;

import ognl.Ognl;

/**
 * 基于OGNL表达式语言的实现。更多信息请参见http://mirrors.ibiblio.org/pub/mirrors/maven2/ognl/ognl/
 * 和http://www.ognl.org/
 * 
 * @author Mikab Peng
 * 
 */
public class OGNLScriptEngine implements CompilableScriptEngine {

	public Object compile(String exp) {
		try {
			return Ognl.parseExpression(exp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到脚本的名字
	 */
	public String getScriptName() {

		return Ognl.class.getName();
	}

	/**
	 * 是否可编译：是
	 */
	public boolean isCompilable() {

		return true;
	}

	public Object runScript(Object script, Map scriptContext) {

		try {
			return Ognl.getValue(script, scriptContext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
