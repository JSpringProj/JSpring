package com.jspring.testcode;

import com.jspring.annotations.Component;
import com.jspring.annotations.Transational;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;


@Component
public class Test1 implements ITest {

	public void t1(){
		System.out.println("PRINT FROM:  Test1.t1()");
	}
	@Transational
	public void t2(){
		System.out.println("PRINT FROM:  Test1.t2()");
	}

	@Transational
	public String t3() {
		t2();
		//context.getBean("")
		System.out.println("PRINT FROM:  Test1.t3() HELLO");
		return "Hello";
	}
}
