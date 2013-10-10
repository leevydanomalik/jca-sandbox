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

import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Build a route and apply input on it
 * 
 * @author edejket
 * 
 */
public class RouteBuildAndRunRequest extends RouteBuildRequest {

	private final Object input;

	private static final Logger log = LoggerFactory
			.getLogger(RouteBuildAndRunRequest.class);

	/**
	 * Constructor taking route definition, input object and camel context
	 * 
	 * @param routeDef
	 *            Definition for this route
	 * @param input
	 *            Input object to pass in first exchange
	 * @param ctx
	 *            CamelContext in which route will be created/looked up
	 */
	public RouteBuildAndRunRequest(final String routeDef, final Object input,
			CamelContext ctx) {
		super(routeDef, ctx);
		this.input = input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sample.edejket.camel.ra.RouteBuildRequest#processRequest(java.lang
	 * .String)
	 */
	@Override
	public void processRequest() throws ResourceException {
		try {
			super.processRequest();
			final Route route = ctx.getRoute(getRouteId());
			if (route == null) {
				throw new Exception("Unable to find route " + getRouteId());
			}
			final Endpoint endpoint = route.getEndpoint();
			final Exchange ex = endpoint.createExchange();
			ex.getIn().setBody(input);
			endpoint.createProducer().process(ex);
		} catch (Exception e) {
			log.error("Error processing request:", e);
			throw new ResourceException(e);
		}
	}

}
