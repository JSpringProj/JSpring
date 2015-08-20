package com.jspring.repositry.impl;

import java.util.HashMap;
import java.util.Map;

public class TransactionHolder {
	
	public static final int CONNECTION = 1;

	private double mainTransactionId;
	
	private final Map<Integer, Object> map;

	public TransactionHolder() {
		super();
		this.mainTransactionId = randomIdGenerator();
		map = new HashMap<Integer, Object>();
	}

	public double getMainTransactionId() {
		return mainTransactionId;
	}
	
	private double randomIdGenerator() {
		return 1000l * Math.random();
	}
	
	Object getValue(int key){
		return map.get(key);
	}
	
	/*void setValue(int key, Object value){
		map.p
	}*/
}
