package com.jspring.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationUtil {

	public static List<Annotation> getAnnotations(Object target) {
		return getAllAnnotation(target.getClass());
	}

	public static List<Annotation> getAllAnnotation(Class target) {
		List<Annotation> classAnnotations = getAnnotations(target);
		List<Annotation> filedAnnotations = getAnnotations(target
				.getDeclaredFields());
		List<Annotation> methodAnnotations = getAnnotations(target
				.getDeclaredMethods());
		List<Annotation> allAnnotations = new ArrayList<Annotation>();
		allAnnotations.addAll(classAnnotations);
		allAnnotations.addAll(methodAnnotations);
		allAnnotations.addAll(filedAnnotations);
		return removeDuplicates(allAnnotations);
	}

	public static List<Annotation> getAnnotations(Class target) {
		return getlist(target.getAnnotations());
	}

	public static List<Annotation> getAnnotations(Method[] target) {
		List<Annotation> methodAnnotations = new ArrayList<Annotation>();
		for (Method method : target) {
			methodAnnotations.addAll(getAnnotations(method));
		}
		return removeDuplicates(methodAnnotations);
	}

	public static List<Annotation> getAnnotations(Method target) {
		return getlist(target.getAnnotations());
	}

	public static List<Annotation> getAnnotations(Field[] target) {
		List<Annotation> fieldAnnotations = new ArrayList<Annotation>();
		for (Field field : target) {
			fieldAnnotations.addAll(getAnnotations(field));
		}
		return removeDuplicates(fieldAnnotations);
	}

	public static List<Annotation> getAnnotations(Field target) {
		return getlist(target.getAnnotations());
	}

	private static List<Annotation> getlist(Annotation[] ann) {
		List<Annotation> list = Arrays.asList(ann);
		return removeDuplicates(list);
	}

	private static List<Annotation> removeDuplicates(List<Annotation> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(i).annotationType().getName() == list.get(j)
						.annotationType().getName()) {
					list.remove(i);
				}
			}
		}
		return list;
	}

	public static boolean containAnnotation(Object target, Class annotationClass) {
		boolean contains = false;
		List<Annotation> anList = getAnnotations(target);
		for (Annotation an : anList) {
			if (an.annotationType().getName().equals(annotationClass.getName())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public static boolean containAnnotation(Method target, Class annotationClass) {
		boolean contains = false;
		List<Annotation> anList = getAnnotations(target);
		for (Annotation an : anList) {
			if (an.annotationType().getName().equals(annotationClass.getName())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public static boolean containAnnotation(Field target, Class annotationClass) {
		boolean contains = false;
		List<Annotation> anList = getAnnotations(target);
		for (Annotation an : anList) {
			if (an.annotationType().getName().equals(annotationClass.getName())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public static boolean containAnnotation(Class target, Class annotationClass) {
		boolean contains = false;
		List<Annotation> anList = getAnnotations(target);
		for (Annotation an : anList) {
			if (an.annotationType().getName().equals(annotationClass.getName())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public static boolean containAllAnnotation(Class target,
			Class annotationClass) {
		boolean contains = false;
		List<Annotation> anList = getAllAnnotation(target);
		for (Annotation an : anList) {
			if (an.annotationType().getName().equals(annotationClass.getName())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public static boolean containAllAnnotation(Class target,
			Class[] annotationClasses) {
		boolean contains = false;
		for (Class clz : annotationClasses) {
			contains = contains || containAllAnnotation(target, clz);
		}
		return contains;
	}
}
