package com.pyramidframework.struts2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;

import com.pyramidframework.dao.VOFactory;
import com.pyramidframework.script.CompilableScriptEngine;
import com.pyramidframework.script.MVELScriptEngine;
import com.pyramidframework.simpleconfig.ConfigContainer;
import com.pyramidframework.simpleconfig.SimpleConfigManager;
import com.pyramidframework.spring.BeanFactoryScriptContextMap;
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

	private HashMap modelDatas = new HashMap();
	protected VOFactory voFactory = null;
	protected List queryResult = null;

	/**
	 * 得到指定类型的数据名称
	 * 
	 * @param model
	 * @return
	 */
	public Object getModel(String model) {
		
		Object object = modelDatas.get(model);
		if (object == null) {
			if(voFactory == null){
				String functionPath = Struts2RequestHelper.getCurrentRequestURI();
				BeanFactory beanFactory = springFactory.getBeanFactory(functionPath, Struts2RequestHelper.getCurrentRequest());
				voFactory = (VOFactory)beanFactory.getBean(VOFactoryBean);
			}
			object = voFactory.getValueObject(model);
			modelDatas.put(model, object);
		}
		
		return object;
	}

	/**
	 * 设置模型对应类型
	 * 
	 * @param model
	 * @param modelData
	 */
	public void setModel(String model, Object modelData) {
		modelDatas.put(model, modelData);
	}

	/**
	 * 通过配置的脚本和脚本引擎去执行，并得到最终的结果
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception {

		String functionPath = Struts2RequestHelper.getCurrentRequestURI();
		
		return executeWithFunctionPath( functionPath);

	}

	/**
	 * @param functionPath
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public String executeWithFunctionPath( String functionPath) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		ConfigContainer container = (ConfigContainer) configManager.getConfigData(functionPath);
		BeanFactory beanFactory = springFactory.getBeanFactory(functionPath, Struts2RequestHelper.getCurrentRequest());

		Map scriptContext = initScriptContext(beanFactory, container, functionPath);

		Object script = container.getData(scriptItemName);
		CompilableScriptEngine scriptEngine = null;

		if (script == null) {
			throw new NullPointerException("Action with no scripts!");
		}

		// 初始化脚本和脚本对象
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

		return String.valueOf(runScript(beanFactory, scriptContext, script, scriptEngine));
	}

	/**
	 * @param scriptContext
	 * @param script
	 * @param scriptEngine
	 * @return
	 */
	protected Object runScript(BeanFactory beanFactory, Map scriptContext, Object script, CompilableScriptEngine scriptEngine) {

		return scriptEngine.runScript(script, scriptContext);
	}

	/**
	 * 生成脚本执行的上下文，主要是生成beanfacotory的引用和功能路径的引用
	 */
	protected Map initScriptContext(BeanFactory beanFactory, ConfigContainer container, String functionPath) {

		Map scriptContext = new BeanFactoryScriptContextMap(beanFactory);

		// 初始化脚本的相关变量
		scriptContext.put("functionPath", functionPath);
		scriptContext.put("beanFactory", beanFactory);
		scriptContext.put("request", Struts2RequestHelper.getCurrentRequest());
		scriptContext.put("response", Struts2RequestHelper.getCurrentResponse());

		// config的实例
		scriptContext.put("config", container);
		
		// action的实例
		scriptContext.put("action", this);

		return scriptContext;
	}
	
	public List getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(List queryResult) {
		this.queryResult = queryResult;
	}

	/**
	 * 默认的属性值
	 */
	static final String DEFAULT_ROOT_DIRECTORY = SpringFactory.DEFAULT_CONFIG_LOCATION_PREFIX;
	static SpringFactory springFactory = SpringFactory.getDefaultInstance();
	static SimpleConfigManager configManager = new SimpleConfigManager("config", DEFAULT_ROOT_DIRECTORY);
	static Class defaultScriptEngine = MVELScriptEngine.class;
	static String scriptItemName = "script";
	static String scriptEngineItemName = "scriptEngine";
	static String VOFactoryBean = "voFactory";
}
