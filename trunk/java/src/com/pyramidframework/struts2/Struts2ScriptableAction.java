package com.pyramidframework.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;

import com.pyramidframework.script.CompilableScriptEngine;
import com.pyramidframework.script.MVELScriptEngine;
import com.pyramidframework.simpleconfig.ConfigContainer;
import com.pyramidframework.simpleconfig.SimpleConfigManager;
import com.pyramidframework.spring.SpringFactory;

/**
 * 通过配置脚本和信息去获得执行时的业务逻辑，最终在运行时执行脚本定义的业务逻辑 配置脚本的项： <br>
 * script:执行的脚本 <scriptEngine>脚本引擎的实现类名称，类必须实现{@link com.pyramidframework.script.CompilableScriptEngine}接口.
 * <br>
 * 主意：本类通过{@link com.pyramidframework.spring.SpringFactory}的getBeanFactory方法获得对spring的beanfactory的实现。
 * <br>
 * 脚本配置和是spring的配置都放置在config目录下,脚本配置的文件名为config.xml,spring为spring.xml。
 * 
 * @author Mikab Peng
 * 
 */
public class Struts2ScriptableAction {
	public static final String DEFAULT_ROOT_DIRECTORY = SpringFactory.DEFAULT_CONFIG_LOCATION_PREFIX;
	static SpringFactory springFactory = new SpringFactory(DEFAULT_ROOT_DIRECTORY);
	static SimpleConfigManager configManager = new SimpleConfigManager("config", DEFAULT_ROOT_DIRECTORY);

	static Class defaultScriptEngine = MVELScriptEngine.class;
	static String scriptItemName = "script";
	static String scriptEngineItemName = "scriptEngine";

	/**
	 * 通过配置的脚本和脚本引擎去执行，并得到最终的结果
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception {

		String functionPath = Struts2RequestHelper.getCurrentRequestURI();

		return executeWithFunctionPath(functionPath);

	}

	/**
	 * @param functionPath
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public String executeWithFunctionPath(String functionPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map scriptContext = initScriptContext(functionPath);

		ConfigContainer container = (ConfigContainer) configManager.getConfigData(functionPath);
		Object script = container.getData(scriptItemName);
		CompilableScriptEngine scriptEngine = null;

		if (script == null) {
			throw new NullPointerException("Action with no scripts!");
		}
		
		//初始化脚本和脚本对象
		if (script instanceof String) {// 未执行过初始化

			String scriptEngineClassName = container.getString(scriptEngineItemName);
			if (scriptEngineClassName == null || scriptEngineClassName.length() < 1) {
				scriptEngine = (CompilableScriptEngine) defaultScriptEngine.newInstance();
			} else {
				scriptEngine = (CompilableScriptEngine) Class.forName(scriptEngineClassName).newInstance();
			}
			container.setData(scriptEngineItemName, scriptEngine);

			// 预编译脚本
			script = scriptEngine.compile((String) script);
			container.setData(scriptItemName, script);
		} else {
			scriptEngine = (CompilableScriptEngine) container.getData(scriptEngineItemName);
		}
		
		
		return String.valueOf(scriptEngine.runScript(script, scriptContext));
	}

	/**
	 * 生成脚本执行的上下文，主要是生成beanfacotory的引用和功能路径的引用
	 */
	protected Map initScriptContext(String functionPath) {

		Map scriptContext = new HashMap();

		BeanFactory beanFactory = springFactory.getBeanFactory(functionPath);

		scriptContext.put("functionPath", functionPath);
		scriptContext.put("beanFactory", beanFactory);

		return scriptContext;
	}
}
