package com.jspring.repository.intf;

import java.sql.Connection;

public interface TransactionalRepositry {

	
	public double startTransaction();
	
	public boolean commit(double transactionId);
	
	public boolean rollback(double transactionId);
	
	public Connection getConnection();
}
