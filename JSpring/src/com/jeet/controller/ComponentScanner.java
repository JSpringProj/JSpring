package com.jeet.controller;

import java.util.ArrayList;
import java.util.List;

import com.jeet.annotations.Component;
import com.jeet.util.AnnotationUtil;

public class ComponentScanner {

	private String packageName;
	public ComponentScanner(String packageName){
		this.packageName = packageName;
	}
	public List<Class> getAllComponents() {
		List<Class> list = new ArrayList<Class>();
		List<Class> classes = null;
		try {
			classes = new PackageScanner().scanClassesFromPackage(packageName);
		} catch (ClassNotFoundException e) {
		}
		for (Class c : classes) {
			if (AnnotationUtil.containAnnotation(c, Component.class)) {
				list.add(c);
			}
		}
		return list;
	}

	/*private Class[] getAllComponentArray() {

		return new Class[] {

		Test1.class, Test.class, Test3.class

		};
	}*/
}
