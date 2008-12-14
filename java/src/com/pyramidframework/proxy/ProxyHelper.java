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
 * 提供类到接口的代理、proxy服务
 * 
 * @author Mikab Peng
 * 
 */
public class ProxyHelper {

	/**
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可。如果包含目标接口，则直接返回
	 * 
	 * @param targetInterface
	 *            目标接口
	 * @param source
	 *            源对象，支持{@see ProxyAware}接口
	 * @return
	 */
	public static Object translateObject(Class targetInterface, Object source) {
		return translateObject(targetInterface, source, null);
	}

	/**
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可。如果包含目标接口，则直接返回
	 * 
	 * @param targetInterface
	 *            目标接口名称
	 * @param source
	 *            源对象，支持{@see ProxyAware}接口
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
	 * 将source对象转换为targetInterface指定的接口类型。source不需要实现targetInterface，只需有相同的方法即可,
	 * 或者在methodMatches里指明函数的对应关系。如果包含目标接口，则直接返回。
	 * 
	 * @param targetInterface
	 *            目标接口
	 * @param source
	 *            源对象 ，支持{@see ProxyAware}接口
	 * @param methodMatches
	 *            方法的映射关系
	 * @return
	 */
	public static Object translateObject(Class targetInterface, Object source, Map methodMatches) {
		// 如果是其子类，则直接返回
		if (targetInterface.isAssignableFrom(source.getClass())) {
			return source;
		}

		DefaultProxyHandler handler = new DefaultProxyHandler(source, methodMatches);

		Object result = translateObject(targetInterface, handler);
		// 通知其内容
		if (source instanceof ProxyAware) {
			((ProxyAware) source).setProxyInsatnce(result, handler);
		}

		return result;

	}

	/**
	 * 得到代理对象，对Proxy.newProxyInstance方法进行封装而已。 不对source方法检查是不是targetInterface的子类
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
	 * 得到代理对象，对Proxy.newProxyInstance方法进行封装而已。如果source支持ProxyAware接口，则被调用.
	 * 不对source方法检查是不是targetInterface的子类
	 * 
	 * @param targetInterface
	 * @param handler
	 * @return
	 */
	public static Object translateObject(Class targetInterface, InvocationHandler handler, Object source) {
		Object result = translateObject(targetInterface, handler);

		// 通知其内容
		if (source instanceof ProxyAware) {
			((ProxyAware) source).setProxyInsatnce(result, handler);
		}

		return result;
	}

	/**
	 * 决定某个对象是不是能被转换成某个对象
	 * 
	 * @param targetInterface
	 *            必须是接口
	 * @param source
	 * @return
	 */
	public static boolean canTranslate(Class targetInterface, Object source) {
		if (targetInterface.isAssignableFrom(source.getClass())) {// 如果是其子类直接返回
			return true;
		}

		// 逐个比较
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
	 * 将bean的class的所有的公开的方法合并到parent的class中，并且以parent class为父类。
	 * 同时在生成的class中新增一个FIELD用于存放bean的的实例.<br>
	 * 注意：只复制public方法而不复制FIELD,同样两个类如果多次操作则会发生异常，应在外部做好检查
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
					if (!hasSameMethod) {// 如果没有相同的方法，则添加之
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
	 * 往生成的mergeBean的实例中放置数据
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
	 * 往生成的mergeBean的实例中放置数据
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
	 * 调用merge方法并且赋值
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

	/** mergeBeanClass方法产生的类在原有父类的名字上加的方法 */
	public static final String generatedClassPostfix = "$generatedClass";
	/** mergeBeanClass方法产生的类存放具体的数据bean的FIELD */
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
