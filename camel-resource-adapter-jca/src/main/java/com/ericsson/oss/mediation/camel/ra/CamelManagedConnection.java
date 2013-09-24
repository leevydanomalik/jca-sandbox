/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ericsson.oss.mediation.camel.ra;

import java.io.PrintWriter;
import java.util.*;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CamelManagedConnection
 * 
 * @version $Revision: $
 */
public class CamelManagedConnection implements ManagedConnection, XAResource,
		LocalTransaction {

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(CamelManagedConnection.class);

	/** The logwriter */
	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private CamelManagedConnectionFactory mcf;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connection */
	private DataFlowImpl connection;

	private int timeout;

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            mcf
	 */
	public CamelManagedConnection(CamelManagedConnectionFactory mcf)
			throws ResourceException {
		this.mcf = mcf;
		this.logwriter = null;
		this.listeners = Collections
				.synchronizedList(new ArrayList<ConnectionEventListener>(1));
		this.connection = null;
	}

	/**
	 * Creates a new connection handle for the underlying physical connection
	 * represented by the ManagedConnection instance.
	 * 
	 * @param subject
	 *            Security context as JAAS subject
	 * @param cxRequestInfo
	 *            ConnectionRequestInfo instance
	 * @return generic Object instance representing the connection handle.
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public Object getConnection(Subject subject,
			ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		log.trace("getConnection()");
		connection = new DataFlowImpl(this, mcf);
		return connection;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * connection handle with a ManagedConneciton instance.
	 * 
	 * @param connection
	 *            Application-level connection handle
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void associateConnection(Object connection) throws ResourceException {
		log.trace("associateConnection({})", connection);

		if (connection == null)
			throw new ResourceException("Null connection handle");

		if (!(connection instanceof DataFlowImpl))
			throw new ResourceException("Wrong connection handle");

		this.connection = (DataFlowImpl) connection;
	}

	/**
	 * Application server calls this method to force any cleanup on the
	 * ManagedConnection instance.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void cleanup() throws ResourceException {
		log.trace("cleanup()");

	}

	/**
	 * Destroys the physical connection to the underlying resource manager.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		log.trace("destroy()");
		this.connection = null;

	}

	/**
	 * Adds a connection event listener to the ManagedConnection instance.
	 * 
	 * @param listener
	 *            A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		log.trace("addConnectionEventListener({})", listener);
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.add(listener);
	}

	/**
	 * Removes an already registered connection event listener from the
	 * ManagedConnection instance.
	 * 
	 * @param listener
	 *            already registered connection event listener to be removed
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		log.trace("removeConnectionEventListener({})", listener);
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/**
	 * Close handle
	 * 
	 * @param handle
	 *            The handle
	 */
	void closeHandle(DataFlow handle) {
		ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(handle);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}

	}

	/**
	 * Gets the log writer for this ManagedConnection instance.
	 * 
	 * @return Character output stream associated with this Managed-Connection
	 *         instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		log.trace("getLogWriter()");
		return logwriter;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 * 
	 * @param out
	 *            Character Output stream to be associated
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		log.trace("setLogWriter({})", out);
		logwriter = out;
	}

	/**
	 * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
	 * 
	 * @return LocalTransaction instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		log.trace("getLocalTransaction()");
		return this;
	}

	/**
	 * Returns an <code>javax.transaction.xa.XAresource</code> instance.
	 * 
	 * @return XAResource instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public XAResource getXAResource() throws ResourceException {
		log.trace("getXAResource()");
		return this;
	}

	/**
	 * Gets the metadata information for this connection's underlying EIS
	 * resource manager instance.
	 * 
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		log.trace("getMetaData()");
		return new CamelManagedConnectionMetaData();
	}

	private void fireRollbackEvent() {
		final ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}
	}

	private void fireCommitEvent() {
		final ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_COMMITTED);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}
	}

	private void fireBeginEvent() {
		final ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_STARTED);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#begin()
	 */
	@Override
	public void begin() throws ResourceException {
		fireBeginEvent();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#commit()
	 */
	@Override
	public void commit() throws ResourceException {
		fireCommitEvent();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#rollback()
	 */
	@Override
	public void rollback() throws ResourceException {
		fireRollbackEvent();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid,
	 * boolean)
	 */
	@Override
	public void commit(Xid xid, boolean onePhase) throws XAException {
		log.debug("Commit called for xid={}, and onePhase={}", xid, onePhase);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid, int)
	 */
	@Override
	public void end(Xid xid, int flags) throws XAException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
	 */
	@Override
	public void forget(Xid xid) throws XAException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
	 */
	@Override
	public int getTransactionTimeout() throws XAException {
		return this.timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
	 */
	@Override
	public boolean isSameRM(XAResource xares) throws XAException {

		return this.equals(xares);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
	 */
	@Override
	public int prepare(Xid xid) throws XAException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#recover(int)
	 */
	@Override
	public Xid[] recover(int flag) throws XAException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
	 */
	@Override
	public void rollback(Xid xid) throws XAException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
	 */
	@Override
	public boolean setTransactionTimeout(int seconds) throws XAException {
		this.timeout = seconds;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid, int)
	 */
	@Override
	public void start(Xid xid, int flags) throws XAException {

	}

}
