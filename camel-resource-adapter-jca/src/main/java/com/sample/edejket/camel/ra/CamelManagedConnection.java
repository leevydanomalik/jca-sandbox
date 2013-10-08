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
package com.sample.edejket.camel.ra;

import java.io.PrintWriter;
import java.util.*;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.*;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
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

	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private CamelManagedConnectionFactory mcf;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connection */
	private DataFlowImpl flow;

	private int txTimeout;

	private CamelResourceAdapter camelRa;

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            mcf
	 */
	public CamelManagedConnection(final CamelManagedConnectionFactory mcf)
			throws ResourceException {
		this.mcf = mcf;
		this.logwriter = null;
		this.listeners = Collections
				.synchronizedList(new ArrayList<ConnectionEventListener>(1));
		this.flow = null;
		this.camelRa = (CamelResourceAdapter) mcf.getResourceAdapter();
	}

	/**
	 * Creates a new flow handle for the underlying physical flow represented by
	 * the ManagedConnection instance.
	 * 
	 * @param subject
	 *            Security context as JAAS subject
	 * @param cxRequestInfo
	 *            ConnectionRequestInfo instance
	 * @return generic Object instance representing the flow handle.
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public Object getConnection(final Subject subject,
			final ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		log.trace("getConnection()");
		flow = new DataFlowImpl(this, mcf);
		return flow;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * flow handle with a ManagedConneciton instance.
	 * 
	 * @param flow
	 *            Application-level flow handle
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void associateConnection(final Object flow) throws ResourceException {
		log.trace("associateConnection({})", flow);

		if (flow == null) {
			throw new ResourceException("Null flow handle");
		}

		if (!(flow instanceof DataFlowImpl)) {
			throw new ResourceException("Wrong flow handle");
		}

		this.flow = (DataFlowImpl) flow;
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
		this.flow = null;

	}

	/**
	 * Destroys the physical flow to the underlying resource manager.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		log.trace("destroy()");
		this.flow = null;

	}

	/**
	 * Adds a flow event listener to the ManagedConnection instance.
	 * 
	 * @param listener
	 *            A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(
			final ConnectionEventListener listener) {
		log.trace("addConnectionEventListener({})", listener);
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.add(listener);
	}

	/**
	 * Removes an already registered flow event listener from the
	 * ManagedConnection instance.
	 * 
	 * @param listener
	 *            already registered flow event listener to be removed
	 */
	public void removeConnectionEventListener(
			final ConnectionEventListener listener) {
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
	void closeHandle(final DataFlow handle) {
		final ConnectionEvent event = new ConnectionEvent(this,
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
	public void setLogWriter(final PrintWriter out) throws ResourceException {
		log.trace("setLogWriter({})", out);
		logwriter = out;
	}

	/**
	 * Gets the metadata information for this flow's underlying EIS resource
	 * manager instance.
	 * 
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		log.trace("getMetaData()");
		return new CamelManagedConnectionMetaData();
	}

	/**
	 * @return the flow
	 */
	public DataFlowImpl getFlow() {
		return flow;
	}

	/**
	 * Process input
	 * 
	 * @param testInput
	 * @throws ResourceException
	 */
	public void processInput(final String testInput) throws ResourceException {
		log.trace("processInput called with input {}", testInput);
		final String routeName = "loadCustomComponentRoute";

		if ("LOAD CUSTOM COMPONENT".equalsIgnoreCase(testInput)) {
			final CamelContext camelCtx = this.camelRa.getCamelContext();
			final RouteBuilder routeBuilder = new RouteBuilder() {

				@Override
				public void configure() throws Exception {

					from("direct:customComponentRoute").to(
							"customComp://someCustomComponent")
							.setId(routeName);

				}
			};

			try {
				camelCtx.addRoutes(routeBuilder);

				final Route route = camelCtx.getRoute(routeName);
				final Endpoint endpoint = route.getEndpoint();
				final Producer producer = endpoint.createProducer();
				final Exchange ex = endpoint.createExchange();
				ex.getIn().setBody("CUSTOM CAMEL COMPONENT TEST");
				producer.process(ex);
				log.trace("processInput method invocation ended...");

			} catch (Exception e) {
				log.error("Error adding route into camel context:", e);
				throw new ResourceException(e);
			}
		} else {
			log.trace("processInput method invocation ended for for other testcases");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.ManagedConnection#getXAResource()
	 */
	public XAResource getXAResource() throws ResourceException {
		log.trace("getXAResource called, returning {}", (XAResource) this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid,
	 * boolean)
	 */
	public void commit(final Xid xid, final boolean onePhase)
			throws XAException {
		log.trace("commit called with xid=[{}] and onePhase=[{}]",
				new Object[] { xid, onePhase });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid, int)
	 */
	public void end(final Xid xid, final int flags) throws XAException {
		log.trace("End called with xid=[{}] and flag[{}]", new Object[] { xid,
				flags });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
	 */
	public void forget(final Xid xid) throws XAException {
		log.trace("Forget called with xid=[{}]", xid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
	 */
	public int getTransactionTimeout() throws XAException {
		log.trace("getTransactionTimeout called and returning {}",
				this.txTimeout);
		return this.txTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
	 */
	public boolean isSameRM(final XAResource xares) throws XAException {
		final boolean result = this.equals(xares);
		log.trace("isSameRM called with xares=[{}] and is returning {}",
				new Object[] { xares, result });
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
	 */
	public int prepare(final Xid xid) throws XAException {
		log.trace("prepare called with xid=[{}]", xid);
		return XA_OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#recover(int)
	 */
	public Xid[] recover(final int flag) throws XAException {
		log.trace("recover called with flag=[{}] and returning null", flag);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
	 */
	public void rollback(final Xid xid) throws XAException {
		log.trace("rollback called for xid=[{}]", xid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
	 */
	public boolean setTransactionTimeout(final int seconds) throws XAException {
		log.trace("setTransactionTimeout called with seconds=[{}]", seconds);
		this.txTimeout = seconds;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid, int)
	 */
	public void start(final Xid xid, final int flags) throws XAException {
		log.trace("start called for xid=[{}] and flags=[{}]", new Object[] {
				xid, flags });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		log.trace("getLocalTransaction called, returning this");
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#begin()
	 */
	public void begin() throws ResourceException {
		log.trace("begin method called...");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#commit()
	 */
	public void commit() throws ResourceException {
		log.trace("commit method called...");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#rollback()
	 */
	public void rollback() throws ResourceException {
		log.trace("rollback method called...");

	}

}
