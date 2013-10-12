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
package com.sample.edejket.camel.ra.test.smoke.deployment.mock;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.edejket.camel.ra.DataFlow;
import com.sample.edejket.camel.ra.DataFlowContextFactory;

@Stateless
public class InjectionTestEJBImpl implements InjectionTestEJB {

	private static final Logger log = LoggerFactory
			.getLogger(InjectionTestEJBImpl.class);

	@Resource(lookup = "java:/eis/CamelContextFactory")
	private DataFlowContextFactory dfContext;

	public static final String validRouteDef = "from(direct:customComponentRoute).to(file://test).autoStartup(true).setId(routeName)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJB
	 * #buildFlow(java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void buildFlow(final String flowDefinition) {

		DataFlow flow = null;
		log.trace("<------------------Called buildFlow() method--------------->");
		try {
			flow = dfContext.getDataFlowImplementation();
			final String flowId = flow.createDataFlow(flowDefinition);
			log.trace("Exiting buildFlow() method, result=[{}]", flowId);
			System.out.println("Exiting buildFlow() method, result=" + flowId);
		} catch (ResourceException re) {
			log.error("Caught exception during build flow  test:", re);
			throw new RuntimeException(re);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJB
	 * #buildFlowAndInvokeFlow(java.lang.String, java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void buildFlowAndInvokeFlow(final String flowDefinition,
			final String input) throws ResourceException {
		DataFlow flow = null;
		log.trace("<------------------Called buildFlowAndInvokeFlow() method--------------->");
		flow = dfContext.getDataFlowImplementation();
		final String flowId = flow.createDataFlowAndApplyInput(flowDefinition,
				input);
		log.trace("Exiting buildFlowAndInvokeFlow() method, result=[{}]",
				flowId);

	}

}
