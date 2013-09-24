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
package com.ericsson.oss.mediation.test.smoke.deployment.mock;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.camel.ra.DataFlow;
import com.ericsson.oss.mediation.camel.ra.DataFlowContextFactory;

@Stateless
public class InjectionTestEJBImpl implements InjectionTestEJB {

	private static final Logger log = LoggerFactory
			.getLogger(InjectionTestEJBImpl.class);

	@Resource(lookup = "java:/eis/CamelContextFactory")
	private DataFlowContextFactory dfContext;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void invokeRarMethodUnderLocalTransaction() {
		log.debug("Called invokeRarMethodUnderLocalTransaction() method");
		System.out
				.println("<------------------Called invokeRarMethodUnderLocalTransaction() method--------------->");
		try {
			DataFlow flow = dfContext.getDataFlowImplementation();
			flow.processInput("someTestInput");
			flow.close();
			log.debug("Exiting invokeRarMethodUnderLocalTransaction() method");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);
		}
	}
}
