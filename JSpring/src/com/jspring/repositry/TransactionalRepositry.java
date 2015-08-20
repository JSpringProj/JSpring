package com.jspring.repositry;

public interface TransactionalRepositry {

	
	public long startTransaction();
	
	public boolean commit(long transactionId);
	
	public boolean rollback(long transactionId);
}
