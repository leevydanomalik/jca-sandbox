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
package com.sample.edejket.camel.contrib.sample.component;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample component producer for our example component
 * {@link SampleComponentComponent}.
 * 
 * @author edejket
 * 
 */
public class SampleComponentProducer extends DefaultProducer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.impl.DefaultProducer#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

	private static final transient Logger log = LoggerFactory
			.getLogger(SampleComponentProducer.class);

	private final SampleComponentEndpoint endpoint;

	/**
	 * Producer constructor
	 * 
	 * @param endpoint
	 */
	public SampleComponentProducer(final SampleComponentEndpoint endpoint) {
		super(endpoint);
		this.endpoint = endpoint;
		log.trace("SampleComponentProducer constructor for endpoint {}",
				this.endpoint.getEndpointUri());
	}

	/**
	 * All the magic should happen in this method, we process the incoming
	 * exchange and pass the result to the next element of the route.
	 * 
	 * @param exchange
	 *            {@link Exchange}
	 * @throws Exception
	 */
	@Override
	public void process(final Exchange exchange) throws Exception {
		log.trace(
				"SimpleCamelComponent is now handling the incoming exchange with body {}...",
				exchange.getIn().getBody());
		if (exchange.getIn().getBody() == null) {
			throw new RuntimeException("Input is null - Rollback required...");
		} else {
			log.trace("Input is not null - Will be commited...");
		}
	}

}
