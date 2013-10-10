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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Build a route using given route definition in given camel context
 * 
 * @author edejket
 * 
 */
public class RouteBuildRequest extends CamelRequest {

	private static final Logger log = LoggerFactory
			.getLogger(RouteBuildRequest.class);

	private final String routeDef;
	private final CamelRouteBuilder routeBuilder;

	/**
	 * RouteBuildRequest constructor
	 * 
	 * @param input
	 *            route definition
	 * @param ctx
	 *            CamelContext
	 */
	public RouteBuildRequest(final String routeDef, final CamelContext ctx) {
		super(ctx);
		this.routeDef = routeDef;
		this.routeBuilder = new CamelRouteBuilder(this.routeDef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sample.edejket.camel.ra.CamelRequest#processRequest(java.lang.String)
	 */
	@Override
	public void processRequest() throws ResourceException {
		try {
			this.ctx.addRoutes(this.routeBuilder);
			log.trace("Created route will have id=[{}]",
					this.routeBuilder.getRouteId());
		} catch (Exception e) {
			throw new ResourceException(e);
		}

	}

	/**
	 * Get id of this route
	 * 
	 * @return id of the route
	 */
	public final String getRouteId() {
		return this.routeBuilder.getRouteId();
	}

}
