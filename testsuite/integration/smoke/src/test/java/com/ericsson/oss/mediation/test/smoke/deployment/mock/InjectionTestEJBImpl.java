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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ericsson.oss.mediation.test.smoke.deployment.mock.InjectionTestEJB
	 * #invokeRarMethodUnderTransaction()
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void invokeRarMethodUnderTransaction() {
		log.trace("<------------------Called invokeRarMethodUnderTransaction() method--------------->");
		try {
			DataFlow flow = dfContext.getDataFlowImplementation();
			flow.processInput("Good input");
			flow.close();
			log.trace("Exiting invokeRarMethodUnderTransaction() method");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ericsson.oss.mediation.test.smoke.deployment.mock.InjectionTestEJB
	 * #invokeRarMethodCauseRollback()
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void invokeRarMethodCauseRollback() {

		DataFlow flow = null;
		log.trace("<------------------Called invokeRarMethodCauseRollback() method--------------->");
		try {
			flow = dfContext.getDataFlowImplementation();
			flow.processInput("bad input");
			log.trace("Exiting invokeRarMethodCauseRollback() method");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);

		}
		throw new RuntimeException("Rollback transaction please...");

	}
}
