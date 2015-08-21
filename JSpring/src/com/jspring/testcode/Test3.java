package com.jspring.testcode;

import com.jspring.annotations.Component;


@Component(name="t3")
public class Test3 implements ITest {

	@Override
	public String t1() {
		System.out.println("PRINT FROM:  Test3.t1()");
		return "T####3";
	}

	@Override
	public void t2() {
		System.out.println("PRINT FROM:  Test3.t2()");

	}

	@Override
	public String t3() {
		// TODO Auto-generated method stub
		System.out.println("PRINT FROM:  Test3.t3()");
		return null;
	}

}
