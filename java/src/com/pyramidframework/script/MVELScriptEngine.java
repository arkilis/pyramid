package com.pyramidframework.script;

import java.util.Map;

import org.mvel.MVEL;

/**
 * MVELʵ�ֵĽű�����1.3.12������MVEL�Ľű���Ϣ����μ�http://mvel.codehaus.org/��
 * 
 * @author Mikab Peng
 * 
 */
public class MVELScriptEngine implements CompilableScriptEngine {

	// Ӧ�����̰߳�ȫ��
	static {
		MVEL.setThreadSafe(true);
	}

	/**
	 * �ȱ���ű����м���� �����Ľű�����ɽ��л��洦��
	 */
	public Object compile(String exp) {

		return MVEL.compileExpression(exp);
	}

	/**
	 * MVEL�ǿ�ִ��Ԥ�����
	 */
	public boolean isCompilable() {
		return true;
	}

	/**
	 * ִ�нű����ҵõ����
	 */
	public Object runScript(Object script, Map scriptContext) {

		return MVEL.executeExpression(script, scriptContext);
	}

	/**
	 * ��ýű�������
	 */
	public String getScriptName() {
		return MVEL.NAME;
	}

}
