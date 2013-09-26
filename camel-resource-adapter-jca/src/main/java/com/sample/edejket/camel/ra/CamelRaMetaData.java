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

import javax.resource.cci.ResourceAdapterMetaData;

/**
 * CamelRaMetaData
 * 
 * @version $Revision: $
 */
public class CamelRaMetaData implements ResourceAdapterMetaData {
	/**
	 * Default constructor
	 */
	public CamelRaMetaData() {

	}

	/**
	 * Gets the version of the resource adapter.
	 * 
	 * @return String representing version of the resource adapter
	 */
	@Override
	public String getAdapterVersion() {
		return "0.0.1-SNAPSHOT";
	}

	/**
	 * Gets the name of the vendor that has provided the resource adapter.
	 * 
	 * @return String representing name of the vendor
	 */
	@Override
	public String getAdapterVendorName() {
		return "crazy serbian software limited";
	}

	/**
	 * Gets a tool displayable name of the resource adapter.
	 * 
	 * @return String representing the name of the resource adapter
	 */
	@Override
	public String getAdapterName() {
		return "camel rar wannabe";
	}

	/**
	 * Gets a tool displayable short desription of the resource adapter.
	 * 
	 * @return String describing the resource adapter
	 */
	@Override
	public String getAdapterShortDescription() {
		return "When i grow up i will be rar";
	}

	/**
	 * Returns a string representation of the version
	 * 
	 * @return String representing the supported version of the connector
	 *         architecture
	 */
	@Override
	public String getSpecVersion() {
		return "1.6";
	}

	/**
	 * Returns an array of fully-qualified names of InteractionSpec
	 * 
	 * @return Array of fully-qualified class names of InteractionSpec classes
	 */
	@Override
	public String[] getInteractionSpecsSupported() {
		return null; // TODO
	}

	/**
	 * Returns true if the implementation class for the Interaction
	 * 
	 * @return boolean Depending on method support
	 */
	@Override
	public boolean supportsExecuteWithInputAndOutputRecord() {
		return false; // TODO
	}

	/**
	 * Returns true if the implementation class for the Interaction
	 * 
	 * @return boolean Depending on method support
	 */
	@Override
	public boolean supportsExecuteWithInputRecordOnly() {
		return false; // TODO
	}

	/**
	 * Returns true if the resource adapter implements the LocalTransaction
	 * 
	 * @return true If resource adapter supports resource manager local
	 *         transaction demarcation
	 */
	@Override
	public boolean supportsLocalTransactionDemarcation() {
		return false;
	}

}