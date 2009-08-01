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
 * ͨ�����ýű�����Ϣȥ���ִ��ʱ��ҵ���߼�������������ʱִ�нű������ҵ���߼� ���ýű���� <br>
 * script:ִ�еĽű� <scriptEngine>�ű������ʵ�������ƣ������ʵ��{@link com.pyramidframework.script.CompilableScriptEngine}�ӿ�.
 * <br>
 * ���⣺����ͨ��{@link com.pyramidframework.spring.SpringFactory}��getBeanFactory������ö�spring��beanfactory��ʵ�֡�
 * <br>
 * �ű����ú���spring�����ö�������configĿ¼��,�ű����õ��ļ���Ϊconfig.xml,springΪspring.xml��
 * 
 * @author Mikab Peng
 * 
 */
public class Struts2ScriptableAction {

	private HashMap modelDatas = new HashMap();
	protected VOFactory voFactory = null;
	protected List queryResult = null;

	/**
	 * �õ�ָ�����͵���������
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
	 * ����ģ�Ͷ�Ӧ����
	 * 
	 * @param model
	 * @param modelData
	 */
	public void setModel(String model, Object modelData) {
		modelDatas.put(model, modelData);
	}

	/**
	 * ͨ�����õĽű��ͽű�����ȥִ�У����õ����յĽ��
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

		// ��ʼ���ű��ͽű�����
		if (script instanceof String) {// δִ�й���ʼ��

			String scriptEngineClassName = container.getString(scriptEngineItemName);
			if (scriptEngineClassName == null || scriptEngineClassName.length() < 1) {
				scriptEngine = (CompilableScriptEngine) defaultScriptEngine.newInstance();
			} else {
				scriptEngine = (CompilableScriptEngine) Class.forName(scriptEngineClassName).newInstance();
			}
			container.setData(scriptEngineItemName, scriptEngine);

			// Ԥ����ű�
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
	 * ���ɽű�ִ�е������ģ���Ҫ������beanfacotory�����ú͹���·��������
	 */
	protected Map initScriptContext(BeanFactory beanFactory, ConfigContainer container, String functionPath) {

		Map scriptContext = new BeanFactoryScriptContextMap(beanFactory);

		// ��ʼ���ű�����ر���
		scriptContext.put("functionPath", functionPath);
		scriptContext.put("beanFactory", beanFactory);
		scriptContext.put("request", Struts2RequestHelper.getCurrentRequest());
		scriptContext.put("response", Struts2RequestHelper.getCurrentResponse());

		// config��ʵ��
		scriptContext.put("config", container);
		
		// action��ʵ��
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
	 * Ĭ�ϵ�����ֵ
	 */
	static final String DEFAULT_ROOT_DIRECTORY = SpringFactory.DEFAULT_CONFIG_LOCATION_PREFIX;
	static SpringFactory springFactory = SpringFactory.getDefaultInstance();
	static SimpleConfigManager configManager = new SimpleConfigManager("config", DEFAULT_ROOT_DIRECTORY);
	static Class defaultScriptEngine = MVELScriptEngine.class;
	static String scriptItemName = "script";
	static String scriptEngineItemName = "scriptEngine";
	static String VOFactoryBean = "voFactory";
}
