package com.pyramidframework.simpleconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置数据的集合的容器
 * 
 * @author Mikab Peng
 * 
 */
public class ConfigContainer implements Cloneable {
	HashMap datas = new HashMap(); // 本节点的全部数据
	HashMap pastDatas = new HashMap();// 本节点中废弃掉的上节点的数据

	/**
	 * 返回节点里的所有的配置数据的键值对。 如果一个键有多个值，则以值是List的实例。
	 * 
	 * @return
	 */
	public Map getAllDatas() {
		return (Map) datas.clone();
	}

	/**
	 * 得到本次节点配置数据所该改变的从上级继承来的数据。 如果一个键有多个值，则以值是List的实例
	 * 
	 * @return
	 */
	public Map getDataChanges() {

		return (Map) pastDatas.clone();
	}

	/**
	 * 得到配置信息中指定name属性键值下的数据配置中的字符串数据 如果配置数据不是字符串，则抛出异常IllegalArgumentException
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
	 * 得到配置信息中指定name属性键值下的数据配置中的数据
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
	 * 复制对象自身，只复制对象的容有的数据容器
	 */
	public Object clone() throws CloneNotSupportedException {
		ConfigContainer newContainer = new ConfigContainer();
		newContainer.datas = (HashMap) datas.clone();
		return newContainer;
	}

	/**
	 * 得到配置信息中指定name属性键值下的数据配置中的所有数据
	 * 
	 * @param name
	 * @return
	 */
	public List getAllDatas(String name) {
		Object o = datas.get(name);
		if (o == null) {
			return null;
		}
		if (o instanceof ArrayList) {// 内部都使用ArrayList来保存数据
			return (ArrayList) ((ArrayList) o).clone();
		}
		List l = new ArrayList(1);
		l.add(o);
		return l;
	}

	/**
	 * 如果两个数据一样，则认为是一样的
	 * 
	 * @param obj
	 *            用来比较的对象
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
	 * 有新配置项时，如果有旧配置项，则对其进行组合配置
	 * 
	 * @param name
	 * @param data
	 */
	protected void addData(String name, Object data) {
		Object o = getData(name);
		if (o == null) {
			setData(name, data);
		} else if (!pastDatas.containsKey(name)) {// 已经在废弃的数据中指明
			pastDatas.put(name, getData(name));
			setData(name, data);
		} else {
			if (o instanceof ArrayList) {
				((ArrayList) o).add(data);
			} else {
				// 如果包含一个，则以数组式存储
				ArrayList a = new ArrayList(3);
				a.add(o);
				a.add(data);
				setData(name, data);
			}
		}
	}

	/**
	 * 用新的数据替换掉旧的数据
	 * 
	 * @param name
	 * @param data
	 */
	public void setData(String name, Object data) {
		if (name != null) { // 共享实例
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
