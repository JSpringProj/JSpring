package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jspring.annotations.Cachable;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.ReflectionUtil;

public class CachingIncocationHandler implements InvocationHandler {

	private Object actualObj;
	
	private Object proxyObj;

	private Map<String, Object> map;

	public CachingIncocationHandler(Object actualObj, Object proxyObj) {
		this.actualObj = actualObj;
		this.proxyObj = proxyObj;
		map = new HashMap<String, Object>();
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		String key = getKey(method);
		Object retVal = map.get(key);

		Method m2 = ReflectionUtil.getActualMethod(actualObj.getClass(), method);
		if (AnnotationUtil.containAnnotation(m2, Cachable.class)) {
			if (retVal == null) {
				retVal = method.invoke(proxyObj, args);
				map.put(key, retVal);
				System.out.println("FRESH : Key: " + getKey(method)
						+ "   Val: " + retVal);
			} else {
				System.out.println("CACHED : Key: " + getKey(method)
						+ "   Val: " + retVal);
			}
		}else {
			System.out.println("NEVER CACHED :"+getKey(method));
			retVal = method.invoke(proxyObj, args);
		}

		return retVal;
	}

	private String getKey(Method method) {
		Class[] classes = method.getParameterTypes();
		StringBuilder paramclasses = new StringBuilder("");
		for (Class c : classes) {
			paramclasses.append(c.getName());
		}
		return method.getReturnType().getName() + method.getName()
				+ paramclasses.toString();
	}

}
