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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.edejket.camel.ra.test.IntegrationTestDeploymentFactory;
import com.sample.edejket.camel.ra.test.smoke.dependencies.SmokeTestDependencies;
import com.sample.edejket.camel.ra.test.smoke.deployment.SmokeTestDeploymentFactory;
import com.sample.edejket.camel.ra.test.smoke.deployment.mock.InjectionTestEJB;

/**
 * Successfull commit test case
 * 
 * @author edejket
 * 
 */
@RunWith(Arquillian.class)
public class CommitTransactionTest {

	/**
	 * 
	 */
	private static final String VALID_INPUT = "Some nice input";

	/**
	 * 
	 */
	private static final String WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME = "war-with-ejb-commit";

	/**
	 * 
	 */
	private static final String CAMEL_ENGINE_RAR_DEPLOYMENT_NAME = "camel-engine-rar-deployment-commit";

	/**
	 * 
	 */
	private static final String contribCompRouteDef = "from(direct:customComponentRoute).to(SampleCamelComponent://someCustomComponent).autoStartup(true).setId(abcde)";

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
			.getLogger(CommitTransactionTest.class);

	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = CAMEL_ENGINE_RAR_DEPLOYMENT_NAME, managed = false, testable = false)
	public static Archive<?> createResourceAdapter() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_SAMPLE_EDEJKET_CAMEL_RA);
	}

	/**
	 * Create war deployment containing this test case and simple ejb used to
	 * triger transactional call to rar
	 * 
	 * @return war deployment
	 */
	@Deployment(name = WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME, managed = false, testable = true)
	public static Archive<?> createWarWithEjb() {
		return SmokeTestDeploymentFactory.createWarTestDeployment();
	}

	/**
	 * Start executing tests, deploy rar
	 */
	@Test
	@InSequence(1)
	@OperateOnDeployment(CAMEL_ENGINE_RAR_DEPLOYMENT_NAME)
	public void testDeployRar() throws Exception {
		log.info("<-----------Commit transaction test case, deploying rar-------------->");
		this.deployer.deploy(CAMEL_ENGINE_RAR_DEPLOYMENT_NAME);
	}

	@Test
	@InSequence(2)
	@OperateOnDeployment(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME)
	public void deployWarWithEjb() throws Exception {
		log.info("<-----------Commit transaction test case, deploying test.war-------------->");
		this.deployer.deploy(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME);
	}

	@Test
	@InSequence(3)
	@OperateOnDeployment(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME)
	public void testBuildRouteUnderTxAndCommit_WhenRouteDefValid()
			throws Exception {
		log.info(
				"<-----------Commit transaction test case, invoking buildFlow({})-------------->",
				contribCompRouteDef);
		this.injectedEjb.buildFlow(contribCompRouteDef);
	}

	@Test
	@InSequence(4)
	@OperateOnDeployment(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME)
	public void testBuildRouteUnderTxAndCommit_WhenRouteDefValid_WhenInputValid()
			throws Exception {
		log.info(
				"<-----------Commit transaction test case, invoking buildFlowAndInvokeFlow({},{})-------------->",
				contribCompRouteDef, VALID_INPUT);
		this.injectedEjb.buildFlowAndInvokeFlow(contribCompRouteDef,
				VALID_INPUT);
	}

	@Ignore
	@Test
	@InSequence(5)
	@OperateOnDeployment(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME)
	public void undeployWarWithEjb() throws Exception {
		log.info("<-----------Commit transaction test case, undeploy test.war-------------->");
		this.deployer.undeploy(WAR_WITH_EJB_COMMIT_DEPLOYMENT_NAME);
	}

	@Ignore
	@Test
	@InSequence(6)
	@OperateOnDeployment(CAMEL_ENGINE_RAR_DEPLOYMENT_NAME)
	public void undeployRar() {
		log.info("<-----------Commit transaction test case, undeploy rar-------------->");
		this.deployer.undeploy(CAMEL_ENGINE_RAR_DEPLOYMENT_NAME);
	}
}
