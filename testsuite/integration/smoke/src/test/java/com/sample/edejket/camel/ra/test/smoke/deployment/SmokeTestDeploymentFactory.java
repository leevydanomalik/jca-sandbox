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
package com.sample.edejket.camel.ra.test.smoke.deployment;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.sample.edejket.camel.ra.test.IntegrationTestDeploymentFactory;
import com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJB;
import com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJBImpl;
import com.sample.edejket.camel.ra.test.smoke.testclasses.CommitTransactionTest;

public class SmokeTestDeploymentFactory extends
		IntegrationTestDeploymentFactory {

	/**
	 * Test archive with single ejb that injects and uses rar
	 * 
	 * @return
	 */
	public static Archive<?> createWarTestDeployment() {

		final WebArchive war = createWarDeployment("EjbInjectionTest.war");
		war.addClass(InjectionTestEJBImpl.class);
		war.addClass(InjectionTestEJB.class);
		war.addClass(CommitTransactionTest.class);
		war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		war.addAsManifestResource("rar-module-manifest.mf", "MANIFEST.MF");
		return war;

	}

}
