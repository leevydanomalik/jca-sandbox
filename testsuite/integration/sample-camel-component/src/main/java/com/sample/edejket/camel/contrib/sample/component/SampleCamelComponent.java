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

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link SampleComponentEndpoint}.
 */
public class SampleCamelComponent extends DefaultComponent {

	private static final Logger log = LoggerFactory
			.getLogger(SampleCamelComponent.class);

	@Override
	protected Endpoint createEndpoint(final String uri, final String remaining,
			final Map<String, Object> parameters) throws IllegalStateException {
		log.debug("createEndpoint method called...");
		final Endpoint endpoint = new SampleComponentEndpoint(uri, this);
		try {
			setProperties(endpoint, parameters);
			return endpoint;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
