package com.jspring.repositry.impl;

import javax.sql.DataSource;

import com.jeet.annotations.Autowired;
import com.jspring.repositry.TransactionalRepositry;

public class TransactionalRepositryImpl implements TransactionalRepositry {
	
	static final ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	@Autowired
	private DataSource dataSource;

	public TransactionalRepositryImpl(){
		
	}
	
	@Override
	public long startTransaction() {
	//	Thread.currentThread().
		
		return 0;
	}

	@Override
	public boolean commit(long transactionId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rollback(long transactionId) {
		// TODO Auto-generated method stub
		return false;
	}

}
