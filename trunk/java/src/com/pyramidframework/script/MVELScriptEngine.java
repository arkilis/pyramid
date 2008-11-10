package com.pyramidframework.script;

import java.util.Map;

import org.mvel.MVEL;

/**
 * MVEL实现的脚本引擎1.3.12。更多MVEL的脚本信息，请参见http://mvel.codehaus.org/。
 * 
 * @author Mikab Peng
 * 
 */
public class MVELScriptEngine implements CompilableScriptEngine {

	// 应该是线程安全的
	static {
		MVEL.setThreadSafe(true);
	}

	/**
	 * 先编译脚本成中间对象。 编译后的脚本对象可进行缓存处理
	 */
	public Object compile(String exp) {

		return MVEL.compileExpression(exp);
	}

	/**
	 * MVEL是可执行预编译的
	 */
	public boolean isCompilable() {
		return true;
	}

	/**
	 * 执行脚本并且得到结果
	 */
	public Object runScript(Object script, Map scriptContext) {

		return MVEL.executeExpression(script, scriptContext);
	}

	/**
	 * 获得脚本的名称
	 */
	public String getScriptName() {
		return MVEL.NAME;
	}

}
