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
package com.ericsson.oss.mediation.test.smoke.testclasses;

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.oss.mediation.test.IntegrationTestDeploymentFactory;
import com.ericsson.oss.mediation.test.smoke.dependencies.SmokeTestDependencies;

@RunWith(Arquillian.class)
public class DeploymentTest {

	/**
	 * Since we want different scenarios, we will control arq deployment
	 * manually
	 * 
	 */
	@ArquillianResource
	private ContainerController controller;

	@ArquillianResource
	private Deployer deployer;

	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "camel-engine-rar", testable = false, managed = false)
	public static Archive<?> depoloyModelService() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_ERICSSON_OSS_MEDIATION_TRANSACT_JCA);
	}

	/**
	 * Start executing tests
	 */

	@Test
	@InSequence(1)
	@OperateOnDeployment("camel-engine-rar")
	public void testDeployModelService() throws Exception {
		this.deployer.deploy("camel-engine-rar");
	}

	@Test
	@InSequence(2)
	@OperateOnDeployment("camel-engine-rar")
	public void deployMediationCore() {
		this.deployer.undeploy("camel-engine-rar");
	}

}
