package com.pyramidframework.script;

import java.util.Map;

import ognl.Ognl;

/**
 * ����OGNL���ʽ���Ե�ʵ�֡�������Ϣ��μ�http://mirrors.ibiblio.org/pub/mirrors/maven2/ognl/ognl/
 * ��http://www.ognl.org/
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
	 * �õ��ű�������
	 */
	public String getScriptName() {

		return Ognl.class.getName();
	}

	/**
	 * �Ƿ�ɱ��룺��
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
