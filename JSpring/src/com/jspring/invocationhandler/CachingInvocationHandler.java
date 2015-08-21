package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jspring.annotations.Cacheable;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;
import com.jspring.repository.intf.CacheRepository;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.ReflectionUtil;

public class CachingInvocationHandler implements InvocationHandler {

	private final Object actualObj;
	private final Object proxyObj;

	public CachingInvocationHandler(Object actualObj, Object proxyObj) {
		this.actualObj = actualObj;
		this.proxyObj = proxyObj;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Object retVal;

		Method m2 = ReflectionUtil
				.getActualMethod(actualObj.getClass(), method);
		if (AnnotationUtil.containAnnotation(m2, Cacheable.class)) {
			String cacheRegion = m2.getAnnotation(Cacheable.class)
					.cacheRegion();
			JAppContext appCon = JSpringApp.getAppContext();
			CacheRepository repository = (CacheRepository) appCon
					.getBean("CacheRepositoryImpl");
			String key = getkey(method, args);
			retVal = repository.getCacheValue(cacheRegion, key);

			if (retVal == null) {
				retVal = method.invoke(proxyObj, args);
				repository.addCacheValue(cacheRegion, key, retVal);
				System.out.println("FRESH : Key: " + key + "   Val: " + retVal);
			} else {
				System.out
						.println("CACHED : Key: " + key + "   Val: " + retVal);
			}
		} else {
			retVal = method.invoke(proxyObj, args);
		}

		return retVal;
	}

	private String getkey(Method method, Object[] args) {
		Class returnType = method.getReturnType();
		int modifier = method.getModifiers();

		StringBuffer key = new StringBuffer();
		key.append(modifier).append(returnType.getName())
				.append(method.getName()).append("(");
		int size = args == null ? 0 : args.length;
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				key.append(",");
			}
			key.append(constructArgumentString(args[i]));
		}
		key.append(")");
		System.out.println("Key : " + key);
		return key.toString();
	}

	private String constructArgumentString(Object arg) {
		String argString;
		if (arg instanceof String) {
			argString = (String) arg;
		} else if (arg instanceof Integer || arg instanceof Boolean
				|| arg instanceof Byte || arg instanceof Long) {
			argString = String.valueOf(arg);
		} else {
			argString = arg.getClass().getName();
		}
		return argString;
	}
}
