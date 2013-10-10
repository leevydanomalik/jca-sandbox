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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

public class CamelRouteBuilderTest {

	public static final String validRouteDef = "from(direct:customComponentRoute).to(customComp://someCustomComponent).autoStartup(true).setId(routeName)";

	@Test
	public void testSplitOnDot_ValidInput() {
		CamelRouteBuilder routeBuilder = new CamelRouteBuilder(validRouteDef);
		final Collection<String> actual = routeBuilder
				.splitOnDot(validRouteDef);
		final String[] _expected = new String[] {
				"from(direct:customComponentRoute)",
				"to(customComp://someCustomComponent)", "autoStartup(true)",
				"setId(routeName)" };
		final Collection<String> expected = Arrays.asList(_expected);
		Assert.assertTrue(expected.containsAll(actual));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitOnDot_WhenInInputIsNull() {
		CamelRouteBuilder routeBuilder = new CamelRouteBuilder(null);
		routeBuilder.splitOnDot(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitOnDot_WhenInInputIsEmpty() {
		CamelRouteBuilder routeBuilder = new CamelRouteBuilder("");
		routeBuilder.splitOnDot("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitOnDot_WhenInInputHasNoDots() {
		CamelRouteBuilder routeBuilder = new CamelRouteBuilder("abcde;efg");
		routeBuilder.splitOnDot("abcde;efg");
	}

}
