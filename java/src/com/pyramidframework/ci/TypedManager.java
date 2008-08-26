package com.pyramidframework.ci;

/**
 * ָ�������͵�������Ϣ���������Ϣ������
 * 
 * @author Mikab Peng
 * 
 */
public class TypedManager extends ConfigurationManager {
	private String configType = null;
	protected ConfigDocumentParser parser = null;

	/**
	 * �õ�������Ϣת�����������Ϣ��
	 * 
	 * @param functionPath
	 *            ����·��
	 * @return
	 */
	public ConfigDomain getConfigDomain(String functionPath) {
		return super.getConfigDomain(functionPath, configType);
	}

	/**
	 * �õ�������Ϣת���������
	 * 
	 * @param functionPath
	 *            ����·��
	 * @return
	 */
	public Object getConfigData(String functionPath) {

		return super.getConfigData(functionPath, configType);

	}

	/**
	 * ���캯��
	 * 
	 * @param configType
	 *            ָ����������Ϣ����
	 */
	public TypedManager(String configType) {
		super();
		if (configType == null){
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}
	
	/**
	 * ����ָ�������ͺͽ��������������
	 * @param configType ������Ϣ����
	 * @param typeParser �����ļ�������
	 */
	public TypedManager(String configType, ConfigDocumentParser typeParser) {
		super();
		
		if (typeParser == null){
			throw new NullPointerException("typeParser parameter is null!");
		}
		this.parser = typeParser;
		
		if (configType == null){
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}
	
	/**
	 * �������ʱ�����˽���������ֱ�ӷ��ؽ�����
	 * @param configType ������Ϣ����
	 */
	public ConfigDocumentParser getInstanceParser(String configType) {
		if(this.configType.equals(configType) && parser != null){
			return parser;
		}
		return super.getGlobalParser(configType);
	}
	
	/**
	 * ��ȡ��Ļ��������Ϣ������
	 * @return
	 */
	public String getConfigType() {
		return configType;
	}
	
	/**
	 * �趨������Ϣ������
	 * @param configType �µ�������Ϣ����
	 */
	public void setConfigType(String configType) {
		if (configType == null){
			throw new NullPointerException("configType parameter is null!");
		}
		this.configType = configType;
	}
	
	/**
	 * ��ȡ�ڲ����еĽ�������ʵ��
	 * @return ��������ʵ��
	 */
	public ConfigDocumentParser getParser() {
		return parser;
	}
	
	/**
	 * ָ���µ�������Ϣ������
	 * @param TypeParser
	 */
	public void setParser(ConfigDocumentParser TypeParser) {
		if (TypeParser == null){
			throw new NullPointerException("typeParser parameter is null!");
		}
		this.parser = TypeParser;
	}
}
