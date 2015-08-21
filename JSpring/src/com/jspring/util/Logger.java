package com.jspring.util;

public class Logger {

	private static final boolean LOG_ON = true;

	public static void log(Object obj, String methodName, String message) {

		if (LOG_ON) {
			String clzName = (obj == null) ? "" : obj.getClass()
					.getSimpleName();
			log(clzName, methodName, message);
		}

	}

	public static void log(String clzName, String methodName, String message) {

		if (LOG_ON) {
			String formattedClzName = "[ClassName]- " + clzName + ", ";
			System.out.println("JLog: " + formattedClzName + " [MethodName]- "
					+ methodName + ",  [Msg]- " + message);
		}

	}

}
