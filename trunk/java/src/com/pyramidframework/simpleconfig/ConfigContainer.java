package com.pyramidframework.simpleconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �������ݵļ��ϵ�����
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigContainer implements Cloneable {
	HashMap datas = new HashMap(); // ���ڵ��ȫ������
	HashMap pastDatas = new HashMap();// ���ڵ��з��������Ͻڵ������

	/**
	 * ���ؽڵ�������е��������ݵļ�ֵ�ԡ� ���һ�����ж��ֵ������ֵ��List��ʵ����
	 * 
	 * @return
	 */
	public Map getAllDatas() {
		return (Map) datas.clone();
	}

	/**
	 * �õ����νڵ������������øı�Ĵ��ϼ��̳��������ݡ� ���һ�����ж��ֵ������ֵ��List��ʵ��
	 * 
	 * @return
	 */
	public Map getDataChanges() {

		return (Map) pastDatas.clone();
	}

	/**
	 * �õ�������Ϣ��ָ��name���Լ�ֵ�µ����������е��ַ������� ����������ݲ����ַ��������׳��쳣IllegalArgumentException
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		Object o = getData(name);
		if (o == null || o instanceof String) {
			return (String) o;
		}
		throw new IllegalArgumentException("The data " + name + " is :" + o.toString());
	}

	/**
	 * �õ�������Ϣ��ָ��name���Լ�ֵ�µ����������е�����
	 * 
	 * @param name
	 * @return
	 */
	public Object getData(String name) {
		Object o = datas.get(name);
		if (o instanceof List) {
			o = ((List) o).get(0);
		}
		return o;
	}

	/**
	 * ���ƶ�������ֻ���ƶ�������е���������
	 */
	public Object clone() throws CloneNotSupportedException {
		ConfigContainer newContainer = new ConfigContainer();
		newContainer.datas = (HashMap) datas.clone();
		return newContainer;
	}

	/**
	 * �õ�������Ϣ��ָ��name���Լ�ֵ�µ����������е���������
	 * 
	 * @param name
	 * @return
	 */
	public List getAllDatas(String name) {
		Object o = datas.get(name);
		if (o == null) {
			return null;
		}
		if (o instanceof ArrayList) {// �ڲ���ʹ��ArrayList����������
			return (ArrayList) ((ArrayList) o).clone();
		}
		List l = new ArrayList(1);
		l.add(o);
		return l;
	}

	/**
	 * �����������һ��������Ϊ��һ����
	 * 
	 * @param obj
	 *            �����ȽϵĶ���
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass().equals(this.getClass())) {
			ConfigContainer t = (ConfigContainer) obj;
			if (datas.equals(t.datas) && pastDatas.equals(t.pastDatas)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ����������ʱ������о���������������������
	 * 
	 * @param name
	 * @param data
	 */
	protected void addData(String name, Object data) {
		Object o = getData(name);
		if (o == null) {
			setData(name, data);
		} else if (!pastDatas.containsKey(name)) {// �Ѿ��ڷ�����������ָ��
			pastDatas.put(name, getData(name));
			setData(name, data);
		} else {
			if (o instanceof ArrayList) {
				((ArrayList) o).add(data);
			} else {
				// �������һ������������ʽ�洢
				ArrayList a = new ArrayList(3);
				a.add(o);
				a.add(data);
				setData(name, data);
			}
		}
	}

	/**
	 * ���µ������滻���ɵ�����
	 * 
	 * @param name
	 * @param data
	 */
	public void setData(String name, Object data) {
		if (name != null) { // ����ʵ��
			name = name.intern();
		}

		if (data != null && data instanceof String) {
			data = ((String) data).intern();
		}

		datas.put(name, data);
	}

	void removeData(String name) {
		if (datas.containsKey(name)) {
			pastDatas.put(name, getData(name));
			datas.remove(name);
		}
	}
}
