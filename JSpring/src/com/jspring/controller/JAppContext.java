package com.jspring.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Bean;
import com.jspring.annotations.Component;
import com.jspring.annotations.Configuration;
import com.jspring.annotations.PostConstruct;
import com.jspring.util.AnnotationUtil;

public class JAppContext {

	private List<Class> componentClasses;
	
	private List<Class> configClasses;

	private Map<String, ObjectWrapper> beans;


	JAppContext(List<Class> componentClasses, List<Class> configClasses) {
		this.componentClasses = componentClasses;
		this.configClasses = configClasses;
		beans = new HashMap<String, ObjectWrapper>();
		createObjects();
		new ConfigBeanCreater().createBean();
		new AutowireInjector().injectAutowire();
		new PostConstructCaller();
	}

	public Object getBean(String beanId) {
		ObjectWrapper objWrapper = beans.get(beanId);
		
		return objWrapper == null ? objWrapper : objWrapper.wrappedObject;
	}


	private void createObjects() {
		for (Class c : componentClasses) {
			createObjectAndInsertInMap(c);
		}
	}

	private void createObjectAndInsertInMap(Class c) {
		ObjectWrapper objWrapper = null;
		try {
				Object actualObj = Class.forName(c.getName()).newInstance();
				Object wrappedObj = ProxyCreator.getProxy(actualObj);
				objWrapper = new ObjectWrapper(actualObj, wrappedObj);
		} catch (Exception e) {
			System.out.println("JAppContext.createObjects() EXCEPTION");
		}
		insertInMap(c, objWrapper);
	}
	
	private void insertInMap(Class c, ObjectWrapper objWrapper) {
		String beanName = "";
		 Component component = (Component)c.getAnnotation(Component.class);
		 String name = component == null ? "" : component.name().trim();
		 System.out.println("JAppContext.insertInMap() name=["+name+"]");
		 if( !name.isEmpty()){
			 beanName = name;
		 }else if (c.getInterfaces().length > 0) {
			 beanName = c.getInterfaces()[0].getSimpleName();
		 }else {
			 beanName = c.getSimpleName();
		 }
		System.out.println("JAppContext.insertInMap() beanName :::: "+beanName);
		addToMap(beanName, objWrapper);
	}
	
	private void addToMap(String beanName, ObjectWrapper objWrapper){
		if( beans.containsKey(beanName)){
			throw new RuntimeException("Duplicate bean name can not be added - beanname : "+beanName);
		}
		beans.put(beanName, objWrapper);
	}

	private class PostConstructCaller {
		PostConstructCaller() {
			for (Class c : componentClasses) {
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
			Collection<ObjectWrapper> collection = beans.values();
			for (ObjectWrapper objWrapper : collection) {
				if (AnnotationUtil.containAllAnnotation(objWrapper.actualObject.getClass(), Autowired.class)) {
					injectFields(objWrapper);
					injectInMethod(objWrapper);
				}
			}
		}

		private void injectFields(ObjectWrapper objWrapper) {
			Class c = objWrapper.actualObject.getClass();
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				if (AnnotationUtil.containAnnotation(f, Autowired.class)) {
					String injectedAnnotationType = ((Autowired) f
							.getAnnotation(Autowired.class)).name();
					String injectedType = f.getType().getSimpleName();
					Object injectedObj = getBean(injectedAnnotationType);
					injectedObj = (injectedObj == null) ? getBean(injectedType)
							: injectedObj;
					System.out
							.println("JAppContext.AutowireInjector.injectFields()"+injectedType+"    ");
					try {
						f.setAccessible(true);
						f.set(objWrapper.actualObject, injectedObj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void injectInMethod(ObjectWrapper objWrapper) {
			Class c = objWrapper.actualObject.getClass();
			Method[] methods = c.getDeclaredMethods();
			for (Method m : methods) {
				if (AnnotationUtil.containAnnotation(m, Autowired.class)) {
					String injectedAnnotationType = ((Autowired) m
							.getAnnotation(Autowired.class)).name();
					String injectedType = m.getParameterTypes()[0]
							.getSimpleName();
					Object injectedObj = getBean(injectedAnnotationType);
					injectedObj = (injectedObj == null) ? getBean(injectedType)
							: injectedObj;
					try {
						m.invoke(objWrapper.actualObject, injectedObj);
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
			for (Class c : configClasses) {
				if (AnnotationUtil.containAnnotation(c, Configuration.class)) {
					Method[] methods = c.getDeclaredMethods();
					for (Method m : methods) {
						if (AnnotationUtil.containAnnotation(m, Bean.class)) {
							System.out
									.println("JAppContext.ConfigBeanCreater.createBean() c.getSimpleName() ;"+c.getSimpleName());
							try {
								System.out
										.println("JAppContext.ConfigBeanCreater.createBean() getBean(c.getSimpleName() ::"+getBean(c.getSimpleName() ));
								Object obj = m.invoke(
										getBean(c.getSimpleName()),
										m.getParameters());
								System.out
										.println("JAppContext.ConfigBeanCreater.createBean() obj=="+obj);
								
								String beanName = "";
								 Bean bean = (Bean)m.getAnnotation(Bean.class);
								 System.out
										.println("JAppContext.ConfigBeanCreater.createBean() bean: "+bean);
								 String name = bean.name().trim();
								 if( !name.isEmpty()){
									 beanName = name;
								 }else{
									 beanName = m.getReturnType().getSimpleName();
								 }
								 addToMap(beanName, new ObjectWrapper(obj, obj));
								System.out
								.println(" obj--------------==========="+obj+"   beanName :  "+beanName);
							} catch (Exception e) {
								System.out
										.println("JAppContext.ConfigBeanCreater.createBean() --------"+e.getMessage());
							}
						}
					}
				}
			}
		}

	}
	
	private class ObjectWrapper {
		private Object actualObject;
		private Object wrappedObject;
		public ObjectWrapper(Object actualObject, Object wrappedObject) {
			super();
			this.actualObject = actualObject;
			this.wrappedObject = wrappedObject;
		}
		
	}

}
