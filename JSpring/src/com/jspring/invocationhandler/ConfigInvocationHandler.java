package com.jspring.invocationhandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jspring.util.Logger;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ConfigInvocationHandler implements MethodInterceptor {

	private ThreadLocal<Map<String, Object>> tLocal;

	public ConfigInvocationHandler() {
		super();
		tLocal = new ThreadLocal<Map<String, Object>>();
	}

	public Object intercept(Object proxy, Method method, Object[] args,
			MethodProxy proxyMethod) throws Throwable {

		String methodName = method.getName();
		Object obj = getObject(methodName);
		Logger.log(this, "intercept", methodName +" is GOT FROM ****Thread Local**** Cache  : "+obj);
		if( obj == null){
			obj = proxyMethod.invokeSuper(proxy, args);
			setObject(methodName, obj);
			Logger.log(this, "intercept", methodName +" Newely created object : "+obj);
		}
		
		return obj;
	}

	private Object getObject(String mName) {
		Map<String, Object> map = getMap();
		return map.get(mName);
	}

	private Map<String, Object> getMap() {
		Map<String, Object> map = tLocal.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			tLocal.set(map);
		}
		return map;
	}

	private void setObject(String mName, Object obj) {
		Map<String, Object> map = getMap();
		map.put(mName, obj);
	}

}
