package com.jspring.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionHolder {

	public static final int ACTUAL_CONNECTION = 1;
	
	public static final int PROXY_CONNECTION = 2;

	private double mainTransactionId = 0.0;

	private List<Double> transactionIds;

	private final Map<Integer, Object> map;

	public TransactionHolder() {
		super();
		map = new HashMap<Integer, Object>();
		transactionIds = new ArrayList<Double>();
	}

	void init(){
		this.mainTransactionId = getNextTransactionId();
	}
	double getNextTransactionId() {
		double transationId = 0;
		do {
			transationId = randomIdGenerator();
		} while (transactionIds.contains(transationId));
		transactionIds.add(transationId);
		return transationId;
	}

	boolean removeTranstaionId(double transactionId) {
		return transactionIds.remove(transactionId);
	}

	double getMainTransactionId() {
		return mainTransactionId;
	}

	Object getValue(int key) {
		return map.get(key);
	}

	void setValue(int key, Object value) {
		map.put(key, value);
	}

	void dispose(){
		mainTransactionId = -1;
		transactionIds.clear();
		map.clear();
	}
	private double randomIdGenerator() {
		return 1000l * Math.random();
	}
}
