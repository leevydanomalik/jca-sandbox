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
package com.sample.edejket.camel.ra.test.smoke.testclasses;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.edejket.camel.ra.test.IntegrationTestDeploymentFactory;
import com.sample.edejket.camel.ra.test.smoke.dependencies.SmokeTestDependencies;
import com.sample.edejket.camel.ra.test.smoke.deployment.SmokeTestDeploymentFactory;
import com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJB;

@RunWith(Arquillian.class)
public class ContribComponentLoadingTest {

	/**
	 * Since we want different scenarios, we will control arq deployment
	 * manually
	 * 
	 */
	@ArquillianResource
	private ContainerController controller;

	@ArquillianResource
	private Deployer deployer;

	@EJB
	InjectionTestEJB injectedEjb;

	private static final Logger log = LoggerFactory
			.getLogger(ContribComponentLoadingTest.class);

	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "camel-engine-rar", testable = false, managed = false)
	public static Archive<?> depoloyModelService() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_SAMPLE_EDEJKET_CAMEL_RA);
	}

	/**
	 * Create war deployment containing this test case and simple ejb used to
	 * triger transactional call to rar
	 * 
	 * @return war deployment
	 */
	@Deployment(name = "war-with-ejb-commit", managed = false, testable = true)
	public static Archive<?> createWarWithEjb() {
		return SmokeTestDeploymentFactory.createWarTestDeployment();
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
	@OperateOnDeployment("war-with-ejb-commit")
	public void deployWarWithEjb() throws Exception {
		log.info("<-----------Contrib component test case, deploying test.war-------------->");
		this.deployer.deploy("war-with-ejb-commit");
	}

	@Test
	@InSequence(3)
	@OperateOnDeployment("war-with-ejb-commit")
	public void testLoadCustomComponent() throws Exception {
		log.info("<-----------invoking testLoadCustomComponent-------------->");
		this.injectedEjb.loadCustomTestComponent();
	}

	@Test
	@InSequence(4)
	@OperateOnDeployment("camel-engine-rar")
	public void deployMediationCore() {
		this.deployer.undeploy("camel-engine-rar");
	}

}
