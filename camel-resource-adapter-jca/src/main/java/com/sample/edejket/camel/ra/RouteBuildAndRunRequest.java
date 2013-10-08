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

public class RouteBuildAndRunRequest extends RouteBuildRequest {

	protected Object input;

	/**
	 * @param input
	 * @param ctx
	 */
	public RouteBuildAndRunRequest(String routeDef, final Object input,
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
			final Route route = super.ctx.getRoute(super.routeId);
			final Endpoint endpoint = route.getEndpoint();
			final Exchange ex = endpoint.createExchange();
			ex.getIn().setBody(input);
			endpoint.createProducer().process(ex);
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
