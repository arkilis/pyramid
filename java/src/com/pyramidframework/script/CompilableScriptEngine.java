package com.pyramidframework.script;

import java.util.Map;

/**
 * �ɽ���Ԥ����Ľű�����Ľӿڡ� �˽ӿ���Ҫ���������ط��������ļ��е�������Ϣ��ȡ�����̶������չ
 * 
 * @author Mikab Peng
 * 
 */
public interface CompilableScriptEngine {

	/**
	 * ����һ�νű���һ����ִ�еĽű�����
	 * 
	 * @param exp
	 *            �ű��ı��ʽ
	 * @return
	 */
	public Object compile(String exp);

	/***************************************************************************
	 * ִ�о���Ľű������ҵõ�ִ�н��
	 * 
	 * @param script
	 *            �ű������
	 * @param scriptContext
	 *            ִ�нű��ĸ�������
	 * @return
	 */
	public Object runScript(Object script, Map scriptContext);

	/**
	 * �ýű������Ƿ��ǿ�ִ���ȱ����ִ�еķ�ʽ���������true���ȱ����ִ�У�����ֱ��ִ�нű��ı��ʽ
	 * 
	 * @return true or false
	 */
	public boolean isCompilable();

	/**
	 * ���ִ�еĽű������ƣ���Mvel,OGNL��
	 * 
	 * @return
	 */
	public String getScriptName();

}
