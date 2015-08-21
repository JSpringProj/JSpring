package com.jspring.testcode;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Component;

@Component
public class Test {

	ITest t1;
	@Autowired//(name="Test1")
	public void add(ITest t1 ){
		this.t1 = t1;
	}
	
	public void print(){
		System.out.println("Test.enclosing_method()");
	}
}
