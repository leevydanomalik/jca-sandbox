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

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataFlowImpl
 * 
 * @version $Revision: $
 */
public class DataFlowImpl implements DataFlow {
	/** The logger */
	private static Logger log = LoggerFactory.getLogger(DataFlowImpl.class
			.getName());

	/** ManagedConnection */
	private CamelManagedConnection mc;

	/** ManagedConnectionFactory */
	private CamelManagedConnectionFactory mcf;

	/**
	 * Default constructor
	 * 
	 * @param mc
	 *            CamelManagedConnection
	 * @param mcf
	 *            CamelManagedConnectionFactory
	 */
	public DataFlowImpl(final CamelManagedConnection mc,
			final CamelManagedConnectionFactory mcf) {
		this.mc = mc;
		this.mcf = mcf;
	}

	/**
	 * Close
	 */
	public void close() {
		mc.closeHandle(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ericsson.oss.mediation.camel.ra.DataFlow#processInput(java.lang.String
	 * )
	 */
	public void processInput(final String testInput) throws ResourceException {
		log.trace("<----------- called with {} -------------->", testInput);
		log.trace("mc:{}", mc);
		mc.processInput(testInput);
	}
}
