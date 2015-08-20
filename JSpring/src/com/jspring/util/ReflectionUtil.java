package com.jspring.util;

import java.lang.reflect.Method;

public class ReflectionUtil {

	public static Method getActualMethod(Class classs, Method proxyMethod) {

		try {
			return classs.getMethod(proxyMethod.getName(),
					proxyMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
