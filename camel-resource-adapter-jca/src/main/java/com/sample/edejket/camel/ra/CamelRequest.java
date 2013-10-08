/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.sample.edejket.camel.ra;

import javax.resource.ResourceException;
import javax.resource.spi.work.Work;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CamelRequest implements Work {

	private static final Logger log = LoggerFactory
			.getLogger(CamelRequest.class);

	protected CamelContext ctx;

	public CamelRequest(final CamelContext ctx) {
		log.trace("Creating instance of CamelRequest using camel context=[{}]",
				ctx);
		this.ctx = ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			processRequest();
		} catch (ResourceException re) {
			log.error("Unable to process request:", re);
			this.ctx = null;
			throw new RuntimeException(re);
		}

	}

	public abstract void processRequest() throws ResourceException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.work.Work#release()
	 */
	@Override
	public void release() {
		this.ctx = null;
	}

}
