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

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public class RouteBuildRequest extends CamelRequest {

	protected String routeId;
	protected String routeDef;

	/**
	 * @param input
	 * @param ctx
	 */
	public RouteBuildRequest(final String routeDef, final CamelContext ctx) {
		super(ctx);
		this.routeDef = routeDef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sample.edejket.camel.ra.CamelRequest#processRequest(java.lang.String)
	 */
	@Override
	public void processRequest() throws ResourceException {
		CamelRouteBuilder routeBuilder = new CamelRouteBuilder();
		final RouteBuilder builder = routeBuilder.buildCamelRoute(routeDef);
		this.routeId = routeBuilder.getRouteId();
		try {

			this.ctx.addRoutes(builder);
		} catch (Exception e) {
			throw new ResourceException(e);
		}

	}

	public final String getRouteId() {
		return routeId;
	}

}
