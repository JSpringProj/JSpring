package com.jspring.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Bean;
import com.jspring.annotations.Configuration;
import com.jspring.annotations.PostConstruct;
import com.jspring.util.AnnotationUtil;

public class JAppContext {

	private List<Class> compClasses;

	private Map<String, Object> beans;

	private Map<String, Object> tempIntfaceBeans;

	public JAppContext(List<Class> compClasses) {
		this.compClasses = compClasses;
		beans = new HashMap<String, Object>();
		tempIntfaceBeans = new HashMap<String, Object>();
		createObjects();
		new ConfigBeanCreater().createBean();
		new AutowireInjector().injectAutowire();
		new PostConstructCaller();
	}

	public Object getBean(String beanId) {
		return beans.get(beanId);
	}

	private Object getBeanFromTemp(String beanId) {
		return tempIntfaceBeans.remove(beanId);
	}

	private void createObjects() {
		for (Class c : compClasses) {
			createObjectAndInsertInMap(c);
		}
	}

	private void createObjectAndInsertInMap(Class c) {
		try {
			Object obj = ProxyCreator.getProxy(c);
			if (obj == null) {
				obj = Class.forName(c.getName()).newInstance();
				System.out.println("No proxy created for "
						+ obj.getClass().getName());
			}
			insertInMap(c, obj);
		} catch (Exception e) {
			System.out.println("JAppContext.createObjects() EXCEPTION");
		}
	}
	
	private void insertInMap(Class c, Object obj) {
		String className = c.getSimpleName();
		String interfaceName = null;
		if (c.getInterfaces().length > 0) {
			interfaceName = c.getInterfaces()[0].getSimpleName();
		}
		if (interfaceName != null) {
			tempIntfaceBeans.put(interfaceName, obj);
		}
		System.out.println("JAppContext.insertInMap() className :::: "+className+"    "+interfaceName);
		beans.put(className, obj);
	}

	private class PostConstructCaller {

		PostConstructCaller() {
			for (Class c : compClasses) {
				if (AnnotationUtil.containAllAnnotation(c, PostConstruct.class)) {
					Method[] methods = c.getDeclaredMethods();
					for (Method m : methods) {
						if (AnnotationUtil.containAnnotation(m,
								PostConstruct.class)) {
							try {
								m.invoke(getBean(c.getSimpleName()),
										m.getParameters());
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
	}

	private class AutowireInjector {
		private void injectAutowire() {
			for (Class c : compClasses) {
				if (AnnotationUtil.containAllAnnotation(c, Autowired.class)) {
					injectFields(c);
					injectInMethod(c);
				}
			}
		}

		private void injectFields(Class c) {
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				if (AnnotationUtil.containAnnotation(f, Autowired.class)) {
					String injectedAnnotationType = ((Autowired) f
							.getAnnotation(Autowired.class)).name();
					String injectedType = f.getType().getSimpleName();
					String invokedType = c.getSimpleName();
					Object invokedObject = getBean(invokedType);
					Object injectedObj = getBean(injectedAnnotationType);
					injectedObj = (injectedObj == null) ? getBeanFromTemp(injectedType)
							: injectedObj;
					try {
						f.setAccessible(true);
						f.set(invokedObject, injectedObj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void injectInMethod(Class c) {

			Method[] methods = c.getDeclaredMethods();
			for (Method m : methods) {
				if (AnnotationUtil.containAnnotation(m, Autowired.class)) {
					String injectedAnnotationType = ((Autowired) m
							.getAnnotation(Autowired.class)).name();
					String injectedType = m.getParameterTypes()[0]
							.getSimpleName();
					String invokedType = c.getSimpleName();
					Object invokedObject = getBean(invokedType);
					Object injectedObj = getBean(injectedAnnotationType);
					injectedObj = (injectedObj == null) ? getBeanFromTemp(injectedType)
							: injectedObj;
					try {
						m.invoke(invokedObject, injectedObj);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	private class ConfigBeanCreater {
		private void createBean() {
			for (Class c : compClasses) {
				if (AnnotationUtil.containAnnotation(c, Configuration.class)) {
					Method[] methods = c.getDeclaredMethods();
					for (Method m : methods) {
						if (AnnotationUtil.containAnnotation(m, Bean.class)) {
							try {
								Object obj = m.invoke(
										getBean(c.getSimpleName()),
										m.getParameters());
								String interfaceName = m.getReturnType().getSimpleName();
								tempIntfaceBeans.put(interfaceName, obj);
								String id = m.getName();
								beans.put(id, obj);
								System.out
								.println(" obj--------------==========="+obj+"   iName: "+m.getReturnType().getSimpleName()+"   "+m.getName()+ "id : "+id);
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}

	}

}
