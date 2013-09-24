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

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.oss.mediation.test.IntegrationTestDeploymentFactory;
import com.ericsson.oss.mediation.test.smoke.dependencies.SmokeTestDependencies;
import com.ericsson.oss.mediation.test.smoke.deployment.SmokeTestDeploymentFactory;
import com.ericsson.oss.mediation.test.smoke.deployment.mock.InjectionTestEJB;

@RunWith(Arquillian.class)
public class EjbInjectionTest {

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

	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "camel-engine-rar-deployment", managed = false, testable = false)
	public static Archive<?> createResourceAdapter() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_ERICSSON_OSS_MEDIATION_TRANSACT_JCA);
	}

	/**
	 * Create war deployment
	 * 
	 * @return war deployment
	 */
	@Deployment(name = "war-with-ejb", managed = false, testable = true)
	public static Archive<?> createWarWithEjb() {
		return SmokeTestDeploymentFactory.createWarTestDeployment();
	}

	/**
	 * Start executing tests
	 */
	@Test
	@InSequence(1)
	@OperateOnDeployment("camel-engine-rar-deployment")
	public void testDeployModelService() throws Exception {
		this.deployer.deploy("camel-engine-rar-deployment");
	}

	@Test
	@InSequence(2)
	@OperateOnDeployment("war-with-ejb")
	public void deployWarWithEjb() throws Exception {
		this.deployer.deploy("war-with-ejb");
	}

	@Test
	@InSequence(3)
	@OperateOnDeployment("war-with-ejb")
	public void testInjectedRarUnderTransaction() throws Exception {
		this.injectedEjb.invokeRarMethodUnderLocalTransaction();
	}

	@Ignore
	// is fucking up the test, due to some arq crap, TODO:See what's this all
	// about
	@Test
	@InSequence(4)
	@OperateOnDeployment("war-with-ejb")
	public void undeployWarWithEjb() throws Exception {
		this.deployer.undeploy("war-with-ejb");
	}

	@Test
	@InSequence(5)
	@OperateOnDeployment("camel-engine-rar-deployment")
	public void deployMediationCore() {
		this.deployer.undeploy("camel-engine-rar-deployment");
	}
}
