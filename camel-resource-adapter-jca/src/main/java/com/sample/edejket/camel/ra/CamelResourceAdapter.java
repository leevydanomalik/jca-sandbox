/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sample.edejket.camel.ra;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.util.jndi.JndiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * CamelResourceAdapter
 * 
 * @version $Revision: $
 */
@Connector(reauthenticationSupport = false, vendorName = "Ericsson", version = "0.0.1-SNAPSHOT", licenseRequired = false)
public class CamelResourceAdapter implements ResourceAdapter, java.io.Serializable {

    /** The serial version UID */
    private static final long serialVersionUID = 1L;

    /** The logger */
    private static Logger log = LoggerFactory.getLogger(CamelResourceAdapter.class);

    private TransactionSynchronizationRegistry txRegistry;

    private XATerminator xaTerminator;

    private CamelContext camelContext;

    private WorkManager workManager;

    private TransactionManager txMgr;

    /**
     * This is called during the activation of a message endpoint.
     * 
     * @param endpointFactory
     *            A message endpoint factory instance.
     * @param spec
     *            An activation spec JavaBean instance.
     * @throws ResourceException
     *             generic exception
     */
    @Override
    public void endpointActivation(final MessageEndpointFactory endpointFactory, final ActivationSpec spec) throws ResourceException {
        log.trace("endpointActivation({}, {})", new Object[] { endpointFactory, spec });

    }

    /**
     * This is called when a message endpoint is deactivated.
     * 
     * @param endpointFactory
     *            A message endpoint factory instance.
     * @param spec
     *            An activation spec JavaBean instance.
     */
    @Override
    public void endpointDeactivation(final MessageEndpointFactory endpointFactory, final ActivationSpec spec) {
        log.trace("endpointDeactivation({})", endpointFactory);

    }

    /**
     * This is called when a resource adapter instance is bootstrapped.
     * 
     * @param ctx
     *            A bootstrap context containing references
     * @throws ResourceAdapterInternalException
     *             indicates bootstrap failure.
     */
    @Override
    public void start(final BootstrapContext ctx) throws ResourceAdapterInternalException {
        log.trace("start()");
        this.txRegistry = ctx.getTransactionSynchronizationRegistry();
        this.xaTerminator = ctx.getXATerminator();
        this.workManager = ctx.getWorkManager();
        this.txMgr = getTxMgr();
        try {
            final JtaTransactionManager jtaMgr = new JtaTransactionManager(this.txMgr);
            final JndiContext jndiCtx = new JndiContext();
            configureCamelTransactions(jndiCtx, jtaMgr);
            this.camelContext = new DefaultCamelContext(jndiCtx);
            this.camelContext.setClassResolver(new JCAClassResolver(this.getClass().getClassLoader()));
            this.camelContext.start();
            this.camelContext.setClassResolver(new JCAClassResolver(this.getClass().getClassLoader()));
            //Let's use the patch and let camel find all META-INF/org/apache/camel/component/*
            //loadContribComponents(this.camelContext);
        } catch (final Exception ne) {
            log.error("Error while trying to start camel context:", ne);
            throw new ResourceAdapterInternalException(ne);
        }
    }

    private void configureCamelTransactions(final JndiContext ctx, final JtaTransactionManager jtaTxMgr) throws Exception {
        final SpringTransactionPolicy requiresPolicy = new SpringTransactionPolicy();
        requiresPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        requiresPolicy.setTransactionManager(jtaTxMgr);
        ctx.bind("PROPAGATION_REQUIRED", requiresPolicy);

        final SpringTransactionPolicy requiresNewPolicy = new SpringTransactionPolicy();
        requiresNewPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        requiresNewPolicy.setTransactionManager(jtaTxMgr);
        ctx.bind("PROPAGATION_REQUIRES_NEW", requiresNewPolicy);

    }

    private void loadContribComponents(final CamelContext ctx) {
        log.trace("Loading contrib components into camel context=[{}]", ctx);
        final ServiceLoader<Component> componentLoader = ServiceLoader.load(Component.class);
        final Iterator<Component> iter = componentLoader.iterator();
        while (iter.hasNext()) {
            final Component comp = iter.next();
            log.trace("Found contrib component=[{}]", comp.toString());
            comp.setCamelContext(ctx);
            ctx.addComponent(comp.getClass().getSimpleName(), comp);
            log.trace("Added contrib component {} using prefix {}", comp.toString(), comp.getClass().getSimpleName());
        }
    }

    private TransactionManager getTxMgr() {
        if (txMgr == null) {
            InitialContext context = null;
            try {
                context = new InitialContext();
                txMgr = (TransactionManager) context.lookup("java:jboss/TransactionManager");
            } catch (final NamingException e) {
                log.error("Cannot get transaction manager", e);
            }
        }
        return txMgr;
    }

    /**
     * This is called when a resource adapter instance is undeployed or during application server shutdown.
     */
    @Override
    public void stop() {
        log.trace("stopping camel context {}", this.camelContext);
        try {
            this.camelContext.stop();
            log.trace("Camel context stopped...");
        } catch (final Exception e) {
            log.error("Error while trying to stop camel context:", e);
        }

    }

    /**
     * This method is called by the application server during crash recovery.
     * 
     * @param specs
     *            An array of ActivationSpec JavaBeans
     * @throws ResourceException
     *             generic exception
     * @return An array of XAResource objects
     */
    @Override
    public XAResource[] getXAResources(final ActivationSpec[] specs) throws ResourceException {
        log.trace("getXAResources({})", specs.toString());
        return null;
    }

    /**
     * @return the txRegistry
     */
    public TransactionSynchronizationRegistry getTxRegistry() {
        return txRegistry;
    }

    /**
     * @return the xaTerminator
     */
    public XATerminator getXaTerminator() {
        return xaTerminator;
    }

    /**
     * Returns a hash code value for the object.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * result + 7;
        return result;
    }

    /**
     * Indicates whether some other object is equal to this one.
     * 
     * @param other
     *            The reference object with which to compare.
     * @return true if this object is the same as the obj argument, false otherwise.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof CamelResourceAdapter)) {
            return false;
        }
        final boolean result = true;
        return result;
    }

    /**
     * @param txRegistry
     *            the txRegistry to set
     */
    public void setTxRegistry(final TransactionSynchronizationRegistry txRegistry) {
        this.txRegistry = txRegistry;
    }

    /**
     * @param txMgr
     *            the txMgr to set
     */
    public void setTxMgr(final TransactionManager txMgr) {
        this.txMgr = txMgr;
    }

    /**
     * @return the camelContext
     */
    public CamelContext getCamelContext() {
        return camelContext;
    }

    /**
     * @param camelContext
     *            the camelContext to set
     */
    public void setCamelContext(final CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    /**
     * @return the workManager
     */
    public WorkManager getWorkManager() {
        return workManager;
    }

    /**
     * @param workManager
     *            the workManager to set
     */
    public void setWorkManager(final WorkManager workManager) {
        this.workManager = workManager;
    }

}
