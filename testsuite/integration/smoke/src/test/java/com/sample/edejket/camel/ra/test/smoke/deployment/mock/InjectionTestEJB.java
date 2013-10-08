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

import javax.ejb.Local;

@Local
public interface InjectionTestEJB {

	void invokeRarMethodUnderTransaction();

	void invokeRarMethodCauseRollback();

	void loadCustomTestComponent();
}
