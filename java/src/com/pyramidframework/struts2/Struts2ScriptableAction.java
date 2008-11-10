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
	public static final String DEFAULT_ROOT_DIRECTORY = SpringFactory.DEFAULT_CONFIG_LOCATION_PREFIX;
	static SpringFactory springFactory = new SpringFactory(DEFAULT_ROOT_DIRECTORY);
	static SimpleConfigManager configManager = new SimpleConfigManager("config", DEFAULT_ROOT_DIRECTORY);

	static Class defaultScriptEngine = MVELScriptEngine.class;
	static String scriptItemName = "script";
	static String scriptEngineItemName = "scriptEngine";

	/**
	 * ͨ�����õĽű��ͽű�����ȥִ�У����õ����յĽ��
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
		
		//��ʼ���ű��ͽű�����
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
		
		
		return String.valueOf(scriptEngine.runScript(script, scriptContext));
	}

	/**
	 * ���ɽű�ִ�е������ģ���Ҫ������beanfacotory�����ú͹���·��������
	 */
	protected Map initScriptContext(String functionPath) {

		Map scriptContext = new HashMap();

		BeanFactory beanFactory = springFactory.getBeanFactory(functionPath);

		scriptContext.put("functionPath", functionPath);
		scriptContext.put("beanFactory", beanFactory);

		return scriptContext;
	}
}
