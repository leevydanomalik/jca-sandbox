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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

public class SampleCamelComponentTest {

	private RouteBuilder buildTestRoute() {
		RouteBuilder b = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:customRoute")
						.to("SampleCamelComponent://someCustomComponent")
						.autoStartup("true").setId("testRoute");
			}
		};
		return b;
	}

	@Test
	public void testRouteWithComponent() throws Exception {
		CamelContext ctx = new DefaultCamelContext();
		SampleCamelComponent comp = new SampleCamelComponent();
		comp.setCamelContext(ctx);
		ctx.addComponent("SampleCamelComponent", comp);
		ctx.start();
		ctx.addRoutes(buildTestRoute());

		Route route = ctx.getRoute("testRoute");
		Endpoint ep = route.getEndpoint();
		Producer p = ep.createProducer();
		Exchange e = p.createExchange();
		e.getIn().setBody("sldsk");
		p.process(e);

	}
}
