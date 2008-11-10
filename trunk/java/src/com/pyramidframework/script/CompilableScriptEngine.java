package com.pyramidframework.script;

import java.util.Map;

/**
 * 可进行预编译的脚本引擎的接口。 此接口主要用于两个地方：配置文件中的配置信息获取和流程定义或扩展
 * 
 * @author Mikab Peng
 * 
 */
public interface CompilableScriptEngine {

	/**
	 * 编译一段脚本到一个可执行的脚本内容
	 * 
	 * @param exp
	 *            脚本的表达式
	 * @return
	 */
	public Object compile(String exp);

	/***************************************************************************
	 * 执行具体的脚本，并且得到执行结果
	 * 
	 * @param script
	 *            脚本的语句
	 * @param scriptContext
	 *            执行脚本的根上下文
	 * @return
	 */
	public Object runScript(Object script, Map scriptContext);

	/**
	 * 该脚本引擎是否是可执行先编译后执行的方式，如果返回true则先编译后执行，否则直接执行脚本的表达式
	 * 
	 * @return true or false
	 */
	public boolean isCompilable();

	/**
	 * 获得执行的脚本的名称，如Mvel,OGNL等
	 * 
	 * @return
	 */
	public String getScriptName();

}
