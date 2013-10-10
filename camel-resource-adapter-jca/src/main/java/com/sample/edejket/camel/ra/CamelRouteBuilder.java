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

import java.util.*;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class CamelRouteBuilder {

	private String routeId;

	public RouteBuilder buildCamelRoute(final String input) {

		final RouteBuilder camelRouteBuilder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				/**
				 * from(direct:customComponentRoute)
				 * .to(customComp://someCustomComponent)
				 * .autoStartup(true).setId(routeName);
				 */
				Collection<String> splitOnDot = splitOnDot(input);
				final RouteDefinition routeDef = from(processFromPart(splitOnDot));
				final Collection<String> toParts = processToParts(splitOnDot);
				for (String to : toParts) {
					routeDef.to(to);
				}
				final String routeName = processRouteId(splitOnDot);
				routeDef.setId(routeName);
				setRouteId(routeName);
				routeDef.setAutoStartup("true");
			}
		};
		return camelRouteBuilder;
	}

	protected String processFromPart(final Collection<String> splitted) {
		final String fromPart = findFromPart(splitted);
		final int start = fromPart.indexOf("(");
		final int end = fromPart.indexOf(")");
		if (start == -1 || end == -1) {
			throw new IllegalArgumentException(
					"from section is not in form from(...)");
		}
		if (end <= start) {
			throw new IllegalArgumentException(
					"from section is not in form from(...)");
		}
		return fromPart.substring(start + 1, end);
	}

	protected Collection<String> splitOnDot(final String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException(
					"Route definition can not be empty");
		}
		if (!input.contains(".")) {
			throw new IllegalArgumentException(
					"Invalid format of the route definition.");
		}
		String[] split = input.split("\\.");
		return Arrays.asList(split);
	}

	protected String findFromPart(final Collection<String> splitted) {
		for (String string : splitted) {
			if (string.contains("from")) {
				return string;
			}
		}
		throw new IllegalArgumentException(
				"Given route definition does not contain from section");
	}

	protected Collection<String> findToPart(final Collection<String> splitted) {
		Collection<String> toCollection = new ArrayList<>();
		for (String line : splitted) {
			if (line.contains("to")) {
				toCollection.add(line);
			}
		}
		if (toCollection.isEmpty()) {
			throw new IllegalArgumentException(
					"no .to found in route definition");
		}
		return toCollection;
	}

	protected Collection<String> processToParts(
			final Collection<String> splitted) {
		final Collection<String> toParts = findToPart(splitted);
		final Collection<String> processedToParts = new ArrayList<>();
		for (String line : toParts) {
			processedToParts.add(processSingleTo(line));
		}
		if (processedToParts.isEmpty()) {
			throw new IllegalArgumentException(
					"Given route has to have at least one to section");
		}
		return processedToParts;
	}

	protected String processSingleTo(final String line) {
		final int start = line.indexOf("(");
		final int end = line.indexOf(")");
		if (start == -1 || end == -1) {
			throw new IllegalArgumentException(
					"to section is not in form to(...)");
		}
		if (end <= start) {
			throw new IllegalArgumentException(
					"from section is not in form to(...)");
		}
		return line.substring(start + 1, end);
	}

	protected String processRouteId(final Collection<String> splitted) {
		final String routeIdLine = findRouteIdLine(splitted);
		return processRouteIdValue(routeIdLine);
	}

	protected String findRouteIdLine(final Collection<String> splitted) {
		for (String input : splitted) {
			if (input.startsWith("setId")) {
				return input;
			}
		}
		throw new IllegalArgumentException(
				"Route id not found in route definition");
	}

	protected String processRouteIdValue(final String line) {
		final int start = line.indexOf("(");
		final int end = line.indexOf(")");
		if (start == -1 || end == -1) {
			throw new IllegalArgumentException(
					"setId section is not in form setId(...)");
		}
		if (end <= start) {
			throw new IllegalArgumentException(
					"setId section is not in form setId(...)");
		}
		return line.substring(start + 1, end);
	}

	/**
	 * @return the routeId
	 */
	protected String getRouteId() {
		return routeId;
	}

	/**
	 * @param routeId
	 *            the routeId to set
	 */
	protected void setRouteId(final String routeId) {
		this.routeId = routeId;
	}

}
