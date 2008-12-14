package com.pyramidframework.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * �ṩ�ൽ�ӿڵĴ���proxy����
 * 
 * @author Mikab Peng
 * 
 */
public class ProxyHelper {

	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ������ɡ��������Ŀ��ӿڣ���ֱ�ӷ���
	 * 
	 * @param targetInterface
	 *            Ŀ��ӿ�
	 * @param source
	 *            Դ����֧��{@see ProxyAware}�ӿ�
	 * @return
	 */
	public static Object translateObject(Class targetInterface, Object source) {
		return translateObject(targetInterface, source, null);
	}

	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ������ɡ��������Ŀ��ӿڣ���ֱ�ӷ���
	 * 
	 * @param targetInterface
	 *            Ŀ��ӿ�����
	 * @param source
	 *            Դ����֧��{@see ProxyAware}�ӿ�
	 * @return
	 */
	public static Object translateObject(String targetInterface, Object source) {
		Class interf = null;
		try {
			interf = Class.forName(targetInterface);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return translateObject(interf, source, null);
	}

	/**
	 * ��source����ת��ΪtargetInterfaceָ���Ľӿ����͡�source����Ҫʵ��targetInterface��ֻ������ͬ�ķ�������,
	 * ������methodMatches��ָ�������Ķ�Ӧ��ϵ���������Ŀ��ӿڣ���ֱ�ӷ��ء�
	 * 
	 * @param targetInterface
	 *            Ŀ��ӿ�
	 * @param source
	 *            Դ���� ��֧��{@see ProxyAware}�ӿ�
	 * @param methodMatches
	 *            ������ӳ���ϵ
	 * @return
	 */
	public static Object translateObject(Class targetInterface, Object source, Map methodMatches) {
		// ����������࣬��ֱ�ӷ���
		if (targetInterface.isAssignableFrom(source.getClass())) {
			return source;
		}

		DefaultProxyHandler handler = new DefaultProxyHandler(source, methodMatches);

		Object result = translateObject(targetInterface, handler);
		// ֪ͨ������
		if (source instanceof ProxyAware) {
			((ProxyAware) source).setProxyInsatnce(result, handler);
		}

		return result;

	}

	/**
	 * �õ�������󣬶�Proxy.newProxyInstance�������з�װ���ѡ� ����source��������ǲ���targetInterface������
	 * 
	 * @param targetInterface
	 * @param handler
	 * @return
	 */
	public static Object translateObject(Class targetInterface, InvocationHandler handler) {
		Object result = Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[] { targetInterface }, handler);

		return result;
	}

	/**
	 * �õ�������󣬶�Proxy.newProxyInstance�������з�װ���ѡ����source֧��ProxyAware�ӿڣ��򱻵���.
	 * ����source��������ǲ���targetInterface������
	 * 
	 * @param targetInterface
	 * @param handler
	 * @return
	 */
	public static Object translateObject(Class targetInterface, InvocationHandler handler, Object source) {
		Object result = translateObject(targetInterface, handler);

		// ֪ͨ������
		if (source instanceof ProxyAware) {
			((ProxyAware) source).setProxyInsatnce(result, handler);
		}

		return result;
	}

	/**
	 * ����ĳ�������ǲ����ܱ�ת����ĳ������
	 * 
	 * @param targetInterface
	 *            �����ǽӿ�
	 * @param source
	 * @return
	 */
	public static boolean canTranslate(Class targetInterface, Object source) {
		if (targetInterface.isAssignableFrom(source.getClass())) {// �����������ֱ�ӷ���
			return true;
		}

		// ����Ƚ�
		if (targetInterface.isInterface()) {
			Method[] methods = targetInterface.getDeclaredMethods();
			try {
				for (int i = 0; i < methods.length; i++) {
					if (source.getClass().getDeclaredMethod(methods[i].getName(), methods[i].getParameterTypes()) == null) {
						return false;
					}
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;

	}

	/**
	 * ��bean��class�����еĹ����ķ����ϲ���parent��class�У�������parent classΪ���ࡣ
	 * ͬʱ�����ɵ�class������һ��FIELD���ڴ��bean�ĵ�ʵ��.<br>
	 * ע�⣺ֻ����public������������FIELD,ͬ�������������β�����ᷢ���쳣��Ӧ���ⲿ���ü��
	 * 
	 * @param parent
	 * @param bean
	 * @return
	 */
	public synchronized static Class mergeBeanClass(Class parent, Class bean) {

		ClazzCacheKey key = new ClazzCacheKey(parent, bean);
		Class result = (Class) generatedClazzCache.get(key);

		if (result == null) {
			try {
				ClassPool pool = new ClassPool();
				pool.insertClassPath(new ClassClassPath(parent));
				pool.insertClassPath(new ClassClassPath(bean));

				CtClass clazz = pool.makeClass(parent.getName() + generatedClassPostfix);

				clazz.setSuperclass(pool.get(parent.getName()));
				CtClass beanClazz = pool.get(bean.getName());

				CtField ctField = new CtField(beanClazz, generatedFieldName, clazz);
				ctField.setModifiers(Modifier.PUBLIC);
				clazz.addField(ctField);

				CtMethod[] methods = beanClazz.getMethods();
				CtMethod[] oldMethods = clazz.getMethods();
				for (int i = 0; i < methods.length; i++) {

					if ((methods[i].getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
						continue;
					}

					boolean hasSameMethod = false;
					for (int j = 0; j < oldMethods.length; j++) {
						if (oldMethods[j].getName().equals(methods[i].getName()) && Arrays.equals(oldMethods[j].getParameterTypes(), methods[i].getParameterTypes())) {
							hasSameMethod = true;
							break;
						}
					}
					if (!hasSameMethod) {// ���û����ͬ�ķ����������֮
						CtMethod ctMethod = new CtMethod(methods[i].getReturnType(), methods[i].getName(), methods[i].getParameterTypes(), clazz);
						StringBuilder builder = new StringBuilder("return ").append(generatedFieldName);
						builder.append(".").append(methods[i].getName()).append("(");

						for (int j = 1; j <= methods[i].getParameterTypes().length; j++) {
							if (j > 1) {
								builder.append(",");
							}
							builder.append("$").append(j);
						}
						builder.append(");");
						//System.err.println(builder.toString());

						ctMethod.setBody(builder.toString());
						ctMethod.setModifiers(methods[i].getModifiers());
						// ctMethod.setExceptionTypes(methods[i].getExceptionTypes());
						clazz.addMethod(ctMethod);
					}
				}
				result = clazz.toClass();
				generatedClazzCache.put(key, result);
				clazz.detach();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * �����ɵ�mergeBean��ʵ���з�������
	 * 
	 * @param mergedObject
	 * @param bean
	 */
	public static void setBeanObject(Object mergedObject, Object bean) {

		try {
			Field field = mergedObject.getClass().getDeclaredField(generatedFieldName);
			field.set(mergedObject, bean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �����ɵ�mergeBean��ʵ���з�������
	 * 
	 * @param mergedObject
	 * @param bean
	 */
	public static Object getBeanObject(Object mergedObject) {

		try {
			Field field = mergedObject.getClass().getDeclaredField(generatedFieldName);
			return field.get(mergedObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ����merge�������Ҹ�ֵ
	 * 
	 * @param parent
	 * @param bean
	 * @return
	 */
	public static Object newMergeInstance(Class parent, Object bean) {
		try {
			Class clazz = mergeBeanClass(parent, bean.getClass());
			Object instance = clazz.newInstance();
			setBeanObject(instance, bean);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** mergeBeanClass��������������ԭ�и���������ϼӵķ��� */
	public static final String generatedClassPostfix = "$generatedClass";
	/** mergeBeanClass�������������ž��������bean��FIELD */
	public static final String generatedFieldName = "generatedProxyBeanField";
	private static final Map generatedClazzCache = new HashMap();

	private static class ClazzCacheKey {
		Class class1 = null;
		Class class2 = null;

		public ClazzCacheKey(Class class1, Class class2) {
			this.class1 = class1;
			this.class2 = class2;
		}

		public int hashCode() {
			return class1.hashCode() + class2.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj != null) {
				if (obj.getClass().equals(this.getClass())) {
					ClazzCacheKey key = (ClazzCacheKey) obj;
					return class1.equals(key.class1) && class2.equals(class2);
				}
			}
			return false;
		}

	}

}
