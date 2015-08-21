package com.jspring.repository.impl;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Component;
import com.jspring.annotations.PostConstruct;
import com.jspring.invocationhandler.ConnectionInvocationHandler;
import com.jspring.repository.intf.TransactionalRepositry;

@Component
public class TransactionalRepositryImpl implements TransactionalRepositry {

	private final ThreadLocal<TransactionHolder> threadLocal;
	@Autowired
	private DataSource dataSource;

	private Connection connection;

	public TransactionalRepositryImpl() {
		threadLocal = new ThreadLocal<TransactionHolder>();
	}
	
	@PostConstruct
	public void testPostCon(){
		System.out.println("TransactionalRepositryImpl.testPostCon() --------------------");
		try {
			System.out
					.println("TransactionalRepositryImpl.TransactionalRepositryImpl()------------dataSource: "+dataSource);
			Connection con = dataSource.getConnection();
			System.out
					.println("TransactionalRepositryImpl.TransactionalRepositryImpl() con:  "+con);
			connection = (Connection) Proxy
					.newProxyInstance(
							con.getClass().getClassLoader(),
							con.getClass().getInterfaces(),
							new ConnectionInvocationHandler(dataSource
									.getConnection()));

		} catch (Exception e) {
			System.out
					.println("TransactionalRepositryImpl.TransactionalRepositryImpl() EXP");
		}
	}

	@Override
	public double startTransaction() {
		double transactionId = 0;
		TransactionHolder holder = threadLocal.get();
		System.out.println("TransactionalRepositryImpl.startTransaction()"
				+ holder);
		if (holder == null) {
			holder = getHolder();
			transactionId = holder.getMainTransactionId();
			System.out
					.println("TransactionalRepositryImpl.startTransaction() dataSource : "
							+ dataSource);
			/*
			 * try { dataSource.getConnection().setAutoCommit(false); } catch
			 * (SQLException e) { }
			 */
		} else {
			transactionId = holder.getNextTransactionId();
		}
		return transactionId;
	}

	@Override
	public boolean commit(double transactionId) {
		TransactionHolder holder = getHolder();
		System.out.println("TransactionalRepositryImpl.commit() holder="
				+ holder);
		double mainTId = holder.getMainTransactionId();
		System.out.println("TransactionalRepositryImpl.commit() mainTId="
				+ mainTId);
		if (mainTId == transactionId) {
			// try {
			// dataSource.getConnection().commit();
			disposeHolder();
			// } catch (SQLException e) {
			// }
		} else {
			return holder.removeTranstaionId(transactionId);
		}
		return true;
	}

	@Override
	public boolean rollback(double transactionId) {
		TransactionHolder holder = getHolder();
		double mainTId = holder.getMainTransactionId();
		if (mainTId == transactionId) {
			try {
				dataSource.getConnection().rollback();
				disposeHolder();
			} catch (SQLException e) {
			}
		} else {
			return holder.removeTranstaionId(transactionId);
		}
		return true;
	}

	@Override
	public Connection getConnection() {
		TransactionHolder holder = getHolder();
		Connection con = (Connection) holder
				.getValue(TransactionHolder.CONNECTION);
		if (con == null) {
			holder.setValue(TransactionHolder.CONNECTION, connection);
			con = connection;
		}
		System.out.println("TransactionalRepositryImpl.getConnection()" + con);
		return con;
	}

	private TransactionHolder getHolder() {
		TransactionHolder holder = threadLocal.get();
		if (holder == null) {
			holder = new TransactionHolder();
			threadLocal.set(holder);
		}
		return holder;
	}

	private void disposeHolder() {
		TransactionHolder holder = getHolder();
		holder.dispose();
		holder = null;
		threadLocal.set(null);
	}

}
