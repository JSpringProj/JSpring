package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jspring.annotations.CacheEvict;
import com.jspring.annotations.Cacheable;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;
import com.jspring.repository.intf.CacheRepository;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.Logger;
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
		Method targetMethod = ReflectionUtil.getActualMethod(
				actualObj.getClass(), method);
		JAppContext appCon = JSpringApp.getAppContext();
		CacheRepository repository = (CacheRepository) appCon
				.getBean("CacheRepository");
		Object retVal = null;

		if (AnnotationUtil.containAnnotation(targetMethod, CacheEvict.class)) {
			String cacheRegion = targetMethod.getAnnotation(CacheEvict.class)
					.cacheRegion();
			repository.evictCache(cacheRegion);
		}

		if (AnnotationUtil.containAnnotation(targetMethod, Cacheable.class)) {
			String cacheRegion = targetMethod.getAnnotation(Cacheable.class)
					.cacheRegion();
			String key = getkey(method, args);
			retVal = repository.getCacheValue(cacheRegion, key);

			if (retVal == null) {
				retVal = method.invoke(proxyObj, args);
				repository.addCacheValue(cacheRegion, key, retVal);
				Logger.log(this, "invoke", "FRESH RESULT for Key: \"" + key + "\" Val: " + retVal);
			} else {
				Logger.log(this, "invoke","CACHED RESULT for Key: \"" + key + "\" Val: " + retVal);
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
