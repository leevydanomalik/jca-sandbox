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

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of endpoint for our {@link SampleComponentComponent}
 * 
 * @author edejket
 * 
 */
public class SampleComponentEndpoint extends DefaultEndpoint {

	public static final Logger LOG = LoggerFactory
			.getLogger(SampleComponentEndpoint.class);

	/**
	 * Default constructor
	 * 
	 */
	public SampleComponentEndpoint() {
		super();
		LOG.debug("SampleComponentEndpoint default constructor called ...");
	}

	/**
	 * Constructor with uri and component thats creating this endpoint
	 * 
	 * @param uri
	 * @param component
	 */
	public SampleComponentEndpoint(final String uri,
			final SampleCamelComponent component) {
		super(uri, component);
		LOG.debug("SampleComponentEndpoint constructor called with {} ...",
				new Object[] { uri, component });

	}

	@Override
	public Producer createProducer() throws IllegalStateException {
		return new SampleComponentProducer(this);
	}

	@Override
	public Consumer createConsumer(final Processor processor)
			throws UnsupportedOperationException {
		return null;
	}

	@Override
	public void stop() throws IllegalStateException {
		try {
			super.stop();
			LOG.debug("Endpoint stop method called");
		} catch (Exception e) {
			LOG.error("Error while trying to stop endpoint, stacktrace:", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void start() throws IllegalStateException {
		try {
			super.start();
			LOG.debug("Endpoint start method called");
		} catch (Exception e) {
			LOG.error("Error while trying to start endpoint, stacktrace:", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
