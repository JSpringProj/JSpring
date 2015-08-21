package com.jspring.controller;

import java.util.ArrayList;
import java.util.List;

import com.jspring.annotations.ComponentScan;
import com.jspring.annotations.Configuration;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.Logger;

public final class JSpringApp {

	private static JAppContext appContext;

	public static JAppContext run(Class... configClasses) {
		List<Class> configCls = new ArrayList<Class>();
		String scanPackage = null;
		for (Class clz : configClasses) {
			if (!AnnotationUtil.containAnnotation(clz, Configuration.class)) {
				throw new RuntimeException("The class " + clz.getSimpleName()
						+ " is not configuration class");
			}

			boolean scanEnable = AnnotationUtil.containAnnotation(clz,
					ComponentScan.class);

			if (scanEnable && scanPackage != null) {
				throw new RuntimeException("The class " + clz.getSimpleName()
						+ " has duplicate scan package");
			}

			if (scanEnable) {
				scanPackage = ((ComponentScan) clz
						.getAnnotation(ComponentScan.class)).packageName();
			}
			configCls.add(clz);
		}

		ComponentScanner scanner = new ComponentScanner("com.jspring");
		List<Class> componeteClasses = scanner.getAllComponents();

		List<Class> clientComponeteClasses = new ArrayList<Class>();
		if (scanPackage != null) {
			scanner = new ComponentScanner(scanPackage);
			clientComponeteClasses = scanner.getAllComponents();
		}

		componeteClasses.addAll(clientComponeteClasses);
		Logger.log(JSpringApp.class, "run", "FILES SCANNED : "+ componeteClasses.size());
		appContext = new JAppContext(componeteClasses, configCls);
		return appContext;
	}

	public static JAppContext getAppContext() {
		return appContext;
	}
}
